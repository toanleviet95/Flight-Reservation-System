package tripma.local.tripma.dto.Baggage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record BaggageRequest(
                @NotNull(message = "Passenger id is required")
                Integer passengerId,
                @NotNull(message = "Flight id is required")
                Integer flightId,
                @NotNull(message = "Quantity is required")
                @PositiveOrZero(message = "Quantity must be greater than or equal to 0")
                Integer quantity,
                @NotNull(message = "Fee is required")
                @PositiveOrZero(message = "Fee must be greater than or equal to 0")
                Double fee) {
}
