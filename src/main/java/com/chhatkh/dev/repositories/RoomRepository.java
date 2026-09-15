package com.chhatkh.dev.repositories;

import com.chhatkh.dev.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
