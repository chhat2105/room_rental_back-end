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
@RequestMapping("/api/viewings")
@RequiredArgsConstructor
public class ViewingController {
    private final ViewingService service;

    @PostMapping @PreAuthorize("hasRole('TENANT')") @ResponseStatus(HttpStatus.CREATED)
    public ViewingRes request(@Valid @RequestBody ViewingReq req) { return service.request(req); }

    @GetMapping("/my") @PreAuthorize("hasRole('TENANT')")
    public List<ViewingRes> mine() { return service.mine(); }

    @PostMapping("/{id}/cancel") @PreAuthorize("hasRole('TENANT')")
    public ViewingRes cancel(@PathVariable Long id) { return service.cancel(id); }

    @GetMapping("/incoming") @PreAuthorize("hasRole('LANDLORD')")
    public List<ViewingRes> incoming() { return service.incoming(); }

    @PostMapping("/{id}/approve") @PreAuthorize("hasRole('LANDLORD')")
    public ViewingRes approve(@PathVariable Long id) { return service.decide(id, true); }

    @PostMapping("/{id}/reject") @PreAuthorize("hasRole('LANDLORD')")
    public ViewingRes reject(@PathVariable Long id) { return service.decide(id, false); }
}
