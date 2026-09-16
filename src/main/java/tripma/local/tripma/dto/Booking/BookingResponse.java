package tripma.local.tripma.dto.Booking;

import tripma.local.tripma.entity.Booking;
import tripma.local.tripma.dto.BookingFlight.BookingFlightResponse;

import java.math.BigDecimal;
import java.util.List;

public record BookingResponse(
        Integer bookingId,
        Integer userId,
        String bookingCode,
        String type,
        String status,
        String discountCode,
        BigDecimal discountAmount,
        BigDecimal totalPrice,
        List<BookingFlightResponse> flights) {

    /** Factory không có flights (dùng trong hotel booking). */
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getBookingCode(),
                booking.getType(),
                booking.getStatus(),
                booking.getDiscountCode(),
                booking.getDiscountAmount(),
                booking.getTotalPrice(),
                List.of());
    }

    /** Factory với danh sách flight legs. */
    public static BookingResponse from(Booking booking, List<BookingFlightResponse> flights) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getBookingCode(),
                booking.getType(),
                booking.getStatus(),
                booking.getDiscountCode(),
                booking.getDiscountAmount(),
                booking.getTotalPrice(),
                flights);
    }
}
