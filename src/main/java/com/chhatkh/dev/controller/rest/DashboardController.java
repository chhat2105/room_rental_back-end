package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.response.RentHubRes.LandlordDashboardRes;
import com.chhatkh.dev.payload.response.RentHubRes.TenantHomeRes;
import com.chhatkh.dev.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;

    @GetMapping("/api/tenant/home")
    public TenantHomeRes tenantHome() {
        return service.tenantHome();
    }

    @GetMapping("/api/landlord/dashboard")
    public LandlordDashboardRes landlordDashboard() {
        return service.landlord();
    }
}
