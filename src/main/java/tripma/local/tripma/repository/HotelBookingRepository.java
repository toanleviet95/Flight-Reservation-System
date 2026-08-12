package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.HotelBooking;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Integer> {
    @Query("SELECT hb FROM HotelBooking hb WHERE hb.booking_id = :bookingId")
    Optional<HotelBooking> findByBookingId(@Param("bookingId") Integer bookingId);
}
