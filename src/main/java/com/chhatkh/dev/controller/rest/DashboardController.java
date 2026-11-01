package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import com.chhatkh.dev.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;

    @GetMapping("/api/tenant/home") @PreAuthorize("hasRole('TENANT')")
    public TenantHomeRes tenantHome() { return service.tenantHome(); }

    @GetMapping("/api/landlord/dashboard") @PreAuthorize("hasRole('LANDLORD')")
    public LandlordDashboardRes landlordDashboard() { return service.landlord(); }
}
