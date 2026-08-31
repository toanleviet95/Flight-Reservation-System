package tripma.local.tripma.dto.Baggage;

public record BaggageRequest(
                Integer passengerId,
                Integer flightId,
                Integer quantity,
                Double fee) {
}
