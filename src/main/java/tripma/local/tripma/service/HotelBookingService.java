package tripma.local.tripma.service;

import org.springframework.stereotype.Service;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.dto.HotelBooking.HotelBookingResponse;
import tripma.local.tripma.entity.HotelBooking;
import tripma.local.tripma.repository.HotelBookingRepository;

@Service
public class HotelBookingService {
    private final HotelBookingRepository hotelBookingRepository;

    public HotelBookingService(HotelBookingRepository hotelBookingRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
    }
}
