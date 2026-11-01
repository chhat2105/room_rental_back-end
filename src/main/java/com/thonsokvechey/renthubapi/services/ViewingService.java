package com.thonsokvechey.renthubapi.services;

import com.thonsokvechey.renthubapi.exception.*;
import com.thonsokvechey.renthubapi.models.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import com.thonsokvechey.renthubapi.payload.request.RentHubReq.*;
import com.thonsokvechey.renthubapi.payload.response.RentHubRes.*;
import com.thonsokvechey.renthubapi.repository.*;
import com.thonsokvechey.renthubapi.security.CurrentUser;
import com.thonsokvechey.renthubapi.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ViewingService {
    private final ViewingRequestRepository viewingRepo;
    private final PropertyRepository propertyRepo;
    private final CurrentUser currentUser;

    public ViewingRes request(ViewingReq req) {
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (p.getStatus() != PropertyStatus.PUBLISHED) throw new BadRequestException("Property is not available");
        ViewingRequest v = new ViewingRequest();
        v.setTenant(currentUser.get()); v.setProperty(p); v.setDate(req.date()); v.setTime(req.time());
        return Mapper.viewing(viewingRepo.save(v));
    }

    @Transactional(readOnly = true)
    public List<ViewingRes> mine() {
        return viewingRepo.findByTenantIdOrderByDateDesc(currentUser.get().getId()).stream().map(Mapper::viewing).toList();
    }

    @Transactional(readOnly = true)
    public List<ViewingRes> incoming() {
        return viewingRepo.findByPropertyLandlordIdOrderByDateDesc(currentUser.get().getId()).stream().map(Mapper::viewing).toList();
    }

    public ViewingRes decide(Long id, boolean approve) {
        ViewingRequest v = find(id);
        if (!v.getProperty().getLandlord().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your property");
        if (v.getStatus() != ViewingStatus.PENDING) throw new BadRequestException("Request already handled");
        v.setStatus(approve ? ViewingStatus.APPROVED : ViewingStatus.REJECTED);
        return Mapper.viewing(v);
    }

    public ViewingRes cancel(Long id) {
        ViewingRequest v = find(id);
        if (!v.getTenant().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your request");
        if (v.getStatus() == ViewingStatus.REJECTED) throw new BadRequestException("Request already rejected");
        v.setStatus(ViewingStatus.CANCELLED);
        return Mapper.viewing(v);
    }

    private ViewingRequest find(Long id) { return viewingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Viewing request not found")); }
}
