package com.chhatkh.dev.services;

import com.chhatkh.dev.constants.Constants;
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
@Transactional
public class AdminService {
    private final UserRepository userRepo;
    private final PropertyRepository propertyRepo;
    private final RentalContractRepository contractRepo;
    private final PaymentRepository paymentRepo;
    private final ComplaintRepository complaintRepo;
    private final CurrentUser currentUser;

    // ---------- Dashboard ----------
    @Transactional(readOnly = true)
    public AdminDashboardRes dashboard() {
        return new AdminDashboardRes(userRepo.count(),
                propertyRepo.countByStatus(PropertyStatus.PENDING_REVIEW),
                propertyRepo.countByStatus(PropertyStatus.PUBLISHED),
                contractRepo.countByStatus(ContractStatus.ACTIVE),
                paymentRepo.countByStatus(PaymentStatus.OVERDUE),
                complaintRepo.countByStatus(ComplaintStatus.OPEN));
    }

    // ---------- Users: manage ----------
    @Transactional(readOnly = true)
    public Page<AdminUserRes> users(Pageable pageable) { return userRepo.findAll(pageable).map(Mapper::adminUser); }

    public AdminUserRes setEnabled(Long id, boolean enabled) {
        if (currentUser.get().getId().equals(id)) throw new BadRequestException("You cannot disable your own account");
        User u = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setStatus(enabled ? Constants.STATUS_ACTIVE : Constants.STATUS_DELETE);
        return Mapper.adminUser(u);
    }

    // ---------- Properties: verify listings ----------
    @Transactional(readOnly = true)
    public Page<PropertyRes> properties(PropertyStatus status, Pageable pageable) {
        return propertyRepo.findByStatus(status == null ? PropertyStatus.PENDING_REVIEW : status, pageable).map(Mapper::property);
    }

    public PropertyRes verify(Long id) {
        Property p = property(id);
        if (p.getStatus() != PropertyStatus.PENDING_REVIEW) throw new BadRequestException("Listing is not waiting for review");
        p.setStatus(PropertyStatus.PUBLISHED); p.setRejectionReason(null);
        return Mapper.property(p);
    }

    public PropertyRes reject(Long id, String reason) {
        Property p = property(id);
        if (p.getStatus() != PropertyStatus.PENDING_REVIEW) throw new BadRequestException("Listing is not waiting for review");
        p.setStatus(PropertyStatus.REJECTED); p.setRejectionReason(reason);
        return Mapper.property(p);
    }

    /** Take a live listing down (fraud, wrong info...). */
    public PropertyRes takeDown(Long id, String reason) {
        Property p = property(id);
        if (p.getStatus() != PropertyStatus.PUBLISHED) throw new BadRequestException("Only published listings can be taken down");
        p.setStatus(PropertyStatus.UNPUBLISHED); p.setRejectionReason(reason);
        return Mapper.property(p);
    }

    // ---------- Rentals: monitor ----------
    @Transactional(readOnly = true)
    public List<ContractRes> rentals(ContractStatus status) {
        return (status == null ? contractRepo.findAllByOrderByIdDesc() : contractRepo.findByStatusOrderByIdDesc(status))
                .stream().map(Mapper::contract).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentRes> overduePayments() {
        return paymentRepo.findByStatusOrderByDueDateAsc(PaymentStatus.OVERDUE).stream().map(Mapper::payment).toList();
    }

    // ---------- Complaints: resolve ----------
    @Transactional(readOnly = true)
    public List<ComplaintRes> complaints(ComplaintStatus status) {
        return (status == null ? complaintRepo.findAllByOrderByIdDesc() : complaintRepo.findByStatusOrderByIdDesc(status))
                .stream().map(Mapper::complaint).toList();
    }

    public ComplaintRes resolve(Long id, ComplaintResolveReq req) {
        Complaint c = complaintRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        c.setStatus(req.status()); c.setResolution(req.resolution());
        return Mapper.complaint(c);
    }

    private Property property(Long id) { return propertyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found")); }
}
