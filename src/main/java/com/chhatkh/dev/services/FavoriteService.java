package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.RoomDto;
import com.chhatkh.dev.entities.Favorite;
import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.entities.User;
import com.chhatkh.dev.exceptions.ResourceNotFoundException;
import com.chhatkh.dev.repositories.FavoriteRepository;
import com.chhatkh.dev.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RoomRepository roomRepository;
    private final AuthService authService;
    private final RoomService roomService;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           RoomRepository roomRepository,
                           AuthService authService,
                           RoomService roomService) {
        this.favoriteRepository = favoriteRepository;
        this.roomRepository = roomRepository;
        this.authService = authService;
        this.roomService = roomService;
    }

    @Transactional
    public boolean toggleFavorite(Long roomId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndRoomId(currentUser.getId(), roomId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // Removed from favorites
        } else {
            Favorite favorite = new Favorite(currentUser, room);
            favoriteRepository.save(favorite);
            return true; // Added to favorites
        }
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getMyFavorites() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(favorite -> roomService.toDto(favorite.getRoom()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isRoomFavorited(Long roomId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return favoriteRepository.existsByUserIdAndRoomId(currentUser.getId(), roomId);
    }
}
