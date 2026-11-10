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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @PostMapping @PreAuthorize("hasRole('TENANT')") @ResponseStatus(HttpStatus.CREATED)
    public ReviewRes create(@Valid @RequestBody ReviewReq req) { return service.create(req); }

    @GetMapping("/property/{propertyId}")
    public List<ReviewRes> forProperty(@PathVariable Long propertyId) { return service.forProperty(propertyId); }
}
