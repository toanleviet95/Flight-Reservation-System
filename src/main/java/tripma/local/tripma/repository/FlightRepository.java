package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tripma.local.tripma.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
