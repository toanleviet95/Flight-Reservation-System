package tripma.local.tripma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.BookingDetail.BookingDetailResponse;
import tripma.local.tripma.dto.BookingFlight.BookingFlightResponse;
import tripma.local.tripma.dto.HotelBooking.HotelBookingResponse;
import tripma.local.tripma.dto.PackageBooking.PackageBookingResponse;
import tripma.local.tripma.entity.Booking;
import tripma.local.tripma.entity.BookingFlight;
import tripma.local.tripma.entity.Flight;
import tripma.local.tripma.entity.HotelBooking;
import tripma.local.tripma.entity.PackageBooking;
import tripma.local.tripma.repository.*;
import tripma.local.tripma.service.BookingService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @MockitoBean
    private BookingRepository bookingRepository;

    @MockitoBean
    private BookingFlightRepository bookingFlightRepository;

    @MockitoBean
    private FlightRepository flightRepository;

    @MockitoBean
    private HotelBookingRepository hotelBookingRepository;

    @MockitoBean
    private PackageBookingRepository packageBookingRepository;

    @BeforeEach
    void setUp() {
        // Mock Booking 1: Flight
        Booking flightBooking = new Booking();
        flightBooking.setBookingId(1);
        flightBooking.setType("flight");
        flightBooking.setStatus("PENDING");
        when(bookingRepository.findById(1)).thenReturn(Optional.of(flightBooking));

        BookingFlight bookingFlight = new BookingFlight();
        bookingFlight.setFlightId(1);
        when(bookingFlightRepository.findByBookingId(1)).thenReturn(Optional.of(bookingFlight));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(new Flight()));

        // Mock Booking 2: Hotel
        Booking hotelBooking = new Booking();
        hotelBooking.setBookingId(2);
        hotelBooking.setType("hotel");
        hotelBooking.setStatus("PENDING");
        when(bookingRepository.findById(2)).thenReturn(Optional.of(hotelBooking));

        HotelBooking hotelBookingEntity = new HotelBooking();
        when(hotelBookingRepository.findByBookingId(2)).thenReturn(Optional.of(hotelBookingEntity));

        // Mock Booking 3: Package
        Booking packageBooking = new Booking();
        packageBooking.setBookingId(3);
        packageBooking.setType("package");
        packageBooking.setStatus("PENDING");
        when(bookingRepository.findById(3)).thenReturn(Optional.of(packageBooking));

        PackageBooking packageBookingEntity = new PackageBooking();
        when(packageBookingRepository.findByBookingId(3)).thenReturn(Optional.of(packageBookingEntity));

        // Mock Booking 4: Cancelled
        Booking cancelledBooking = new Booking();
        cancelledBooking.setBookingId(4);
        cancelledBooking.setType("hotel");
        cancelledBooking.setStatus("CANCELLED");
        when(bookingRepository.findById(4)).thenReturn(Optional.of(cancelledBooking));
    }

    @Test
    void testConfirmBooking_HappyPath() {
        BookingResponse response = bookingService.confirmBooking(2);
        assertNotNull(response);
        assertEquals("CONFIRMED", response.status());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testConfirmBooking_UnhappyPath_NotFound() {
        when(bookingRepository.findById(9999)).thenReturn(Optional.empty());
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
        verify(bookingRepository, times(1)).save(any(Booking.class));
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
