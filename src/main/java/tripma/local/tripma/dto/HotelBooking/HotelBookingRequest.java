package tripma.local.tripma.dto.HotelBooking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HotelBookingRequest(
        @NotNull Integer userId,
        @NotNull Integer hotelId,
        @NotNull LocalDate checkIn,
        @NotNull LocalDate checkOut,
        @NotNull @Positive Integer guests,
        BigDecimal discountAmount) {
}
