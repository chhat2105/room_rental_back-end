package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.RoomDto;
import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.exceptions.ResourceNotFoundException;
import com.chhatkh.dev.repositories.RoomRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return toDto(room);
    }

    public RoomDto createRoom(RoomDto dto) {
        Room room = new Room();
        BeanUtils.copyProperties(dto, room, "id");
        Room saved = roomRepository.save(room);
        return toDto(saved);
    }

    public RoomDto updateRoom(Long id, RoomDto dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        BeanUtils.copyProperties(dto, room, "id");
        Room updated = roomRepository.save(room);
        return toDto(updated);
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }

    private RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        BeanUtils.copyProperties(room, dto);
        return dto;
    }
}
