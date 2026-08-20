package tripma.local.tripma.dto.HotelBooking;

import tripma.local.tripma.entity.HotelBooking;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HotelBookingResponse(
        Integer hotel_booking_id,
        Integer booking_id,
        Integer hotel_id,
        LocalDateTime check_in,
        LocalDateTime check_out,
        Integer guests,
        BigDecimal total_price) {
    public static HotelBookingResponse fromEntity(HotelBooking hotelBooking) {
        return new HotelBookingResponse(
                hotelBooking.getHotel_booking_id(),
                hotelBooking.getBooking_id(),
                hotelBooking.getHotel_id(),
                hotelBooking.getCheck_in(),
                hotelBooking.getCheck_out(),
                hotelBooking.getGuests(),
                hotelBooking.getTotal_price());
    }
}