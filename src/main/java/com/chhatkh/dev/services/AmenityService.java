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
public class AmenityService {
    private final AmenityRepository repo;

    @Transactional(readOnly = true)
    public List<AmenityRes> all() { return repo.findAll().stream().map(Mapper::amenity).toList(); }

    public AmenityRes create(AmenityReq req) {
        if (repo.existsByNameIgnoreCase(req.name())) throw new BadRequestException("Amenity already exists");
        Amenity a = new Amenity(); a.setName(req.name().trim());
        return Mapper.amenity(repo.save(a));
    }
}
