package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.BookingDto;
import com.chhatkh.dev.dtos.BookingRequest;
import com.chhatkh.dev.dtos.BookingStatusUpdateRequest;
import com.chhatkh.dev.entities.*;
import com.chhatkh.dev.exceptions.BadRequestException;
import com.chhatkh.dev.exceptions.ResourceNotFoundException;
import com.chhatkh.dev.repositories.BookingRepository;
import com.chhatkh.dev.repositories.RoomRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final AuthService authService;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          AuthService authService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.authService = authService;
    }

    @Transactional
    public BookingDto createBooking(BookingRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        if (!room.isAvailable()) {
            throw new BadRequestException("This room is currently not available for rental");
        }

        if (room.getOwner() != null && room.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Landlord cannot book their own room");
        }

        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days < 1) {
            throw new BadRequestException("Rental period must be at least 1 day");
        }

        // Calculate total rent based on months/days
        double months = Math.max(1.0, (double) days / 30.0);
        double totalPrice = Math.round((room.getPricePerMonth() * months) * 100.0) / 100.0;
        Double deposit = room.getDeposit() != null ? room.getDeposit() : room.getPricePerMonth();

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setTenant(currentUser);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setMonthlyRent(room.getPricePerMonth());
        booking.setDepositAmount(deposit);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        booking.setNotes(request.getNotes());
        booking.setContactPhone(request.getContactPhone() != null ? request.getContactPhone() : currentUser.getPhoneNumber());

        Booking saved = bookingRepository.save(booking);
        return toDto(saved);
    }

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, BookingStatusUpdateRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Room room = booking.getRoom();
        boolean isLandlord = room.getOwner() != null && room.getOwner().getId().equals(currentUser.getId());
        boolean isTenant = booking.getTenant().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        BookingStatus newStatus = request.getStatus();

        if (newStatus == BookingStatus.CANCELLED) {
            if (!isTenant && !isAdmin) {
                throw new AccessDeniedException("Only the tenant or admin can cancel this booking");
            }
        } else if (newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.REJECTED || newStatus == BookingStatus.COMPLETED) {
            if (!isLandlord && !isAdmin) {
                throw new AccessDeniedException("Only the room owner or admin can update booking status to " + newStatus);
            }
            if (newStatus == BookingStatus.CONFIRMED) {
                // Mark room unavailable if desired
                room.setAvailable(false);
                roomRepository.save(room);
            } else if (newStatus == BookingStatus.COMPLETED || newStatus == BookingStatus.REJECTED) {
                room.setAvailable(true);
                roomRepository.save(room);
            }
        }

        booking.setStatus(newStatus);
        if (request.getReason() != null) {
            booking.setNotes((booking.getNotes() != null ? booking.getNotes() + " | " : "") + "Status change note: " + request.getReason());
        }

        Booking updated = bookingRepository.save(booking);
        return toDto(updated);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getMyBookings() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return bookingRepository.findByTenantIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getLandlordBookingRequests() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return bookingRepository.findByLandlordIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        boolean isTenant = booking.getTenant().getId().equals(currentUser.getId());
        boolean isLandlord = booking.getRoom().getOwner() != null && booking.getRoom().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isTenant && !isLandlord && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to view this booking");
        }

        return toDto(booking);
    }

    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setMonthlyRent(booking.getMonthlyRent());
        dto.setDepositAmount(booking.getDepositAmount());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setNotes(booking.getNotes());
        dto.setContactPhone(booking.getContactPhone());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        if (booking.getRoom() != null) {
            Room r = booking.getRoom();
            dto.setRoomId(r.getId());
            dto.setRoomTitle(r.getTitle());
            dto.setRoomAddress(r.getAddress());
            dto.setRoomCity(r.getCity());
            if (r.getImageUrls() != null && !r.getImageUrls().isEmpty()) {
                dto.setRoomThumbnail(r.getImageUrls().get(0));
            }
            if (r.getOwner() != null) {
                dto.setLandlordId(r.getOwner().getId());
                dto.setLandlordName(r.getOwner().getFullName() != null ? r.getOwner().getFullName() : r.getOwner().getUsername());
            }
        }

        if (booking.getTenant() != null) {
            User t = booking.getTenant();
            dto.setTenantId(t.getId());
            dto.setTenantName(t.getFullName() != null ? t.getFullName() : t.getUsername());
            dto.setTenantEmail(t.getEmail());
            dto.setTenantPhone(t.getPhoneNumber());
        }

        return dto;
    }
}
