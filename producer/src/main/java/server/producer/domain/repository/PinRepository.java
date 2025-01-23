package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.Pin;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {
    List<Pin> findByUserIdAndHouseId(Long userId, Long houseId);
//    void deleteByUserIdAndHouseId(Long userId, Long houseId);
}