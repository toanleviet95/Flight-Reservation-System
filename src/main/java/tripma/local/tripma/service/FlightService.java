package tripma.local.tripma.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripma.local.tripma.dto.Flight.FlightRequest;
import tripma.local.tripma.dto.Flight.FlightResponse;
import tripma.local.tripma.entity.Flight;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.repository.FlightRepository;

import java.util.List;

@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<FlightResponse> findAll() {
        return flightRepository.findAll().stream().map(FlightResponse::from).toList();
    }

    public FlightResponse findById(Long id) {
        return FlightResponse.from(getFlightOrThrow(id));
    }

    public FlightResponse create(FlightRequest request) {
        Flight flight = new Flight();
        flight.setFlightNumber(request.flightNumber());
        flight.setAirlineId(request.airlineId());
        flight.setDepartureAirportId(request.departureAirportId());
        flight.setArrivalAirportId(request.arrivalAirportId());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setBasePrice(request.basePrice());
        flight.setAircraftId(request.aircraftId());
        return FlightResponse.from(flightRepository.save(flight));
    }

    public FlightResponse update(Long id, FlightRequest request) {
        Flight flight = getFlightOrThrow(id);
        flight.setFlightNumber(request.flightNumber());
        flight.setAirlineId(request.airlineId());
        flight.setDepartureAirportId(request.departureAirportId());
        flight.setArrivalAirportId(request.arrivalAirportId());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setBasePrice(request.basePrice());
        flight.setAircraftId(request.aircraftId());
        return FlightResponse.from(flightRepository.save(flight));
    }

    public void delete(Long id) {
        flightRepository.delete(getFlightOrThrow(id));
    }

    private Flight getFlightOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
    }
}
