package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.BookingFlight;
import java.util.Optional;

@Repository
public interface BookingFlightRepository extends JpaRepository<BookingFlight, Integer> {
    Optional<BookingFlight> findByBookingId(Integer bookingId);
}
