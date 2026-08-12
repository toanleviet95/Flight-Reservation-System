package tripma.local.tripma.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tripma.local.tripma.dto.Booking.BookingRequest;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.PackageBooking.PackageBookingRequest;
import tripma.local.tripma.dto.BookingDetail.BookingDetailResponse;
import tripma.local.tripma.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createFlightBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createFlightBooking(request);
    }

    @PostMapping("/package")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createPackageBooking(@Valid @RequestBody PackageBookingRequest request) {
        return bookingService.createPackageBooking(request);
    }

    @PutMapping("/{id}/confirm")
    public BookingResponse confirmBooking(@PathVariable Integer id) {
        return bookingService.confirmBooking(id);
    }

    @PutMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable Integer id) {
        return bookingService.cancelBooking(id);
    }

    @GetMapping("/{id}")
    public BookingDetailResponse getDetailBooking(@PathVariable Integer id) {
        return bookingService.getDetailBooking(id);
    }
}