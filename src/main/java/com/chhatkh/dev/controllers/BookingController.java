package com.chhatkh.dev.controllers;

import com.chhatkh.dev.dtos.ApiResponse;
import com.chhatkh.dev.dtos.BookingDto;
import com.chhatkh.dev.dtos.BookingRequest;
import com.chhatkh.dev.dtos.BookingStatusUpdateRequest;
import com.chhatkh.dev.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Room booking requests and rental reservation management")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create room booking request (Tenant)")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingDto booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Booking request submitted successfully", booking));
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get bookings made by authenticated tenant")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyBookings() {
        List<BookingDto> bookings = bookingService.getMyBookings();
        return ResponseEntity.ok(ApiResponse.ok("My bookings retrieved successfully", bookings));
    }

    @GetMapping("/landlord/requests")
    @Operation(summary = "Get incoming booking requests for landlord's rooms")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getLandlordBookingRequests() {
        List<BookingDto> requests = bookingService.getLandlordBookingRequests();
        return ResponseEntity.ok(ApiResponse.ok("Landlord booking requests retrieved successfully", requests));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(@PathVariable Long id) {
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.ok("Booking retrieved successfully", booking));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update booking status (CONFIRMED, REJECTED, CANCELLED, COMPLETED)")
    public ResponseEntity<ApiResponse<BookingDto>> updateStatus(@PathVariable Long id,
                                                               @Valid @RequestBody BookingStatusUpdateRequest request) {
        BookingDto updated = bookingService.updateBookingStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Booking status updated successfully", updated));
    }
}
