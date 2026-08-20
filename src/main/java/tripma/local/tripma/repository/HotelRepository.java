package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}