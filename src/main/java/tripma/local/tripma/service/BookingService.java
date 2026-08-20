package tripma.local.tripma.service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.dto.Booking.BookingRequest;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.dto.HotelBooking.HotelBookingResponse;
import tripma.local.tripma.dto.BookingDetail.BookingDetailResponse;
import tripma.local.tripma.dto.BookingFlight.BookingFlightResponse;
import tripma.local.tripma.entity.Booking;
import tripma.local.tripma.entity.Hotel;
import tripma.local.tripma.entity.Package;
import tripma.local.tripma.entity.PackageBooking;
import tripma.local.tripma.entity.HotelBooking;
import tripma.local.tripma.entity.BookingFlight;
import tripma.local.tripma.entity.Flight;
import tripma.local.tripma.repository.BookingRepository;
import tripma.local.tripma.repository.HotelBookingRepository;
import tripma.local.tripma.repository.HotelRepository;
import tripma.local.tripma.repository.PackageBookingRepository;
import tripma.local.tripma.repository.PackageRepository;
import tripma.local.tripma.repository.BookingFlightRepository;
import tripma.local.tripma.repository.FlightRepository;
import tripma.local.tripma.dto.PackageBooking.PackageBookingRequest;
import tripma.local.tripma.dto.PackageBooking.PackageBookingResponse;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelBookingService hotelBookingService;
    private final HotelRepository hotelRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final PackageRepository packageRepository;
    private final PackageBookingRepository packageBookingRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final FlightRepository flightRepository;

    public BookingService(
            BookingRepository bookingRepository, HotelBookingService hotelBookingService,
            HotelBookingRepository hotelBookingRepository,
            HotelRepository hotelRepository,
            PackageRepository packageRepository,
            PackageBookingRepository packageBookingRepository,
            BookingFlightRepository bookingFlightRepository,
            FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.hotelBookingService = hotelBookingService;
        this.hotelRepository = hotelRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.packageRepository = packageRepository;
        this.packageBookingRepository = packageBookingRepository;
        this.bookingFlightRepository = bookingFlightRepository;
        this.flightRepository = flightRepository;
    }

    public BookingResponse createFlightBooking(BookingRequest request) {
        Booking booking = new Booking();
        booking.setUserId(request.userId());
        booking.setBookingCode(request.bookingCode());
        booking.setType(request.type());
        booking.setStatus(request.status());
        booking.setDiscountCode(request.discountCode());
        booking.setDiscountAmount(request.discountAmount());
        booking.setTotalPrice(request.totalPrice());
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse createHotelBooking(HotelBookingRequest request) {
        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy khách sạn với ID " + request.hotelId()));

        long numberOfNights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Lỗi: Ngày trả phòng phải nằm sau ngày nhận phòng!");
        }

        double pricePerNight = hotel.getPrice_per_night() != null ? hotel.getPrice_per_night().doubleValue() : 0.0;
        double hotelTotalPrice = pricePerNight * numberOfNights;
        double discountAmount = request.discountAmount() != null ? request.discountAmount() : 0.0;
        double finalTotalPrice = hotelTotalPrice - discountAmount;

        Booking parentBooking = new Booking();
        parentBooking.setUserId(request.userId());
        parentBooking.setBookingCode("HOTEL-" + System.currentTimeMillis());
        parentBooking.setType("HOTEL");
        parentBooking.setStatus("PENDING");
        parentBooking.setTotalPrice(finalTotalPrice);

        bookingRepository.save(parentBooking);

        HotelBooking childBooking = new HotelBooking();
        childBooking.setBooking_id(parentBooking.getBookingId());
        childBooking.setHotel_id(hotel.getHotel_id());
        childBooking.setCheck_in(request.checkIn());
        childBooking.setCheck_out(request.checkOut());
        childBooking.setGuests(request.guests());
        childBooking.setTotal_price(BigDecimal.valueOf(hotelTotalPrice));

        hotelBookingRepository.save(childBooking);

        return BookingResponse.from(parentBooking);
    }

    @Transactional
    public BookingResponse createPackageBooking(PackageBookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Lỗi: Yêu cầu không được rỗng!");
        }

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lỗi: Không tìm thấy booking với ID " + request.bookingId()));

        Package travelPackage = packageRepository.findById(request.packageId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lỗi: Không tìm thấy package với ID " + request.packageId()));

        PackageBooking packageBooking = new PackageBooking();
        packageBooking.setBooking_id(request.bookingId());
        packageBooking.setPackage_id(request.packageId());

        packageBookingRepository.save(packageBooking);

        double totalPrice = travelPackage.getPrice() - booking.getDiscountAmount();

        booking.setType("package");
        booking.setStatus("PENDING");
        booking.setTotalPrice(totalPrice);
        booking.setBookingCode("PACKAGE-" + System.currentTimeMillis());

        bookingRepository.save(booking);

        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse confirmBooking(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Lỗi: Mã đặt chỗ (bookingId) không được để trống!");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lỗi: Không tìm thấy booking với ID " + bookingId));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Lỗi: Booking đã bị hủy!");
        }

        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Lỗi: Booking đã được xác nhận!");
        }

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Lỗi: Mã đặt chỗ (bookingId) không được để trống!");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lỗi: Không tìm thấy booking với ID " + bookingId));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Lỗi: Booking đã bị hủy!");
        }

        if ("COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Lỗi: Không thể hủy đặt chỗ đã hoàn thành!");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        return BookingResponse.from(booking);
    }

    public BookingDetailResponse getDetailBooking(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Lỗi: Mã đặt chỗ (bookingId) không được để trống!");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lỗi: Không tìm thấy booking với ID " + bookingId));

        Object details = null;
        String type = booking.getType();
        if (type != null) {
            if (type.equalsIgnoreCase("flight")) {
                BookingFlight bookingFlight = bookingFlightRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Lỗi: Không tìm thấy chi tiết chuyến bay cho booking với ID " + bookingId));
                Flight flight = flightRepository.findById(bookingFlight.getFlightId().longValue())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Lỗi: Không tìm thấy chuyến bay với ID " + bookingFlight.getFlightId()));
                details = BookingFlightResponse.from(bookingFlight, flight);
            } else if (type.equalsIgnoreCase("hotel")) {
                HotelBooking hotelBooking = hotelBookingRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Lỗi: Không tìm thấy chi tiết khách sạn cho booking với ID " + bookingId));
                details = HotelBookingResponse.fromEntity(hotelBooking);
            } else if (type.equalsIgnoreCase("package")) {
                PackageBooking packageBooking = packageBookingRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Lỗi: Không tìm thấy chi tiết gói du lịch cho booking với ID " + bookingId));
                details = PackageBookingResponse.fromEntity(packageBooking);
            }
        }

        return new BookingDetailResponse(BookingResponse.from(booking), details);
    }
}