package com.chhatkh.dev.services;

import com.chhatkh.dev.exception.*;
import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.request.RentHubReq.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import com.chhatkh.dev.repository.*;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {
    private final UserRepository userRepo;
    private final RentalApplicationRepository appRepo;
    private final RentalContractRepository contractRepo;
    private final CurrentUser currentUser;

    /** Allowed: yourself, admin, or someone you have a rental application with (landlord views tenant, tenant views landlord). */
    public UserProfileRes get(Long id) {
        User me = currentUser.get();
        boolean allowed = me.getId().equals(id) || currentUser.hasRole("ADMIN")
                || appRepo.existsByTenantIdAndPropertyLandlordId(id, me.getId())
                || appRepo.existsByTenantIdAndPropertyLandlordId(me.getId(), id);
        if (!allowed) throw new ForbiddenException("You cannot view this profile");
        User u = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return Mapper.profile(u,
                contractRepo.countByApplicationTenantIdAndStatus(id, ContractStatus.COMPLETED),
                contractRepo.countByApplicationTenantIdAndStatus(id, ContractStatus.ACTIVE));
    }
}
