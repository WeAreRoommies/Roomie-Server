package server.consumer.repository;

import entity.HousingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRequestRepository extends JpaRepository<HousingRequest, Long> {
}
