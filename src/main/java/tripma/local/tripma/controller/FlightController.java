package tripma.local.tripma.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tripma.local.tripma.dto.Flight.FlightRequest;
import tripma.local.tripma.dto.Flight.FlightResponse;
import tripma.local.tripma.service.FlightService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public Page<FlightResponse> findAll(Pageable pageable) {
        return flightService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public FlightResponse findById(@PathVariable Long id) {
        return flightService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponse create(@Valid @RequestBody FlightRequest request) {
        return flightService.create(request);
    }

    @PutMapping("/{id}")
    public FlightResponse update(@PathVariable Long id, @Valid @RequestBody FlightRequest request) {
        return flightService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        flightService.delete(id);
    }
}
