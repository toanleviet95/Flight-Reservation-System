package tripma.local.tripma.dto.Baggage;

import tripma.local.tripma.entity.Baggage;

public record BaggageResponse(
        Integer baggageId,
        Integer passengerId,
        Integer flightId,
        Integer quantity,
        Double fee) {
    public static BaggageResponse from(Baggage baggage) {
        return new BaggageResponse(
                baggage.getBaggageId(),
                baggage.getPassengerId(),
                baggage.getFlightId(),
                baggage.getQuantity(),
                baggage.getFee());
    }
}
