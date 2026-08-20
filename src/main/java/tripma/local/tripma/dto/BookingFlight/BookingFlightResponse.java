package tripma.local.tripma.dto.BookingFlight;

import tripma.local.tripma.entity.BookingFlight;
import tripma.local.tripma.entity.Flight;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingFlightResponse(
        Integer bookingFlightId,
        Integer bookingId,
        Integer flightId,
        String direction,
        String cabinClass,
        BigDecimal price,
        String flightNumber,
        Integer airlineId,
        Integer departureAirportId,
        Integer arrivalAirportId,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal basePrice,
        Integer aircraftId) {
    public static BookingFlightResponse from(BookingFlight bookingFlight, Flight flight) {
        return new BookingFlightResponse(
                bookingFlight.getBookingFlightId(),
                bookingFlight.getBookingId(),
                bookingFlight.getFlightId(),
                bookingFlight.getDirection(),
                bookingFlight.getCabinClass(),
                bookingFlight.getPrice(),
                flight.getFlightNumber(),
                flight.getAirlineId(),
                flight.getDepartureAirportId(),
                flight.getArrivalAirportId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getBasePrice(),
                flight.getAircraftId());
    }
}