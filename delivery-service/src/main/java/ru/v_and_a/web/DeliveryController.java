package ru.v_and_a.web;

import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.DeliveryServiceIml;
import ru.v_and_a.web.api.DeliveryApi;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;
import ru.v_and_a.web.mapper.DeliveryWebMapper;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryServiceIml deliveryServiceIml;
    private final DeliveryWebMapper deliveryWebMapper;

    @Override
    @CircuitBreaker(name = "delivery", fallbackMethod = "createFallback")
    public String create(@RequestBody DeliveryRequest deliveryRequest) {
        return deliveryServiceIml.create(deliveryWebMapper.toDeliveryCommand(deliveryRequest));
    }

    private String createFallback(DeliveryRequest deliveryRequest, Throwable throwable) {
        return "fallback-order-id-" + deliveryRequest.getOrderId();
    }

    @Override
    public List<DeliveryResponse> getAll(Pageable pageable) {
        return deliveryServiceIml.getAll(pageable)
                .stream()
                .map(deliveryWebMapper::toDeliveryResponse)
                .toList();
    }

    @Override
    public DeliveryResponse getById(@PathVariable("id") Long deliveryId) {
        return deliveryWebMapper.toDeliveryResponse(deliveryServiceIml.getById(deliveryId));
    }

    @Override
    public DeliveryResponse update(@PathVariable("id") Long deliveryId, @RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryServiceIml.update(deliveryId, deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    public void delete(@PathVariable("id") Long deliveryId) {
        deliveryServiceIml.delete(deliveryId);
    }
}
