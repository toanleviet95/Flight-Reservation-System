package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
}