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
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final PropertyRepository propertyRepo;
    private final RentalContractRepository contractRepo;
    private final CurrentUser currentUser;

    public ReviewRes create(ReviewReq req) {
        User me = currentUser.get();
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!contractRepo.existsByApplicationTenantIdAndApplicationPropertyIdAndStatus(me.getId(), p.getId(), ContractStatus.COMPLETED))
            throw new BadRequestException("You can review a property after your rental is completed");
        if (reviewRepo.existsByTenantIdAndPropertyId(me.getId(), p.getId())) throw new BadRequestException("You already reviewed this property");
        Review r = new Review();
        r.setTenant(me); r.setProperty(p); r.setRating(req.rating()); r.setComment(req.comment());
        return Mapper.review(reviewRepo.save(r));
    }

    @Transactional(readOnly = true)
    public List<ReviewRes> forProperty(Long propertyId) {
        return reviewRepo.findByPropertyIdOrderByIdDesc(propertyId).stream().map(Mapper::review).toList();
    }
}
