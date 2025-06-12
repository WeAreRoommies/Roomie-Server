package server.producer.domain.repository;

import entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.roomOccupancies WHERE r.house.id = :houseId")
    List<Room> findRoomsAndOccupanciesByHouseId(@Param("houseId") Long houseId);

}
