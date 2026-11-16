package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.exception.ForbiddenException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Complaint;
import com.chhatkh.dev.models.Property;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.models.enums.ComplaintStatus;
import com.chhatkh.dev.models.enums.ContractStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ComplaintReq;
import com.chhatkh.dev.payload.response.RentHubRes.ComplaintRes;
import com.chhatkh.dev.repository.ComplaintRepository;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.repository.RentalContractRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.ComplaintService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintRepository complaintRepo;
    private final PropertyRepository propertyRepo;
    private final RentalContractRepository contractRepo;
    private final CurrentUser currentUser;

    @Override
    public ComplaintRes create(ComplaintReq req) {
        User me = currentUser.get();
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!contractRepo.existsByApplicationTenantIdAndApplicationPropertyIdAndStatus(me.getId(), p.getId(), ContractStatus.ACTIVE))
            throw new BadRequestException("You can only file a complaint for a property you are currently renting");
        Complaint c = new Complaint();
        c.setTenant(me);
        c.setProperty(p);
        c.setDescription(req.description());
        return Mapper.complaint(complaintRepo.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRes> mine() {
        return complaintRepo.findByTenantIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::complaint).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRes> incoming() {
        return complaintRepo.findByPropertyLandlordIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::complaint).toList();
    }

    @Override
    public ComplaintRes updateStatus(Long id, ComplaintStatus status) {
        Complaint c = complaintRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        if (!c.getProperty().getLandlord().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your property");
        c.setStatus(status);
        return Mapper.complaint(c);
    }
}
