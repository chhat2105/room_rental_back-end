package com.chhatkh.dev.controllers;

import com.chhatkh.dev.dtos.ApiResponse;
import com.chhatkh.dev.dtos.InquiryDto;
import com.chhatkh.dev.dtos.InquiryRequest;
import com.chhatkh.dev.services.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@Tag(name = "Inquiries", description = "Tenant to Landlord contact and message inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    @Operation(summary = "Send inquiry/message to room landlord")
    public ResponseEntity<ApiResponse<InquiryDto>> sendInquiry(@Valid @RequestBody InquiryRequest request) {
        InquiryDto inquiry = inquiryService.sendInquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Inquiry sent successfully to landlord", inquiry));
    }

    @GetMapping("/received")
    @Operation(summary = "Get inquiries received by authenticated landlord")
    public ResponseEntity<ApiResponse<List<InquiryDto>>> getReceivedInquiries() {
        List<InquiryDto> list = inquiryService.getMyReceivedInquiries();
        return ResponseEntity.ok(ApiResponse.ok("Received inquiries retrieved successfully", list));
    }

    @GetMapping("/sent")
    @Operation(summary = "Get inquiries sent by authenticated user")
    public ResponseEntity<ApiResponse<List<InquiryDto>>> getSentInquiries() {
        List<InquiryDto> list = inquiryService.getMySentInquiries();
        return ResponseEntity.ok(ApiResponse.ok("Sent inquiries retrieved successfully", list));
    }
}
