package tripma.local.tripma.dto.PackageBooking;

import tripma.local.tripma.entity.PackageBooking;

public record PackageBookingResponse(
        Integer packageBookingId,
        Integer bookingId,
        Integer packageId) {
    public static PackageBookingResponse fromEntity(PackageBooking packageBooking) {
        return new PackageBookingResponse(
                packageBooking.getPackage_booking_id(),
                packageBooking.getBooking_id(),
                packageBooking.getPackage_id());
    }
}
