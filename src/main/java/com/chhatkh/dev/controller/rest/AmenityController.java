package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.request.RentHubReq.AmenityReq;
import com.chhatkh.dev.payload.response.RentHubRes.AmenityRes;
import com.chhatkh.dev.services.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService service;

    @GetMapping
    public List<AmenityRes> all() {
        return service.all();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityRes create(@Valid @RequestBody AmenityReq req) {
        return service.create(req);
    }
}
