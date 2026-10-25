package com.chhatkh.dev.controllers;

import com.chhatkh.dev.dtos.ApiResponse;
import com.chhatkh.dev.dtos.RoomDto;
import com.chhatkh.dev.entities.RoomType;
import com.chhatkh.dev.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Room listing, search, details, and landlord management")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Search and filter rooms (public)")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getAllRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean available
    ) {
        List<RoomDto> rooms = roomService.getAllRooms(keyword, city, district, roomType, minPrice, maxPrice, available);
        return ResponseEntity.ok(ApiResponse.ok("Rooms retrieved successfully", rooms));
    }

    @GetMapping("/my-rooms")
    @Operation(summary = "Get rooms created by the authenticated landlord/user")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getMyRooms() {
        List<RoomDto> rooms = roomService.getMyRooms();
        return ResponseEntity.ok(ApiResponse.ok("My rooms retrieved successfully", rooms));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room details by ID (public)")
    public ResponseEntity<ApiResponse<RoomDto>> getRoomById(@PathVariable Long id) {
        RoomDto room = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.ok("Room retrieved successfully", room));
    }

    @PostMapping
    @Operation(summary = "Create a new room (authenticated)")
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(@Valid @RequestBody RoomDto dto) {
        RoomDto created = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Room created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details (owner or admin)")
    public ResponseEntity<ApiResponse<RoomDto>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDto dto) {
        RoomDto updated = roomService.updateRoom(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Room updated successfully", updated));
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Toggle room availability (owner or admin)")
    public ResponseEntity<ApiResponse<RoomDto>> toggleAvailability(@PathVariable Long id) {
        RoomDto updated = roomService.toggleAvailability(id);
        return ResponseEntity.ok(ApiResponse.ok("Room availability toggled successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room (owner or admin)")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.ok("Room deleted successfully", null));
    }
}
