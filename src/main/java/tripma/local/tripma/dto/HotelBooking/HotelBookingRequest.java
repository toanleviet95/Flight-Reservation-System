package tripma.local.tripma.dto.HotelBooking;

import java.time.LocalDateTime;

public record HotelBookingRequest(
        Integer userId,
        Integer hotelId,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Integer guests,
        Double discountAmount) {
}