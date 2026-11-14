package com.chhatkh.dev.services;

import com.chhatkh.dev.models.enums.ComplaintStatus;
import com.chhatkh.dev.models.enums.ContractStatus;
import com.chhatkh.dev.models.enums.PropertyStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ComplaintResolveReq;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    AdminDashboardRes dashboard();

    Page<AdminUserRes> users(Pageable pageable);

    AdminUserRes setEnabled(Long id, boolean enabled);

    Page<PropertyRes> properties(PropertyStatus status, Pageable pageable);

    PropertyRes verify(Long id);

    PropertyRes reject(Long id, String reason);

    PropertyRes takeDown(Long id, String reason);

    List<ContractRes> rentals(ContractStatus status);

    List<PaymentRes> overduePayments();

    List<ComplaintRes> complaints(ComplaintStatus status);

    ComplaintRes resolve(Long id, ComplaintResolveReq req);
}
