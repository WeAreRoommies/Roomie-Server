package server.producer.domain.repository;

import entity.RecentlyViewedHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedHouseRepository extends JpaRepository<RecentlyViewedHouse, Long> {
	Optional<RecentlyViewedHouse> findByUserIdAndHouseId(Long userId, Long houseId);

	List<RecentlyViewedHouse> findByUserIdOrderByViewedAtDesc(Long userId);
}
