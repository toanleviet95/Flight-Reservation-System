package tripma.local.tripma.dto.HotelBooking;

import tripma.local.tripma.entity.HotelBooking;
import java.math.BigDecimal;
import java.time.LocalDate;

public record HotelBookingResponse(
        Integer hotelBookingId,
        Integer bookingId,
        Integer hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer guests,
        BigDecimal totalPrice) {

    public static HotelBookingResponse fromEntity(HotelBooking hotelBooking) {
        return new HotelBookingResponse(
                hotelBooking.getHotelBookingId(),
                hotelBooking.getBookingId(),
                hotelBooking.getHotelId(),
                hotelBooking.getCheckIn(),
                hotelBooking.getCheckOut(),
                hotelBooking.getGuests(),
                hotelBooking.getTotalPrice());
    }
}
