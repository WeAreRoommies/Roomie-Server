package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.producer.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long userId);

	@Query("SELECT u.location FROM User u WHERE u.id = :userId")
	Optional<String> findLocationById(@Param("userId") Long userId);
}
