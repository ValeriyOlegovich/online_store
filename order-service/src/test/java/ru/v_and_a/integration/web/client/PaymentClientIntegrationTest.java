package ru.v_and_a.integration.web.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.v_and_a.web.client.PaymentClient;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        classes = ru.v_and_a.OrderServiceApplication.class,
        properties = {
                "payment.service.url=http://localhost:${wiremock.server.port}"
        }
)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentClientIntegrationTest {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private ThreadPoolBulkheadRegistry bulkheadRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    @AfterEach
    void resetWireMock() {
        WireMock.reset();
        WireMock.resetAllRequests();
    }

    @Test
    @DisplayName("Должен успешно создать платёж при 200 от сервиса")
    void shouldCreatePaymentSuccessfully() {
        stubFor(post("/api/v1/payments")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "status": "PAID",
                      "message": "Платёж успешно создан"
                    }
                    """)));

        var request = new PaymentRequest();
        request.setAmount(new BigDecimal(100));
        request.setCurrency("RUB");

        PaymentResponse response = paymentClient.createPayment("success-key", request);

        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getMessage()).contains("успешно создан");

        verify(1, postRequestedFor(urlEqualTo("/api/v1/payments")));
    }

    @Test
    @DisplayName("Retry Должен повторить 3 раза и вернуть PAYMENT_UNAVAILABLE при CONNECTION_RESET_BY_PEER")
    void shouldRetryThreeTimesWhenConnectionReset() {
        stubFor(post("/api/v1/payments")
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        var request = new PaymentRequest();
        request.setAmount(new BigDecimal(100));
        request.setCurrency("RUB");

        PaymentResponse response = paymentClient.createPayment("retry-key", request);

        assertThat(response.getStatus()).isEqualTo("PAYMENT_UNAVAILABLE");
        verify(3, postRequestedFor(urlEqualTo("/api/v1/payments")));
    }

    @Test
    @DisplayName("CircuitBreaker Должен перейти в состояние OPEN после нескольких ошибок")
    void shouldOpenCircuitAfterFailures() {
        stubFor(post("/api/v1/payments")
                .willReturn(aResponse().withStatus(500))); // Используем 500, чтобы CB учёл ошибку

        var request = new PaymentRequest();
        request.setAmount(new BigDecimal(100));
        request.setCurrency("RUB");

        // Делаем 3 вызова → должно хватить для OPEN
        for (int i = 0; i < 3; i++) {
            PaymentResponse resp = paymentClient.createPayment("cb-key-" + i, request);
        }

        // Ждём, пока CircuitBreaker перейдёт в OPEN
        await().atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("paymentClient");
                    assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
                });

        // Четвёртый вызов — не должен достигать сервиса
        WireMock.reset(); // Убеждаемся, что новых вызовов не будет

        PaymentResponse response = paymentClient.createPayment("cb-open", request);

        assertThat(response.getStatus()).isEqualTo("CIRCUIT_OPEN");
        assertThat(response.getMessage()).contains("Цепь разомкнута");

        verify(0, postRequestedFor(urlEqualTo("/api/v1/payments")));
        // Ждём, пока CircuitBreaker перейдёт в CLOSE
        await().atMost(Duration.ofSeconds(2));
    }

    @SneakyThrows
    @Test
    @DisplayName("Должен отклонять вызовы при превышении maxConcurrentCalls")
    void shouldRejectWhenBulkheadIsFull() throws InterruptedException {
        // Делаем долгий ответ, чтобы вызовы "зависли"
        stubFor(post("/api/v1/payments")
                .willReturn(aResponse()
                        .withFixedDelay(2000) // важно!
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "status": "PAID",
                      "message": "Платёж успешно создан"
                    }
                    """)));

        int totalRequests = 5;

        var request = new PaymentRequest();
        request.setAmount(new BigDecimal(100));
        request.setCurrency("RUB");

        ExecutorService executor = Executors.newFixedThreadPool(totalRequests);
        CompletableFuture<Void>[] futures = new CompletableFuture[totalRequests];

        for (int i = 0; i < totalRequests; i++) {
            int key = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                PaymentResponse resp = paymentClient.createPayment("bulk-key-" + key, request);
                // Один из ответов будет BULKHEAD_REJECTED, остальные — PAID
                assertThat(List.of("BULKHEAD_REJECTED", "PAID"))
                        .contains(resp.getStatus());
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();

        verify(3, postRequestedFor(urlEqualTo("/api/v1/payments")));
    }

    @SneakyThrows
    @Test
    @DisplayName("RateLimiter Должен блокировать запросы при превышении лимита")
    void shouldLimitRequestsByRate() {
        stubFor(post("/api/v1/payments")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "status": "PAID",
                      "message": "Платёж успешно создан"
                    }
                    """)));

        var request = new PaymentRequest();
        request.setAmount(new BigDecimal(100));
        request.setCurrency("RUB");

        int rateLimit = 5;
        // Первые 5 запроса — проходят
        for (int i = 0; i < rateLimit; i++) {
            PaymentResponse resp = paymentClient.createPayment("rate-key-" + i, request);
            assertThat(resp.getStatus()).isEqualTo("PAID");
        }

        // 6ой — должен быть отклонён
        PaymentResponse response = paymentClient.createPayment("rate-key-" + rateLimit, request);

        assertThat(response.getStatus()).isEqualTo("REJECTED");
        assertThat(response.getMessage()).contains("Запрос отклонён из-за ограничения ресурсов");
    }
}
