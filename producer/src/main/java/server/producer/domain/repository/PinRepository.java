package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.producer.entity.Pin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.producer.entity.Room;

import java.util.List;
import java.util.Optional;

public interface PinRepository extends JpaRepository<Pin, Long> {
    Optional<Pin> findByUserIdAndHouseId(Long userId, Long houseId);
    void deleteByUserIdAndHouseId(Long userId, Long houseId);
}