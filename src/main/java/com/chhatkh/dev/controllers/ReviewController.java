package com.chhatkh.dev.controllers;

import com.chhatkh.dev.dtos.ApiResponse;
import com.chhatkh.dev.dtos.ReviewDto;
import com.chhatkh.dev.dtos.ReviewRequest;
import com.chhatkh.dev.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Reviews", description = "Ratings and reviews for rental rooms")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/rooms/{roomId}/reviews")
    @Operation(summary = "Submit or update review for a room (authenticated)")
    public ResponseEntity<ApiResponse<ReviewDto>> addReview(
            @PathVariable Long roomId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewDto review = reviewService.addReview(roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Review submitted successfully", review));
    }

    @GetMapping("/api/rooms/{roomId}/reviews")
    @Operation(summary = "Get all reviews for a specific room (public)")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getReviewsForRoom(@PathVariable Long roomId) {
        List<ReviewDto> reviews = reviewService.getReviewsForRoom(roomId);
        return ResponseEntity.ok(ApiResponse.ok("Reviews retrieved successfully", reviews));
    }

    @DeleteMapping("/api/reviews/{id}")
    @Operation(summary = "Delete review (author or admin)")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.ok("Review deleted successfully", null));
    }
}
