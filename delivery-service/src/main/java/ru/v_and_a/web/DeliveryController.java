package ru.v_and_a.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.v_and_a.application.DeliveryApplicationService;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;
import ru.v_and_a.web.mapper.DeliveryWebMapper;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryApplicationService deliveryApplicationService;
    private final DeliveryWebMapper deliveryWebMapper;

    @Override
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse create(@RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryApplicationService.create(deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    @GetMapping("/getAll")
    public List<DeliveryResponse> getAll() {
        return deliveryApplicationService.getAll()
                .stream()
                .map(deliveryWebMapper::toDeliveryResponse)
                .toList();
    }

    @Override
    @GetMapping("/getById/{id}")
    public DeliveryResponse getById(@PathVariable("id") Long deliveryId) {
        return deliveryWebMapper.toDeliveryResponse(deliveryApplicationService.getById(deliveryId));
    }

    @Override
    @PutMapping("/updateById/{id}")
    public DeliveryResponse update(@PathVariable("id") Long deliveryId, @RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryApplicationService.update(deliveryId, deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    @DeleteMapping("/deleteById/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long deliveryId) {
        deliveryApplicationService.delete(deliveryId);
    }
}
