package ru.v_and_a.application;

import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.v_and_a.application.command.DeliveryCommand;
import ru.v_and_a.application.dto.DeliveryAddressDetails;
import ru.v_and_a.application.dto.DeliveryDetails;
import ru.v_and_a.application.dto.TimeWindowDetails;
import ru.v_and_a.common.ResourceNotFoundException;
import ru.v_and_a.domain.model.Delivery;
import ru.v_and_a.domain.model.DeliveryAddress;
import ru.v_and_a.domain.model.TimeWindow;
import ru.v_and_a.domain.repository.DeliveryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeliveryServiceIml implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    @Override
    public String create(DeliveryCommand deliveryCommand) {
        Delivery delivery = new Delivery(
                deliveryCommand.orderId(),
                deliveryCommand.status(),
                toDeliveryAddress(deliveryCommand.deliveryAddress()),
                deliveryCommand.deliveryDate(),
                toTimeWindow(deliveryCommand.timeWindow()),
                deliveryCommand.trackingNumber()
        );
        if (true) {
            throw new RuntimeException("Simulated failure for CircuitBreaker test");
        }
        deliveryRepository.save(delivery);
        return "Заказ создан";
    }

    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public List<DeliveryDetails> getAll(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .stream()
                .map(this::toDeliveryDetails)
                .toList();
    }

    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public DeliveryDetails getById(Long deliveryId) {
        return toDeliveryDetails(getDelivery(deliveryId));
    }

    @Transactional
    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    @Override
    public DeliveryDetails update(Long deliveryId, DeliveryCommand deliveryCommand) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.update(
                deliveryCommand.orderId(),
                deliveryCommand.status(),
                toDeliveryAddress(deliveryCommand.deliveryAddress()),
                deliveryCommand.deliveryDate(),
                toTimeWindow(deliveryCommand.timeWindow()),
                deliveryCommand.trackingNumber()
        );
        return toDeliveryDetails(delivery);
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

    private DeliveryAddress toDeliveryAddress(DeliveryAddressDetails deliveryAddressDetails) {
        if (deliveryAddressDetails == null) {
            throw new IllegalArgumentException("Delivery address must be provided");
        }
        return new DeliveryAddress(
                deliveryAddressDetails.street(),
                deliveryAddressDetails.city(),
                deliveryAddressDetails.postalCode(),
                deliveryAddressDetails.country()
        );
    }

    private TimeWindow toTimeWindow(TimeWindowDetails timeWindowDetails) {
        if (timeWindowDetails == null) {
            throw new IllegalArgumentException("Time window must be provided");
        }
        return new TimeWindow(timeWindowDetails.startTime(), timeWindowDetails.endTime());
    }

    private DeliveryDetails toDeliveryDetails(Delivery delivery) {
        return new DeliveryDetails(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getStatus(),
                new DeliveryAddressDetails(
                        delivery.getDeliveryAddress().getStreet(),
                        delivery.getDeliveryAddress().getCity(),
                        delivery.getDeliveryAddress().getPostalCode(),
                        delivery.getDeliveryAddress().getCountry()
                ),
                delivery.getDeliveryDate(),
                new TimeWindowDetails(
                        delivery.getTimeWindow().getStartTime(),
                        delivery.getTimeWindow().getEndTime()
                ),
                delivery.getTrackingNumber()
        );
    }

}
