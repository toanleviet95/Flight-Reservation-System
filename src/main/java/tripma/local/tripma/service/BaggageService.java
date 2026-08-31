package tripma.local.tripma.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.dto.Baggage.BaggageRequest;
import tripma.local.tripma.dto.Baggage.BaggageResponse;
import tripma.local.tripma.entity.Baggage;
import tripma.local.tripma.repository.BaggageRepository;
import tripma.local.tripma.exception.ResourceNotFoundException;

@Service
public class BaggageService {
    private final BaggageRepository baggageRepository;

    public BaggageService(BaggageRepository baggageRepository) {
        this.baggageRepository = baggageRepository;
    }

    public BaggageResponse findById(Integer baggageId) {
        return BaggageResponse.from(getBaggageOrThrow(baggageId));
    }

    @Transactional
    public BaggageResponse addBaggage(BaggageRequest request) {
        Baggage baggage = new Baggage();
        baggage.setPassengerId(request.passengerId());
        baggage.setFlightId(request.flightId());
        baggage.setQuantity(request.quantity());
        baggage.setFee(request.fee());
        return BaggageResponse.from(baggageRepository.save(baggage));
    }

    @Transactional
    public BaggageResponse update(Integer baggageId, BaggageRequest request) {
        Baggage baggage = getBaggageOrThrow(baggageId);
        baggage.setPassengerId(request.passengerId());
        baggage.setFlightId(request.flightId());
        baggage.setQuantity(request.quantity());
        baggage.setFee(request.fee());
        return BaggageResponse.from(baggageRepository.save(baggage));
    }

    @Transactional
    public void delete(Integer baggageId) {
        baggageRepository.delete(getBaggageOrThrow(baggageId));
    }

    private Baggage getBaggageOrThrow(Integer baggageId) {
        return baggageRepository.findById(baggageId)
                .orElseThrow(() -> new ResourceNotFoundException("Baggage not found with id: " + baggageId));
    }

    // public Double calculateFee(BaggageRequest request) {
    //     return null;
    // }
}
