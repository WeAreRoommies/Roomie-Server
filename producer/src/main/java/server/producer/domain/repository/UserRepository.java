package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.producer.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long userId);
}
