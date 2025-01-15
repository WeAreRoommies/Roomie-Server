package server.producer.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.producer.entity.House;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
	List<House> findByLocationAndMoodTag(String location, String moodTag);
}