package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.BookingFlight;

@Repository
public interface BookingFlightRepository extends JpaRepository<BookingFlight, Integer> {
}
