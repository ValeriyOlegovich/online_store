package ru.v_and_a.web.mapper;

import org.springframework.stereotype.Component;
import ru.v_and_a.application.command.DeliveryCommand;
import ru.v_and_a.application.dto.DeliveryAddressDetails;
import ru.v_and_a.application.dto.DeliveryDetails;
import ru.v_and_a.application.dto.TimeWindowDetails;
import ru.v_and_a.web.dto.*;

@Component
public class DeliveryWebMapper {

    public DeliveryCommand toDeliveryCommand(DeliveryRequest deliveryRequest) {

        return new DeliveryCommand(
                deliveryRequest.getOrderId(),
                deliveryRequest.getStatus(),
                toDeliveryAddressDetails(deliveryRequest.getDeliveryAddress()),
                deliveryRequest.getDeliveryDate(),
                toTimeWindowDetails(deliveryRequest.getTimeWindow()),
                deliveryRequest.getTrackingNumber()
        );
    }

    public DeliveryResponse toDeliveryResponse(DeliveryDetails deliveryDetails) {
        return new DeliveryResponse(
                deliveryDetails.id(),
                deliveryDetails.orderId(),
                deliveryDetails.status(),
                toDeliveryAddressResponse(deliveryDetails.deliveryAddress()),
                deliveryDetails.deliveryDate(),
                toTimeWindowResponse(deliveryDetails.timeWindow()),
                deliveryDetails.trackingNumber(),
                null
        );
    }

    private DeliveryAddressDetails toDeliveryAddressDetails(DeliveryAddressRequest deliveryAddressRequest) {
        if (deliveryAddressRequest == null) {
            return null;
        }
        return new DeliveryAddressDetails(
                deliveryAddressRequest.getStreet(),
                deliveryAddressRequest.getCity(),
                deliveryAddressRequest.getApartment(),
                deliveryAddressRequest.getBuilding()
        );
    }

    private TimeWindowDetails toTimeWindowDetails(TimeWindowRequest timeWindowRequest) {
        if (timeWindowRequest == null) {
            return null;
        }
        return new TimeWindowDetails(timeWindowRequest.getStartTime(), timeWindowRequest.getEndTime());
    }

    private DeliveryAddressResponse toDeliveryAddressResponse(DeliveryAddressDetails deliveryAddressDetails) {
        return new DeliveryAddressResponse(
                deliveryAddressDetails.street(),
                deliveryAddressDetails.city(),
                deliveryAddressDetails.postalCode(),
                deliveryAddressDetails.country()
        );
    }

    private TimeWindowResponse toTimeWindowResponse(TimeWindowDetails timeWindowDetails) {
        return new TimeWindowResponse(timeWindowDetails.startTime(), timeWindowDetails.endTime());
    }
}
