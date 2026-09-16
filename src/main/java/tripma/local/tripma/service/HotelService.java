package tripma.local.tripma.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tripma.local.tripma.dto.Hotel.HotelRequest;
import tripma.local.tripma.dto.Hotel.HotelResponse;
import tripma.local.tripma.entity.Hotel;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.repository.HotelRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Transactional(readOnly = true)
    public Page<HotelResponse> findAll(Pageable pageable) {
        return hotelRepository.findAll(pageable).map(HotelResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public HotelResponse findById(Integer hotel_id) {
        return HotelResponse.fromEntity(getHotelOrThrow(hotel_id));
    }

    public HotelResponse create(HotelRequest request) {
        Hotel hotel = new Hotel();
        applyRequest(hotel, request);
        return HotelResponse.fromEntity(hotelRepository.save(hotel));
    }

    public HotelResponse update(Integer hotel_id, HotelRequest request) {
        Hotel hotel = getHotelOrThrow(hotel_id);
        applyRequest(hotel, request);
        return HotelResponse.fromEntity(hotelRepository.save(hotel));
    }

    public void delete(Integer hotel_id) {
        getHotelOrThrow(hotel_id);
        hotelRepository.deleteById(hotel_id);
    }

    private void applyRequest(Hotel hotel, HotelRequest request) {
        hotel.setName(request.name());
        hotel.setAddress(request.address());
        hotel.setCity(request.city());
        hotel.setCountry(request.country());
        hotel.setRating(request.rating());
        hotel.setPrice_per_night(request.price_per_night());
        hotel.setDescription(request.description());
    }

    private Hotel getHotelOrThrow(Integer hotel_id) {
        return hotelRepository.findById(hotel_id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotel_id));
    }
}
