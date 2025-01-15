package server.producer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.producer.entity.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.producer.entity.Room;

import java.util.List;

public interface PinRepository extends JpaRepository<Room, Long> {

}