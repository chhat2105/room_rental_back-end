package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.request.RentHubReq.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import com.chhatkh.dev.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService service;

    @PostMapping @PreAuthorize("hasRole('TENANT')") @ResponseStatus(HttpStatus.CREATED)
    public ComplaintRes create(@Valid @RequestBody ComplaintReq req) { return service.create(req); }

    @GetMapping("/my") @PreAuthorize("hasRole('TENANT')")
    public List<ComplaintRes> mine() { return service.mine(); }

    @GetMapping("/incoming") @PreAuthorize("hasRole('LANDLORD')")
    public List<ComplaintRes> incoming() { return service.incoming(); }

    @PatchMapping("/{id}/status") @PreAuthorize("hasRole('LANDLORD')")
    public ComplaintRes updateStatus(@PathVariable Long id, @RequestParam ComplaintStatus status) { return service.updateStatus(id, status); }
}
