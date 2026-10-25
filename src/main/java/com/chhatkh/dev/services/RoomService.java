package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.RoomDto;
import com.chhatkh.dev.entities.Role;
import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.entities.RoomType;
import com.chhatkh.dev.entities.User;
import com.chhatkh.dev.exceptions.ResourceNotFoundException;
import com.chhatkh.dev.repositories.ReviewRepository;
import com.chhatkh.dev.repositories.RoomRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;
    private final AuthService authService;

    public RoomService(RoomRepository roomRepository,
                       ReviewRepository reviewRepository,
                       AuthService authService) {
        this.roomRepository = roomRepository;
        this.reviewRepository = reviewRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms(String keyword, String city, String district,
                                    RoomType roomType, Double minPrice, Double maxPrice,
                                    Boolean available) {
        List<Room> rooms;
        boolean hasFilter = (keyword != null && !keyword.trim().isEmpty())
                || (city != null && !city.trim().isEmpty())
                || (district != null && !district.trim().isEmpty())
                || roomType != null
                || minPrice != null
                || maxPrice != null
                || available != null;

        if (hasFilter) {
            String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
            String c = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
            String d = (district != null && !district.trim().isEmpty()) ? district.trim() : null;
            rooms = roomRepository.searchRooms(kw, c, d, roomType, minPrice, maxPrice, available);
        } else {
            rooms = roomRepository.findAll();
        }

        return rooms.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return toDto(room);
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getMyRooms() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return roomRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDto createRoom(RoomDto dto) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != Role.ROLE_LANDLORD && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Only landlords or administrators can create room listings");
        }

        Room room = new Room();
        mapDtoToEntity(dto, room);
        room.setOwner(currentUser);

        Room saved = roomRepository.save(room);
        return toDto(saved);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto dto) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        // Check ownership or admin
        boolean isOwner = room.getOwner() != null && room.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to update this room");
        }

        mapDtoToEntity(dto, room);
        Room updated = roomRepository.save(room);
        return toDto(updated);
    }

    @Transactional
    public RoomDto toggleAvailability(Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        boolean isOwner = room.getOwner() != null && room.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to modify this room");
        }

        room.setAvailable(!room.isAvailable());
        return toDto(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        boolean isOwner = room.getOwner() != null && room.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this room");
        }

        roomRepository.delete(room);
    }

    private void mapDtoToEntity(RoomDto dto, Room room) {
        room.setTitle(dto.getTitle());
        room.setDescription(dto.getDescription());
        room.setPricePerMonth(dto.getPricePerMonth());
        room.setDeposit(dto.getDeposit());
        if (dto.getRoomType() != null) {
            room.setRoomType(dto.getRoomType());
        }
        room.setAddress(dto.getAddress());
        room.setCity(dto.getCity());
        room.setDistrict(dto.getDistrict());
        room.setLatitude(dto.getLatitude());
        room.setLongitude(dto.getLongitude());
        room.setSizeSqMeters(dto.getSizeSqMeters());
        if (dto.getBedrooms() != null) {
            room.setBedrooms(dto.getBedrooms());
        }
        if (dto.getBathrooms() != null) {
            room.setBathrooms(dto.getBathrooms());
        }
        room.setAvailable(dto.isAvailable());
        room.setFurnished(dto.isFurnished());
        room.setPetFriendly(dto.isPetFriendly());

        if (dto.getAmenities() != null) {
            room.setAmenities(dto.getAmenities());
        }
        if (dto.getImageUrls() != null) {
            room.setImageUrls(dto.getImageUrls());
        }
    }

    public RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setTitle(room.getTitle());
        dto.setDescription(room.getDescription());
        dto.setPricePerMonth(room.getPricePerMonth());
        dto.setDeposit(room.getDeposit());
        dto.setRoomType(room.getRoomType());
        dto.setAddress(room.getAddress());
        dto.setCity(room.getCity());
        dto.setDistrict(room.getDistrict());
        dto.setLatitude(room.getLatitude());
        dto.setLongitude(room.getLongitude());
        dto.setSizeSqMeters(room.getSizeSqMeters());
        dto.setBedrooms(room.getBedrooms());
        dto.setBathrooms(room.getBathrooms());
        dto.setAvailable(room.isAvailable());
        dto.setFurnished(room.isFurnished());
        dto.setPetFriendly(room.isPetFriendly());
        dto.setAmenities(new ArrayList<>(room.getAmenities()));
        dto.setImageUrls(new ArrayList<>(room.getImageUrls()));
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());

        if (room.getOwner() != null) {
            dto.setOwnerId(room.getOwner().getId());
            dto.setOwnerName(room.getOwner().getFullName() != null ? room.getOwner().getFullName() : room.getOwner().getUsername());
            dto.setOwnerEmail(room.getOwner().getEmail());
            dto.setOwnerPhone(room.getOwner().getPhoneNumber());
        }

        // Ratings & review summary
        Double avg = reviewRepository.getAverageRatingForRoom(room.getId());
        dto.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        dto.setTotalReviews((int) reviewRepository.countByRoomId(room.getId()));

        return dto;
    }
}
