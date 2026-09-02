package tripma.local.tripma.service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.dto.Booking.BookingRequest;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.Booking.FlightLegRequest;
import tripma.local.tripma.dto.BookingFlight.BookingFlightResponse;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.dto.HotelBooking.HotelBookingResponse;
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

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelBookingService hotelBookingService;
    private final HotelRepository hotelRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final FlightRepository flightRepository;

    public BookingService(
            BookingRepository bookingRepository,
            HotelBookingService hotelBookingService,
            HotelRepository hotelRepository,
            HotelBookingRepository hotelBookingRepository,
            BookingFlightRepository bookingFlightRepository,
            FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.hotelBookingService = hotelBookingService;
        this.hotelRepository = hotelRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.bookingFlightRepository = bookingFlightRepository;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public BookingResponse createFlightBooking(BookingRequest request) {
        List<FlightLegRequest> legs = request.flights();
        List<Flight> flights = legs.stream()
                .map(leg -> flightRepository.findById(Long.valueOf(leg.flightId()))
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy chuyến bay với ID " + leg.flightId())))
                .toList();
        Booking booking = new Booking();
        booking.setUserId(request.userId());
        booking.setBookingCode(request.bookingCode());
        booking.setType("FLIGHT");
        booking.setStatus("PENDING");
        booking.setDiscountCode(request.discountCode());
        booking.setDiscountAmount(request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO);
        booking.setTotalPrice(request.totalPrice() != null ? request.totalPrice() : BigDecimal.ZERO);
        bookingRepository.save(booking);

        List<BookingFlightResponse> flightResponses = legs.stream().map(leg -> {
            Flight flight = flights.stream()
                    .filter(f -> f.getId().equals(Long.valueOf(leg.flightId())))
                    .findFirst()
                    .orElseThrow();

            BookingFlight bookingFlight = new BookingFlight();
            bookingFlight.setBookingId(booking.getBookingId());
            bookingFlight.setFlightId(leg.flightId());
            bookingFlight.setDirection(leg.direction());
            bookingFlight.setCabinClass(leg.cabinClass());
            bookingFlight.setPrice(leg.price());
            bookingFlightRepository.save(bookingFlight);

            return BookingFlightResponse.from(bookingFlight, flight);
        }).toList();

        return BookingResponse.from(booking, flightResponses);
    }

    @Transactional
    public BookingResponse createHotelBooking(HotelBookingRequest request) {
        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy khách sạn với ID " + request.hotelId()));

        long numberOfNights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Lỗi: Ngày trả phòng phải nằm sau ngày nhận phòng!");
        }

        BigDecimal pricePerNight = hotel.getPrice_per_night() != null
                ? hotel.getPrice_per_night()
                : BigDecimal.ZERO;
        BigDecimal hotelTotalPrice = pricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
        BigDecimal discountAmount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;
        BigDecimal finalTotalPrice = hotelTotalPrice.subtract(discountAmount).max(BigDecimal.ZERO);

        Booking parentBooking = new Booking();
        parentBooking.setUserId(request.userId());
        parentBooking.setBookingCode("HOTEL-" + System.currentTimeMillis());
        parentBooking.setType("HOTEL");
        parentBooking.setStatus("PENDING");
        parentBooking.setDiscountAmount(discountAmount);
        parentBooking.setTotalPrice(finalTotalPrice);
        bookingRepository.save(parentBooking);

        HotelBooking childBooking = new HotelBooking();
        childBooking.setBookingId(parentBooking.getBookingId());
        childBooking.setHotelId(hotel.getHotel_id());
        childBooking.setCheckIn(request.checkIn());
        childBooking.setCheckOut(request.checkOut());
        childBooking.setGuests(request.guests());
        childBooking.setTotalPrice(finalTotalPrice);
        hotelBookingRepository.save(childBooking);

        return BookingResponse.from(parentBooking);
    }
}
