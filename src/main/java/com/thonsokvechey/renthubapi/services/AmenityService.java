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
