package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.ReviewDto;
import com.chhatkh.dev.dtos.ReviewRequest;
import com.chhatkh.dev.entities.Review;
import com.chhatkh.dev.entities.Role;
import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.entities.User;
import com.chhatkh.dev.exceptions.BadRequestException;
import com.chhatkh.dev.exceptions.ResourceNotFoundException;
import com.chhatkh.dev.repositories.ReviewRepository;
import com.chhatkh.dev.repositories.RoomRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;
    private final AuthService authService;

    public ReviewService(ReviewRepository reviewRepository,
                         RoomRepository roomRepository,
                         AuthService authService) {
        this.reviewRepository = reviewRepository;
        this.roomRepository = roomRepository;
        this.authService = authService;
    }

    @Transactional
    public ReviewDto addReview(Long roomId, ReviewRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        if (room.getOwner() != null && room.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Landlord cannot review their own room");
        }

        // Check if user already reviewed this room, update if exists or create new
        Review review = reviewRepository.findByRoomIdAndUserId(roomId, currentUser.getId())
                .orElse(new Review(room, currentUser, request.getRating(), request.getComment()));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }

        return reviewRepository.findByRoomIdOrderByCreatedAtDesc(roomId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        boolean isAuthor = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    public ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRoomId(review.getRoom().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFullName() != null ? review.getUser().getFullName() : review.getUser().getUsername());
        dto.setUserAvatar(review.getUser().getProfileImageUrl());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
