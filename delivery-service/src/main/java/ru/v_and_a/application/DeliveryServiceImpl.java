package ru.v_and_a.application;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.v_and_a.common.ResourceNotFoundException;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;
import ru.v_and_a.domain.model.Delivery;
import ru.v_and_a.domain.model.DeliveryStatus;
import ru.v_and_a.domain.repository.DeliveryRepository;
import ru.v_and_a.kafka.DeliveryEventProducer;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer deliveryEventProducer;

    @Override
    @Transactional
    public DeliveryResponse create(DeliveryRequest deliveryRequest) {
        Delivery delivery = deliveryRequestEntity(deliveryRequest);

        delivery =deliveryRepository.save(delivery);
        return toDeliveryResponse(delivery);
    }

    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public List<DeliveryResponse> getAll(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .stream()
                .map(this::toDeliveryResponse)
                .toList();
    }

    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public DeliveryResponse getById(Long deliveryId) {
        return toDeliveryResponse(getDelivery(deliveryId));
    }

    @Transactional
    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public DeliveryResponse update(Long deliveryId, DeliveryRequest deliveryRequest) {
        Delivery delivery = getDelivery(deliveryId);
        return toDeliveryResponse(delivery);
    }

    @Transactional
    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public void delete(Long deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) {
            throw new ResourceNotFoundException("Delivery with id " + deliveryId + " was not found");
        }
        deliveryRepository.deleteById(deliveryId);
    }

    private Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + deliveryId + " was not found"));
    }

    @Override
    public void createDeliveryByOrderUuid(String orderUuid) {
        var result = create(new DeliveryRequest(orderUuid, DeliveryStatus.CREATED));
        UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(
                result.getOrderUuid(),
                OrderStatus.CREATED,
                null
        );
        deliveryEventProducer.sendDeliveryCreatedEvent(event);
    }



    private DeliveryResponse toDeliveryResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrderUuid(),
                delivery.getStatus(),
                delivery.getTrackingNumber()
        );
    }
    private Delivery deliveryRequestEntity(DeliveryRequest request) {
        if (request == null) {
            return null;
        }

        return Delivery.builder()
                .orderUuid(request.getOrderUuid())
                .status(Optional.ofNullable(request.getStatus()).orElse(DeliveryStatus.CREATED))
                .deliveryDate(OffsetDateTime.now())
                .trackingNumber(getTrackingNumber())
                .build();
    }

    private String getTrackingNumber() {
        return "TRK-" + System.nanoTime();
    }
}
