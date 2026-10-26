package com.chhatkh.dev.controllers;

import com.chhatkh.dev.dtos.ApiResponse;
import com.chhatkh.dev.dtos.RoomDto;
import com.chhatkh.dev.services.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "User saved/wishlist rooms management")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{roomId}")
    @Operation(summary = "Toggle favorite status for a room (adds if not exists, removes if exists)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFavorite(@PathVariable Long roomId) {
        boolean favorited = favoriteService.toggleFavorite(roomId);
        String msg = favorited ? "Room added to favorites" : "Room removed from favorites";
        return ResponseEntity.ok(ApiResponse.ok(msg, Map.of("favorited", favorited, "roomId", roomId)));
    }

    @GetMapping("/my-favorites")
    @Operation(summary = "Get list of favorite rooms for authenticated user")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getMyFavorites() {
        List<RoomDto> favorites = favoriteService.getMyFavorites();
        return ResponseEntity.ok(ApiResponse.ok("Favorites retrieved successfully", favorites));
    }

    @GetMapping("/{roomId}/status")
    @Operation(summary = "Check if room is favorited by current user")
    public ResponseEntity<ApiResponse<Boolean>> checkFavoriteStatus(@PathVariable Long roomId) {
        boolean favorited = favoriteService.isRoomFavorited(roomId);
        return ResponseEntity.ok(ApiResponse.ok("Status retrieved", favorited));
    }
}
