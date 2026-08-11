package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.PackageBooking;

@Repository
public interface PackageBookingRepository extends JpaRepository<PackageBooking, Integer> {
}
