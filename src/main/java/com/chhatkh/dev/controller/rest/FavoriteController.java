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
@RequestMapping("/api/favorites")
@PreAuthorize("hasRole('TENANT')")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService service;

    @GetMapping public List<PropertyRes> mine() { return service.mine(); }

    @PostMapping("/{propertyId}") @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long propertyId) { service.add(propertyId); }

    @DeleteMapping("/{propertyId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long propertyId) { service.remove(propertyId); }
}
