package tripma.local.tripma.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tripma.local.tripma.dto.Baggage.BaggageRequest;
import tripma.local.tripma.dto.Baggage.BaggageResponse;
import tripma.local.tripma.service.BaggageService;

@RestController
@RequestMapping("/api/baggage")
public class BaggageController {

    private final BaggageService baggageService;

    public BaggageController(BaggageService baggageService) {
        this.baggageService = baggageService;
    }

    @GetMapping("/{baggageId}")
    public BaggageResponse findById(@PathVariable Integer baggageId) {
        return baggageService.findById(baggageId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaggageResponse addBaggage(@Valid @RequestBody BaggageRequest request) {
        return baggageService.addBaggage(request);
    }

    @PutMapping("/{baggageId}")
    public BaggageResponse update(@PathVariable Integer baggageId, @Valid @RequestBody BaggageRequest request) {
        return baggageService.update(baggageId, request);
    }

    @DeleteMapping("/{baggageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer baggageId) {
        baggageService.delete(baggageId);
    }

}
