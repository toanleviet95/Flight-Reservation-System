package tripma.local.tripma.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightRequest(
                String flightNumber,
                Integer airlineId,
                Integer departureAirportId,
                Integer arrivalAirportId,
                LocalDateTime departureTime,
                LocalDateTime arrivalTime,
                BigDecimal basePrice,
                Integer aircraftId) {
}
