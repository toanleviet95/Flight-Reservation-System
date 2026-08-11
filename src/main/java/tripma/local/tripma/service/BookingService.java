package tripma.local.tripma.service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.dto.Booking.BookingRequest;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.entity.Booking;
import tripma.local.tripma.entity.Hotel;
import tripma.local.tripma.entity.HotelBooking;
import tripma.local.tripma.repository.BookingRepository;
import tripma.local.tripma.repository.HotelBookingRepository;
import tripma.local.tripma.repository.HotelRepository;
import tripma.local.tripma.repository.PackageBookingRepository;
import tripma.local.tripma.dto.PackageBooking.PackageBookingRequest;
import tripma.local.tripma.dto.PackageBooking.PackageBookingResponse;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelBookingService hotelBookingService;
    private final HotelRepository hotelRepository;
    private final HotelBookingRepository hotelBookingRepository;

    public BookingService(BookingRepository bookingRepository, HotelBookingService hotelBookingService,
            HotelBookingRepository hotelBookingRepository,
            HotelRepository hotelRepository) {
        this.bookingRepository = bookingRepository;
        this.hotelBookingService = hotelBookingService;
        this.hotelRepository = hotelRepository;
        this.hotelBookingRepository = hotelBookingRepository;
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

    public BookingResponse createPackageBooking(PackageBookingRequest request) {
        
        return null;
    }
}