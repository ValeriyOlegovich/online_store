package ru.v_and_a.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.DeliveryApplicationService;
import ru.v_and_a.web.api.DeliveryApi;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;
import ru.v_and_a.web.mapper.DeliveryWebMapper;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryApplicationService deliveryApplicationService;
    private final DeliveryWebMapper deliveryWebMapper;

    @Override
    public DeliveryResponse create(@RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryApplicationService.create(deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    public List<DeliveryResponse> getAll(Pageable pageable) {
        return deliveryApplicationService.getAll(pageable)
                .stream()
                .map(deliveryWebMapper::toDeliveryResponse)
                .toList();
    }

    @Override
    public DeliveryResponse getById(@PathVariable("id") Long deliveryId) {
        return deliveryWebMapper.toDeliveryResponse(deliveryApplicationService.getById(deliveryId));
    }

    @Override
    public DeliveryResponse update(@PathVariable("id") Long deliveryId, @RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryApplicationService.update(deliveryId, deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    public void delete(@PathVariable("id") Long deliveryId) {
        deliveryApplicationService.delete(deliveryId);
    }
}
