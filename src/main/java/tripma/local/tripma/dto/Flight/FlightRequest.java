package tripma.local.tripma.dto.Flight;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightRequest(
        @NotBlank(message = "Flight number is required") String flightNumber,
        @NotNull(message = "Airline ID is required") Integer airlineId,
        @NotNull(message = "Departure airport ID is required") Integer departureAirportId,
        @NotNull(message = "Arrival airport ID is required") Integer arrivalAirportId,
        @NotNull(message = "Departure time is required") LocalDateTime departureTime,
        @NotNull(message = "Arrival time is required") LocalDateTime arrivalTime,
        @NotNull(message = "Base price is required") @Positive(message = "Base price must be greater than zero") BigDecimal basePrice,
        @NotNull(message = "Aircraft ID is required") Integer aircraftId) {

    @AssertTrue(message = "Arrival time must be after departure time")
    public boolean isArrivalTimeValid() {
        if (departureTime == null || arrivalTime == null) {
            return true;
        }
        return arrivalTime.isAfter(departureTime);
    }
}
