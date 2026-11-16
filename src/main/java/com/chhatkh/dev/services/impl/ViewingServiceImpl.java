package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.exception.ForbiddenException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Property;
import com.chhatkh.dev.models.ViewingRequest;
import com.chhatkh.dev.models.enums.PropertyStatus;
import com.chhatkh.dev.models.enums.ViewingStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ViewingReq;
import com.chhatkh.dev.payload.response.RentHubRes.ViewingRes;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.repository.ViewingRequestRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.ViewingService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ViewingServiceImpl implements ViewingService {
    private final ViewingRequestRepository viewingRepo;
    private final PropertyRepository propertyRepo;
    private final CurrentUser currentUser;

    @Override
    public ViewingRes request(ViewingReq req) {
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (p.getStatus() != PropertyStatus.PUBLISHED) throw new BadRequestException("Property is not available");
        ViewingRequest v = new ViewingRequest();
        v.setTenant(currentUser.get());
        v.setProperty(p);
        v.setDate(req.date());
        v.setTime(req.time());
        return Mapper.viewing(viewingRepo.save(v));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewingRes> mine() {
        return viewingRepo.findByTenantIdOrderByDateDesc(currentUser.get().getId()).stream().map(Mapper::viewing).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewingRes> incoming() {
        return viewingRepo.findByPropertyLandlordIdOrderByDateDesc(currentUser.get().getId()).stream().map(Mapper::viewing).toList();
    }

    @Override
    public ViewingRes decide(Long id, boolean approve) {
        ViewingRequest v = find(id);
        if (!v.getProperty().getLandlord().getId().equals(currentUser.get().getId()))
            throw new ForbiddenException("Not your property");
        if (v.getStatus() != ViewingStatus.PENDING) throw new BadRequestException("Request already handled");
        v.setStatus(approve ? ViewingStatus.APPROVED : ViewingStatus.REJECTED);
        return Mapper.viewing(v);
    }

    @Override
    public ViewingRes cancel(Long id) {
        ViewingRequest v = find(id);
        if (!v.getTenant().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your request");
        if (v.getStatus() == ViewingStatus.REJECTED) throw new BadRequestException("Request already rejected");
        v.setStatus(ViewingStatus.CANCELLED);
        return Mapper.viewing(v);
    }

    private ViewingRequest find(Long id) {
        return viewingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Viewing request not found"));
    }
}
