package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Property;
import com.chhatkh.dev.models.Review;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.models.enums.ContractStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ReviewReq;
import com.chhatkh.dev.payload.response.RentHubRes.ReviewRes;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.repository.RentalContractRepository;
import com.chhatkh.dev.repository.ReviewRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.ReviewService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepo;
    private final PropertyRepository propertyRepo;
    private final RentalContractRepository contractRepo;
    private final CurrentUser currentUser;

    @Override
    public ReviewRes create(ReviewReq req) {
        User me = currentUser.get();
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!contractRepo.existsByApplicationTenantIdAndApplicationPropertyIdAndStatus(me.getId(), p.getId(), ContractStatus.COMPLETED))
            throw new BadRequestException("You can review a property after your rental is completed");
        if (reviewRepo.existsByTenantIdAndPropertyId(me.getId(), p.getId()))
            throw new BadRequestException("You already reviewed this property");
        Review r = new Review();
        r.setTenant(me);
        r.setProperty(p);
        r.setRating(req.rating());
        r.setComment(req.comment());
        return Mapper.review(reviewRepo.save(r));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewRes> forProperty(Long propertyId) {
        return reviewRepo.findByPropertyIdOrderByIdDesc(propertyId).stream().map(Mapper::review).toList();
    }
}
