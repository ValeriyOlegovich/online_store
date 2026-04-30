package ru.v_and_a.web.inspectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.v_and_a.domain.model.IdempotencyKey;
import ru.v_and_a.domain.repository.IdempotencyKeyRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // Работаем только с POST
        if (!request.getMethod().matches("POST")) {
            return true; // пропускаем
        }

        String key = request.getHeader("X-Idempotency-Key");
        if (key == null || key.isBlank()) {
            log.warn("Отсутствует заголовок X-Idempotency-Key");
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"X-Idempotency-Key header is required\"}");
            return false;
        }

        // Проверяем, был ли уже выполнен запрос с этим ключом
        Optional<IdempotencyKey> existing = idempotencyKeyRepository.findByKey(key);

        if (existing.isPresent()) {
            log.info("Повторный запрос с Idempotency-Key: {}. Возвращаем 200.", key);
            response.setStatus(200);
            return false;
        }

        // Ключа нет → сохраняем его и разрешаем выполнение

        IdempotencyKey newKey = new IdempotencyKey();
        newKey.setIdempotencyKey(key);
        newKey.setCreatedAt(LocalDateTime.now());
        idempotencyKeyRepository.save(newKey);

        // Оборачиваем response, чтобы потом прочитать тело
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        request.setAttribute("wrappedResponse", wrappedResponse);

        log.info("Idempotency-Key {} сохранён.", key);
        return true; // ✅ продолжаем обработку
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        if (ex != null) return; // ошибка → не сохраняем

        ContentCachingResponseWrapper wrapped = (ContentCachingResponseWrapper) request.getAttribute("wrappedResponse");
        if (wrapped == null) return;

        String key = request.getHeader("Idempotency-Key");
        Optional<IdempotencyKey> dbKeyOpt = idempotencyKeyRepository.findByKey(key);
        if (dbKeyOpt.isEmpty()) return;

        IdempotencyKey dbKey = dbKeyOpt.get();
        if (dbKey.getHttpStatus() != null) return; // уже сохранён

        // Читаем тело ответа
        byte[] bodyBytes = wrapped.getContentAsByteArray();
        String responseBody = StandardCharsets.UTF_8.decode(java.nio.ByteBuffer.wrap(bodyBytes)).toString();

        // Сохраняем статус и тело
        dbKey.setHttpStatus(wrapped.getStatus());
        dbKey.setResponsePayload(responseBody.isEmpty() ? "{}" : responseBody);
        idempotencyKeyRepository.save(dbKey);

        // Копируем содержимое обратно в оригинальный response
        try {
            wrapped.copyBodyToResponse();
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy cached response", e);
        }
    }
}