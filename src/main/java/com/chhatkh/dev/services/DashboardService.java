package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.response.RentHubRes.LandlordDashboardRes;
import com.chhatkh.dev.payload.response.RentHubRes.TenantHomeRes;

public interface DashboardService {
    TenantHomeRes tenantHome();

    LandlordDashboardRes landlord();
}
