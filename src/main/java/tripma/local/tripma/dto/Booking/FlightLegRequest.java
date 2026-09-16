package tripma.local.tripma.dto.Booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


public record FlightLegRequest(
        @NotNull Integer flightId,
        @NotNull @NotBlank String direction,
        @NotNull @NotBlank String cabinClass,
        @NotNull BigDecimal price) {
}
