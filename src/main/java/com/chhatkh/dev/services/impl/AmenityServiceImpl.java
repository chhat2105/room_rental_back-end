package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.models.Amenity;
import com.chhatkh.dev.payload.request.RentHubReq.AmenityReq;
import com.chhatkh.dev.payload.response.RentHubRes.AmenityRes;
import com.chhatkh.dev.repository.AmenityRepository;
import com.chhatkh.dev.services.AmenityService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository repo;

    @Override
    @Transactional(readOnly = true)
    public List<AmenityRes> all() {
        return repo.findAll().stream().map(Mapper::amenity).toList();
    }

    @Override
    public AmenityRes create(AmenityReq req) {
        if (repo.existsByNameIgnoreCase(req.name())) throw new BadRequestException("Amenity already exists");
        Amenity a = new Amenity();
        a.setName(req.name().trim());
        return Mapper.amenity(repo.save(a));
    }
}
