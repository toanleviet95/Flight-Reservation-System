package tripma.local.tripma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.BookingDetail.BookingDetailResponse;
import tripma.local.tripma.dto.BookingFlight.BookingFlightResponse;
import tripma.local.tripma.dto.HotelBooking.HotelBookingResponse;
import tripma.local.tripma.dto.PackageBooking.PackageBookingResponse;
import tripma.local.tripma.entity.Booking;
import tripma.local.tripma.repository.BookingRepository;
import tripma.local.tripma.service.BookingService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        Booking booking = bookingRepository.findById(2).orElse(null);
        if (booking != null) {
            booking.setStatus("PENDING");
            bookingRepository.save(booking);
        }
    }

    @Test
    void testConfirmBooking_HappyPath() {
        BookingResponse response = bookingService.confirmBooking(2);
        assertNotNull(response);
        assertEquals("CONFIRMED", response.status());

        Booking booking = bookingRepository.findById(2).orElseThrow();
        assertEquals("CONFIRMED", booking.getStatus());
    }

    @Test
    void testConfirmBooking_UnhappyPath_NotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.confirmBooking(9999);
        });
        assertTrue(exception.getMessage().contains("Không tìm thấy booking"));
    }

    @Test
    void testConfirmBooking_UnhappyPath_Cancelled() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.confirmBooking(4);
        });
        assertTrue(exception.getMessage().contains("Booking đã bị hủy"));
    }

    @Test
    void testCancelBooking_HappyPath() {
        BookingResponse response = bookingService.cancelBooking(2);
        assertNotNull(response);
        assertEquals("CANCELLED", response.status());

        Booking booking = bookingRepository.findById(2).orElseThrow();
        assertEquals("CANCELLED", booking.getStatus());
    }

    @Test
    void testCancelBooking_UnhappyPath_Cancelled() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(4);
        });
        assertTrue(exception.getMessage().contains("Booking đã bị hủy"));
    }

    @Test
    void testGetDetailBooking_Flight() {
        BookingDetailResponse response = bookingService.getDetailBooking(1);
        assertNotNull(response);
        assertEquals("flight", response.booking().type().toLowerCase());
        assertNotNull(response.details());
        assertTrue(response.details() instanceof BookingFlightResponse);
    }

    @Test
    void testGetDetailBooking_Hotel() {
        BookingDetailResponse response = bookingService.getDetailBooking(2);
        assertNotNull(response);
        assertEquals("hotel", response.booking().type().toLowerCase());
        assertNotNull(response.details());
        assertTrue(response.details() instanceof HotelBookingResponse);
    }

    @Test
    void testGetDetailBooking_Package() {
        BookingDetailResponse response = bookingService.getDetailBooking(3);
        assertNotNull(response);
        assertEquals("package", response.booking().type().toLowerCase());
        assertNotNull(response.details());
        assertTrue(response.details() instanceof PackageBookingResponse);
    }
}
