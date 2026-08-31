package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.Baggage;
import java.util.Optional;

@Repository
public interface BaggageRepository extends JpaRepository<Baggage, Integer> {
}
