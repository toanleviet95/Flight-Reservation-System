package tripma.local.tripma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tripma.local.tripma.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}