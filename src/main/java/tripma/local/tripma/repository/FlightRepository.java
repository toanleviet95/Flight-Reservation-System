package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
}
