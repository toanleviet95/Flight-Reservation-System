package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.Package;

@Repository
public interface PackageRepository extends JpaRepository<Package, Integer> {
}