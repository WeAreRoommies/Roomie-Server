package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.producer.entity.House;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findHouseDetailsById(Long houseId);
}
