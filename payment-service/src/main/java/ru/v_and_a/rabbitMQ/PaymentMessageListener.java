package ru.v_and_a.rabbitMQ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.v_and_a.application.PaymentService;
import ru.v_and_a.domain.model.PaymentStatus;
import ru.v_and_a.rabbitMQ.config.RabbitConfig;
import ru.v_and_a.rabbitMQ.events.OrderCancelledEvent;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitConfig.ORDER_CANCELLED_EVENT_QUEUE)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Получено событие: заказ отменён. orderUuid={}", event.orderUuid());

        // Проверяем, есть ли платёж по этому заказу
        Optional.ofNullable(paymentService.getPaymentByOrderUuid(event.orderUuid()))
                .ifPresentOrElse(
                        payment -> {
                            // Платёж найден → отменяем его
                            if (!PaymentStatus.CANCELLED.equals(payment.getStatus())) {
                                payment.setStatus(PaymentStatus.CANCELLED);
                                paymentService.cancel(payment.getId());
                                log.info("Платёж для заказа {} отменён", event.orderUuid());
                            } else {
                                log.debug("Платёж для заказа {} уже отменён", event.orderUuid());
                            }
                        },
                        () -> log.debug("Платёж для заказа {} не найден — ничего отменять", event.orderUuid())
                );
    }
}
