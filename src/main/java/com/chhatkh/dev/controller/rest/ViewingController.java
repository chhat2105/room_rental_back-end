package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.request.RentHubReq.ViewingReq;
import com.chhatkh.dev.payload.response.RentHubRes.ViewingRes;
import com.chhatkh.dev.services.ViewingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viewings")
@RequiredArgsConstructor
public class ViewingController {
    private final ViewingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ViewingRes request(@Valid @RequestBody ViewingReq req) {
        return service.request(req);
    }

    @GetMapping("/my")
    public List<ViewingRes> mine() {
        return service.mine();
    }

    @PostMapping("/{id}/cancel")
    public ViewingRes cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    @GetMapping("/incoming")
    public List<ViewingRes> incoming() {
        return service.incoming();
    }

    @PostMapping("/{id}/approve")
    public ViewingRes approve(@PathVariable Long id) {
        return service.decide(id, true);
    }

    @PostMapping("/{id}/reject")
    public ViewingRes reject(@PathVariable Long id) {
        return service.decide(id, false);
    }
}
