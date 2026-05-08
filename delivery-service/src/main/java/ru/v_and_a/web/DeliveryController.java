package ru.v_and_a.web;

import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.DeliveryServiceImpl;
import ru.v_and_a.web.api.DeliveryApi;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryServiceImpl deliveryServiceImpl;

    @Override
    @CircuitBreaker(name = "delivery", fallbackMethod = "createFallback")
    public DeliveryResponse create(@RequestBody DeliveryRequest deliveryRequest) {
        return deliveryServiceImpl.create(deliveryRequest);
    }

    private String createFallback(DeliveryRequest deliveryRequest, Throwable throwable) {
        return "fallback-order-uuid-" + deliveryRequest.getOrderUuid();
    }

    @Override
    public List<DeliveryResponse> getAll(Pageable pageable) {
        return deliveryServiceImpl.getAll(pageable);
    }

    @Override
    public DeliveryResponse getById(@PathVariable("id") Long deliveryId) {
        return deliveryServiceImpl.getById(deliveryId);
    }

    @Override
    public DeliveryResponse update(@PathVariable("id") Long deliveryId, @RequestBody DeliveryRequest deliveryRequest) {
        return deliveryServiceImpl.update(deliveryId, deliveryRequest);
    }

    @Override
    public void delete(@PathVariable("id") Long deliveryId) {
        deliveryServiceImpl.delete(deliveryId);
    }
}
