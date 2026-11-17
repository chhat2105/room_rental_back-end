package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.response.RentHubRes.PropertyRes;
import com.chhatkh.dev.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService service;

    @GetMapping
    public List<PropertyRes> mine() {
        return service.mine();
    }

    @PostMapping("/{propertyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long propertyId) {
        service.add(propertyId);
    }

    @DeleteMapping("/{propertyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long propertyId) {
        service.remove(propertyId);
    }
}
