package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.RoomDto;
import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.entities.RoomType;

import java.util.List;

public interface RoomService {

    List<RoomDto> getAllRooms(String keyword, String city, String district,
                              RoomType roomType, Double minPrice, Double maxPrice,
                              Boolean available);

    RoomDto getRoomById(Long id);

    List<RoomDto> getMyRooms();

    RoomDto createRoom(RoomDto dto);

    RoomDto updateRoom(Long id, RoomDto dto);

    RoomDto toggleAvailability(Long id);

    void deleteRoom(Long id);

    RoomDto toDto(Room room);
}
