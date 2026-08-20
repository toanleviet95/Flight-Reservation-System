package tripma.local.tripma.dto.Booking;

import tripma.local.tripma.entity.Booking;

public record BookingResponse(
    Integer bookingId,
    Integer userId,
    String bookingCode,
    String type,
    String status,
    String discountCode,
    Double discountAmount,
    Double totalPrice
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
            booking.getBookingId(),
            booking.getUserId(),
            booking.getBookingCode(),
            booking.getType(),
            booking.getStatus(),
            booking.getDiscountCode(),
            booking.getDiscountAmount(),
            booking.getTotalPrice()
        );
    }
}