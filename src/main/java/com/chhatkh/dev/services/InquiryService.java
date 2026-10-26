package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.InquiryDto;
import com.chhatkh.dev.dtos.InquiryRequest;
import com.chhatkh.dev.entities.Inquiry;
import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.entities.User;
import com.chhatkh.dev.exceptions.BadRequestException;
import com.chhatkh.dev.exceptions.ResourceNotFoundException;
import com.chhatkh.dev.repositories.InquiryRepository;
import com.chhatkh.dev.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final RoomRepository roomRepository;
    private final AuthService authService;

    public InquiryService(InquiryRepository inquiryRepository,
                          RoomRepository roomRepository,
                          AuthService authService) {
        this.inquiryRepository = inquiryRepository;
        this.roomRepository = roomRepository;
        this.authService = authService;
    }

    @Transactional
    public InquiryDto sendInquiry(InquiryRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        User receiver = room.getOwner();
        if (receiver == null) {
            throw new BadRequestException("This room does not have an assigned landlord to contact");
        }

        if (receiver.getId().equals(currentUser.getId())) {
            throw new BadRequestException("You cannot send an inquiry to yourself");
        }

        String phone = request.getContactPhone() != null ? request.getContactPhone() : currentUser.getPhoneNumber();

        Inquiry inquiry = new Inquiry(room, currentUser, receiver, request.getMessage(), phone);
        Inquiry saved = inquiryRepository.save(inquiry);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<InquiryDto> getMyReceivedInquiries() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return inquiryRepository.findByReceiverIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InquiryDto> getMySentInquiries() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return inquiryRepository.findBySenderIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public InquiryDto toDto(Inquiry inquiry) {
        InquiryDto dto = new InquiryDto();
        dto.setId(inquiry.getId());
        dto.setMessage(inquiry.getMessage());
        dto.setContactPhone(inquiry.getContactPhone());
        dto.setCreatedAt(inquiry.getCreatedAt());

        if (inquiry.getRoom() != null) {
            dto.setRoomId(inquiry.getRoom().getId());
            dto.setRoomTitle(inquiry.getRoom().getTitle());
        }

        if (inquiry.getSender() != null) {
            dto.setSenderId(inquiry.getSender().getId());
            dto.setSenderName(inquiry.getSender().getFullName() != null ? inquiry.getSender().getFullName() : inquiry.getSender().getUsername());
            dto.setSenderEmail(inquiry.getSender().getEmail());
        }

        if (inquiry.getReceiver() != null) {
            dto.setReceiverId(inquiry.getReceiver().getId());
            dto.setReceiverName(inquiry.getReceiver().getFullName() != null ? inquiry.getReceiver().getFullName() : inquiry.getReceiver().getUsername());
        }

        return dto;
    }
}
