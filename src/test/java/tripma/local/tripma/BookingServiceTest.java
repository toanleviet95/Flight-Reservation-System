package tripma.local.tripma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tripma.local.tripma.dto.Booking.BookingRequest;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.Booking.FlightLegRequest;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.entity.Booking;
import tripma.local.tripma.entity.BookingFlight;
import tripma.local.tripma.entity.Flight;
import tripma.local.tripma.entity.Hotel;
import tripma.local.tripma.entity.HotelBooking;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.repository.BookingFlightRepository;
import tripma.local.tripma.repository.BookingRepository;
import tripma.local.tripma.repository.FlightRepository;
import tripma.local.tripma.repository.HotelBookingRepository;
import tripma.local.tripma.repository.HotelRepository;
import tripma.local.tripma.service.BookingService;
import tripma.local.tripma.service.HotelBookingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    HotelRepository hotelRepository;
    @Mock
    HotelBookingRepository hotelBookingRepository;
    @Mock
    BookingFlightRepository bookingFlightRepository;
    @Mock
    FlightRepository flightRepository;
    @Mock
    HotelBookingService hotelBookingService;

    @InjectMocks
    BookingService bookingService;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Trả về Booking có ID được gán sau khi save() */
    private Booking savedBookingWithId(int id) {
        Booking b = new Booking();
        b.setBookingId(id);
        b.setType("FLIGHT");
        b.setStatus("PENDING");
        return b;
    }

    private Flight flightWithId(long id) {
        Flight f = new Flight();
        f.setId(id);
        return f;
    }

    private Hotel hotelWithPrice(int id, BigDecimal pricePerNight) {
        Hotel h = new Hotel();
        h.setHotel_id(id);
        h.setPrice_per_night(pricePerNight);
        return h;
    }

    // ─── createFlightBooking ──────────────────────────────────────────────────

    @Test
    void createFlightBooking_positive_serverDefaultsTypeAndStatus() {
        // Arrange
        FlightLegRequest leg = new FlightLegRequest(1, "OUTBOUND", "ECONOMY", new BigDecimal("500000"));
        BookingRequest request = new BookingRequest(1, "BK-001", null, null, null, List.of(leg));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flightWithId(1L)));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(10);
            return b;
        });
        when(bookingFlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BookingResponse response = bookingService.createFlightBooking(request);

        // Assert
        assertThat(response.type()).isEqualTo("FLIGHT"); // server-controlled
        assertThat(response.status()).isEqualTo("PENDING"); // server-controlled
        assertThat(response.bookingCode()).isEqualTo("BK-001");
        assertThat(response.flights()).hasSize(1);
    }

    @Test
    void createFlightBooking_positive_allFlightLegsArePersisted() {
        // Arrange — 2 chặng bay
        FlightLegRequest leg1 = new FlightLegRequest(1, "OUTBOUND", "ECONOMY", new BigDecimal("500000"));
        FlightLegRequest leg2 = new FlightLegRequest(2, "RETURN", "BUSINESS", new BigDecimal("900000"));
        BookingRequest request = new BookingRequest(1, "BK-002", null, null, null, List.of(leg1, leg2));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flightWithId(1L)));
        when(flightRepository.findById(2L)).thenReturn(Optional.of(flightWithId(2L)));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(11);
            return b;
        });
        when(bookingFlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BookingResponse response = bookingService.createFlightBooking(request);

        // Assert
        assertThat(response.flights()).hasSize(2);
        verify(bookingFlightRepository, times(2)).save(any(BookingFlight.class));
    }

    @Test
    void createFlightBooking_negative_unknownFlightId_throws404() {
        // Arrange
        FlightLegRequest leg = new FlightLegRequest(99, "OUTBOUND", "ECONOMY", new BigDecimal("100"));
        BookingRequest request = new BookingRequest(1, "BK-XXX", null, null, null, List.of(leg));

        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert — phải ném ResourceNotFoundException (→ HTTP 404), không phải
        // 500
        assertThatThrownBy(() -> bookingService.createFlightBooking(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        // Booking cha KHÔNG được lưu vì validate trước
        verify(bookingRepository, never()).save(any());
    }

    // ─── createHotelBooking ───────────────────────────────────────────────────

    @Test
    void createHotelBooking_positive_priceCalculatedCorrectly() {
        // Arrange — 3 đêm * 500.000đ = 1.500.000
        HotelBookingRequest request = new HotelBookingRequest(
                1, 5,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 4),
                2, null);

        Hotel hotel = hotelWithPrice(5, new BigDecimal("500000"));
        when(hotelRepository.findById(5)).thenReturn(Optional.of(hotel));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(20);
            return b;
        });
        when(hotelBookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BookingResponse response = bookingService.createHotelBooking(request);

        // Assert
        assertThat(response.totalPrice()).isEqualByComparingTo("1500000");
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.type()).isEqualTo("HOTEL");
    }

    @Test
    void createHotelBooking_positive_discountAppliedAndFlooredAtZero() {
        // discount > tổng giá → kết quả phải là 0, không phải âm
        HotelBookingRequest request = new HotelBookingRequest(
                1, 5,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2), // 1 đêm * 100.000
                1, new BigDecimal("999999")); // discount > giá

        Hotel hotel = hotelWithPrice(5, new BigDecimal("100000"));
        when(hotelRepository.findById(5)).thenReturn(Optional.of(hotel));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(21);
            return b;
        });
        when(hotelBookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.createHotelBooking(request);

        // Không được âm
        assertThat(response.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createHotelBooking_positive_hotelBookingAndBookingStoreSameDiscountedPrice() {
        // Đảm bảo HotelBooking.totalPrice == Booking.totalPrice (cùng giá đã trừ
        // discount)
        HotelBookingRequest request = new HotelBookingRequest(
                1, 5,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 3), // 2 đêm * 200.000 = 400.000
                1, new BigDecimal("50000")); // discount 50.000 → final = 350.000

        Hotel hotel = hotelWithPrice(5, new BigDecimal("200000"));
        when(hotelRepository.findById(5)).thenReturn(Optional.of(hotel));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(22);
            b.setTotalPrice(new BigDecimal("350000")); // giả lập DB gán lại
            return b;
        });
        ArgumentCaptor<HotelBooking> childCaptor = ArgumentCaptor.forClass(HotelBooking.class);
        when(hotelBookingRepository.save(childCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.createHotelBooking(request);

        BigDecimal capturedChildPrice = childCaptor.getValue().getTotalPrice();
        assertThat(capturedChildPrice).isEqualByComparingTo("350000");
        assertThat(response.totalPrice()).isEqualByComparingTo("350000");
    }

    @Test
    void createHotelBooking_negative_checkOutSameAsCheckIn_throwsIllegalArgument() {
        // Cùng ngày → 0 đêm → phải reject
        HotelBookingRequest request = new HotelBookingRequest(
                1, 5,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 1), // check-out = check-in
                1, null);

        Hotel hotel = hotelWithPrice(5, new BigDecimal("500000"));
        when(hotelRepository.findById(5)).thenReturn(Optional.of(hotel));

        assertThatThrownBy(() -> bookingService.createHotelBooking(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createHotelBooking_negative_checkOutBeforeCheckIn_throwsIllegalArgument() {
        // check-out trước check-in
        HotelBookingRequest request = new HotelBookingRequest(
                1, 5,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 3), // trước check-in
                1, null);

        Hotel hotel = hotelWithPrice(5, new BigDecimal("500000"));
        when(hotelRepository.findById(5)).thenReturn(Optional.of(hotel));

        assertThatThrownBy(() -> bookingService.createHotelBooking(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createHotelBooking_negative_unknownHotelId_throws404() {
        HotelBookingRequest request = new HotelBookingRequest(
                1, 999,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2),
                1, null);

        when(hotelRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createHotelBooking(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(bookingRepository, never()).save(any());
    }
}
