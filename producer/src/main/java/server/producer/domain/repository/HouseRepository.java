package server.producer.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.producer.entity.House;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {

}