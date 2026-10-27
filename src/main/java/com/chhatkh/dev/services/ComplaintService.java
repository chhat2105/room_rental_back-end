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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintService {
    private final ComplaintRepository complaintRepo;
    private final PropertyRepository propertyRepo;
    private final RentalContractRepository contractRepo;
    private final CurrentUser currentUser;

    public ComplaintRes create(ComplaintReq req) {
        User me = currentUser.get();
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!contractRepo.existsByApplicationTenantIdAndApplicationPropertyIdAndStatus(me.getId(), p.getId(), ContractStatus.ACTIVE))
            throw new BadRequestException("You can only file a complaint for a property you are currently renting");
        Complaint c = new Complaint();
        c.setTenant(me); c.setProperty(p); c.setDescription(req.description());
        return Mapper.complaint(complaintRepo.save(c));
    }

    @Transactional(readOnly = true)
    public List<ComplaintRes> mine() {
        return complaintRepo.findByTenantIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::complaint).toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintRes> incoming() {
        return complaintRepo.findByPropertyLandlordIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::complaint).toList();
    }

    public ComplaintRes updateStatus(Long id, ComplaintStatus status) {
        Complaint c = complaintRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        if (!c.getProperty().getLandlord().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your property");
        c.setStatus(status);
        return Mapper.complaint(c);
    }
}
