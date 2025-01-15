package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.Pin;

import java.util.Optional;

public interface PinRepository extends JpaRepository<Pin, Long> {
    Optional<Pin> findByUserIdAndHouseId(Long userId, Long houseId);
    void deleteByUserIdAndHouseId(Long userId, Long houseId);
}