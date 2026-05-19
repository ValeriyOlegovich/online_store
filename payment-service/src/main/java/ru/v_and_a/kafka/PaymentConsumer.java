package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.v_and_a.core.dto.commands.ProcessPaymentCommand;
import ru.v_and_a.core.dto.commands.RejectPaymentCommand;
import ru.v_and_a.core.dto.enums.OrderStatus;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentEventProducer paymentProducer;

    @KafkaListener(
            topics = "${kafka.topic.payment-request}",
            groupId="payment-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeProcessPaymentCommand(ProcessPaymentCommand command, Acknowledgment acknowledgment) {
        log.info("Получено событие оплаты заказа: " + command);
            if (command.orderUuid().equals("payment-error")) {
                paymentProducer.sendUpdateOrderStatusEvent(command.orderUuid(), OrderStatus.REJECTED, "Ошибка оплаты");
            } else {
                paymentProducer.sendUpdateOrderStatusEvent(command.orderUuid(), OrderStatus.PAID, null);
            }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topic.payment-cancel}",
            groupId="payment-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeRejectPaymentCommand (RejectPaymentCommand command, Acknowledgment acknowledgment) {
        log.info("Получено событие Отмены оплаты: " + command);
        paymentProducer.sendUpdateOrderStatusEvent(command.orderUuid(), OrderStatus.REJECTED, command.message());
        acknowledgment.acknowledge();
    }
}
