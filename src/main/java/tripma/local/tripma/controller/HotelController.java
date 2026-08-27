package tripma.local.tripma.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import tripma.local.tripma.dto.Hotel.HotelRequest;
import tripma.local.tripma.dto.Hotel.HotelResponse;
import tripma.local.tripma.service.HotelService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public Page<HotelResponse> findAll(Pageable pageable) {
        return hotelService.findAll(pageable);
    }

    @GetMapping("/{hotel_id}")
    public HotelResponse findById(@PathVariable Integer hotel_id) {
        return hotelService.findById(hotel_id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelResponse create(@Valid @RequestBody HotelRequest request) {
        return hotelService.create(request);
    }

    @PutMapping("/{hotel_id}")
    public HotelResponse update(@PathVariable Integer hotel_id, @Valid @RequestBody HotelRequest request) {
        return hotelService.update(hotel_id, request);
    }

    @DeleteMapping("/{hotel_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer hotel_id) {
        hotelService.delete(hotel_id);
    }
}
