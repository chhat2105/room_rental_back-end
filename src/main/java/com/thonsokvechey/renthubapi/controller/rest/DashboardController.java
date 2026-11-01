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
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;
    private final UserProfileService profileService;

    @GetMapping("/api/tenant/home") @PreAuthorize("hasRole('TENANT')")
    public TenantHomeRes tenantHome() { return service.tenantHome(); }

    @GetMapping("/api/landlord/dashboard") @PreAuthorize("hasRole('LANDLORD')")
    public LandlordDashboardRes landlordDashboard() { return service.landlord(); }

    /** Landlord views an applicant's profile (or users view each other once an application exists). */
    @GetMapping("/api/users/{id}/profile")
    public UserProfileRes profile(@PathVariable Long id) { return profileService.get(id); }
}
