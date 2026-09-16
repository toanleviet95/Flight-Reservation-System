package tripma.local.tripma.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.repository.HotelBookingRepository;

@Service
@Transactional
public class HotelBookingService {

    private final HotelBookingRepository hotelBookingRepository;

    public HotelBookingService(HotelBookingRepository hotelBookingRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
    }
}
