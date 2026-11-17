package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.request.RentHubReq.ReviewReq;
import com.chhatkh.dev.payload.response.RentHubRes.ReviewRes;
import com.chhatkh.dev.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewRes create(@Valid @RequestBody ReviewReq req) {
        return service.create(req);
    }

    @GetMapping("/property/{propertyId}")
    public List<ReviewRes> forProperty(@PathVariable Long propertyId) {
        return service.forProperty(propertyId);
    }
}
