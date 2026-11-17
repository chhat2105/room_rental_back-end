package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.models.enums.ComplaintStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ComplaintReq;
import com.chhatkh.dev.payload.response.RentHubRes.ComplaintRes;
import com.chhatkh.dev.services.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintRes create(@Valid @RequestBody ComplaintReq req) {
        return service.create(req);
    }

    @GetMapping("/my")
    public List<ComplaintRes> mine() {
        return service.mine();
    }

    @GetMapping("/incoming")
    public List<ComplaintRes> incoming() {
        return service.incoming();
    }

    @PatchMapping("/{id}/status")
    public ComplaintRes updateStatus(@PathVariable Long id, @RequestParam ComplaintStatus status) {
        return service.updateStatus(id, status);
    }
}
