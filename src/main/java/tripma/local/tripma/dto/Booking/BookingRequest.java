package tripma.local.tripma.dto.Booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record BookingRequest(
        @NotNull Integer userId,
        @NotBlank String bookingCode,
        String discountCode,
        BigDecimal discountAmount,
        BigDecimal totalPrice,
        @NotEmpty @Valid List<FlightLegRequest> flights) {
}
