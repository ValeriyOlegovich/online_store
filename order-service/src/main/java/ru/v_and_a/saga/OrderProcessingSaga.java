package ru.v_and_a.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.v_and_a.core.dto.commands.ProcessPaymentCommand;
import ru.v_and_a.core.dto.commands.RejectPaymentCommand;
import ru.v_and_a.core.dto.commands.ScheduleDeliveryCommand;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;
import ru.v_and_a.domain.model.OrderSagaState;
import ru.v_and_a.domain.repository.OrderSagaStateRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingSaga {

    @Value("${kafka.topic.payment-request}")
    private String paymentRequestTopic;

    @Value("${kafka.topic.delivery-request}")
    private String deliveryRequestTopic;

    @Value("${kafka.topic.payment-cancel}")
    private String paymentCancelTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderSagaStateRepository stateRepository;

    public void processOrder(String orderUuid) {
        log.info("Создание заказа: " + orderUuid);
        // 1. Сохранить состояние саги
        OrderSagaState state = new OrderSagaState(orderUuid, OrderStatus.CREATED, null);
        stateRepository.save(state);

        // 2. Отправить команду на оплату
        kafkaTemplate.send(paymentRequestTopic, new ProcessPaymentCommand(orderUuid));
    }

    @KafkaListener(
            topics = "${kafka.topic.order-creation-status}",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUpdateOrderStatus(UpdateOrderStatusEvent event,
                                 @Nullable Acknowledgment acknowledgment) {
        log.info("Получено событие изменения статуса заказа: {}", event);

        try {
            // 1. Валидация входных данных
            if (event.orderUuid() == null) {
                log.error("Получено событие с пустым orderUuid: {}", event);
                if (acknowledgment != null) acknowledgment.acknowledge();
                return;
            }

            // 2. Поиск состояния саги
            OrderSagaState state = stateRepository.findById(event.orderUuid())
                    .orElseThrow(() -> new IllegalStateException(
                            "Состояние саги не найдено для заказа: " + event.orderUuid()));

            // 3. Проверка, не обработано ли уже событие (идемпотентность)
            if (state.getStatus() == event.status()) {
                log.info("Событие уже обработано (идемпотентность): orderUuid={}, status={}",
                        event.orderUuid(), event.status());
                if (acknowledgment != null) acknowledgment.acknowledge();
                return;
            }

            // 4. Обработка статуса
            switch (event.status()) {
                case PAID -> {
                    log.info("Заказ оплачен: orderUuid={}", event.orderUuid());
                    kafkaTemplate.send(deliveryRequestTopic, new ScheduleDeliveryCommand(event.orderUuid()))
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    log.info("Команда на доставку отправлена: orderUuid={}", event.orderUuid());
                                } else {
                                    log.error("Ошибка отправки команды на доставку", ex);
                                }
                            });
                }
                case DELIVERED -> {
                    log.info("Заказ доставлен и завершён: orderUuid={}", event.orderUuid());
                    // Можно отправить событие "OrderCompleted"
                }
                case REJECTED -> {
                    log.warn("Заказ отклонён: orderUuid={}, message={}", event.orderUuid(), event.message());
                    if (state.getStatus() == OrderStatus.PAID) {
                        log.info("Отправка команды на отмену оплаты: orderUuid={}", event.orderUuid());
                        kafkaTemplate.send(paymentCancelTopic, new RejectPaymentCommand(event.orderUuid(), event.message()))
                                .whenComplete((result, ex) -> {
                                    if (ex == null) {
                                        log.info("Команда на отмену оплаты отправлена: orderUuid={}", event.orderUuid());
                                    } else {
                                        log.error("Ошибка отправки команды на отмену оплаты", ex);
                                    }
                                });
                    } else {
                        log.info("Заказ отменён до оплаты: orderUuid={}", event.orderUuid());
                    }
                }
                default -> log.warn("Неизвестный статус: {}", event.status());
            }

            // 5. Обновление состояния саги (только после успешной логики)
            state.setStatus(event.status());
            stateRepository.save(state);

            log.info("Состояние саги обновлено: orderUuid={}, status={}", event.orderUuid(), event.status());

        } catch (Exception e) {
            log.error("Ошибка при обработке события UpdateOrderStatusEvent: {}", event, e);
            // Не ack'аем — чтобы сообщение попало в retry/DLT
            return;
        }

        // 6. Acknowledge только при успехе
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }
}

