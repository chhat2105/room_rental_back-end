package com.thonsokvechey.renthubapi.controller.rest;

import com.thonsokvechey.renthubapi.models.enums.*;
import com.thonsokvechey.renthubapi.payload.request.RentHubReq.*;
import com.thonsokvechey.renthubapi.payload.response.RentHubRes.*;
import com.thonsokvechey.renthubapi.services.*;
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

    /** ?q=Toul Kork&type=APARTMENT&minPrice=100&maxPrice=500&bedrooms=2&furnished=true&amenityIds=1,3&lat=11.55&lng=104.92&radiusKm=3 */
    @GetMapping
    public Page<PropertyRes> search(PropertyFilter filter,
                                    @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(filter, pageable);
    }

    @GetMapping("/{id}")
    public PropertyRes get(@PathVariable Long id) { return service.get(id); }
}
