package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.HotelBooking;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Integer> {
}
