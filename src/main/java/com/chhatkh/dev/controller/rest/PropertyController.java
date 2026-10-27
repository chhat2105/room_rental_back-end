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

/** Tenant: browse, search / filter, property details (public) */
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService service;

    @GetMapping
    public Page<PropertyRes> search(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) PropertyType type,
                                    @RequestParam(required = false) BigDecimal minPrice,
                                    @RequestParam(required = false) BigDecimal maxPrice,
                                    @RequestParam(required = false) Integer bedrooms,
                                    @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(q, type, minPrice, maxPrice, bedrooms, pageable);
    }

    @GetMapping("/{id}")
    public PropertyRes get(@PathVariable Long id) { return service.get(id); }
}
