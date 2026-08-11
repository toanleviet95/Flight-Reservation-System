package tripma.local.tripma.dto.PackageBooking;

public record PackageBookingRequest(
        Integer packageBookingId,
        Integer bookingId,
        Integer packageId) {
    
}
