package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.ForbiddenException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.models.enums.ContractStatus;
import com.chhatkh.dev.payload.response.RentHubRes.UserProfileRes;
import com.chhatkh.dev.repository.RentalApplicationRepository;
import com.chhatkh.dev.repository.RentalContractRepository;
import com.chhatkh.dev.repository.UserRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.UserProfileService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepo;
    private final RentalApplicationRepository appRepo;
    private final RentalContractRepository contractRepo;
    private final CurrentUser currentUser;

    /** Allowed: yourself, admin, or someone you have a rental application with (landlord views tenant, tenant views landlord). */
    @Override
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
