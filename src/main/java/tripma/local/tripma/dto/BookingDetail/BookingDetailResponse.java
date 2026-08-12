package tripma.local.tripma.dto.BookingDetail;

import tripma.local.tripma.dto.Booking.BookingResponse;

public record BookingDetailResponse(
        BookingResponse booking,
        Object details) {
}
