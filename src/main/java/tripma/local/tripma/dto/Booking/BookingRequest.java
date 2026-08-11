package tripma.local.tripma.dto.Booking;

public record BookingRequest(
    Integer userId,
    String bookingCode,
    String type,
    String status,
    String discountCode,
    Double discountAmount,
    Double totalPrice) {

}