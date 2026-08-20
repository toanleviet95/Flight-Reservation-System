package tripma.local.tripma.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.dto.HotelBooking.HotelBookingResponse;
import tripma.local.tripma.service.HotelBookingService;

import java.util.List;

@RestController
@RequestMapping("/api/hotel-bookings")
public class HotelBookingController {

    private final HotelBookingService hotelBookingService;

    public HotelBookingController(HotelBookingService hotelBookingService) {
        this.hotelBookingService = hotelBookingService;
    }
}
