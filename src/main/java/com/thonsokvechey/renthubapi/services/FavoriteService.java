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
public class FavoriteService {
    private final FavoriteRepository favoriteRepo;
    private final PropertyRepository propertyRepo;
    private final CurrentUser currentUser;

    @Transactional(readOnly = true)
    public List<PropertyRes> mine() {
        return favoriteRepo.findByUserId(currentUser.get().getId()).stream()
                .map(f -> Mapper.property(f.getProperty())).toList();
    }

    public void add(Long propertyId) {
        User me = currentUser.get();
        if (favoriteRepo.findByUserIdAndPropertyId(me.getId(), propertyId).isPresent()) return;
        Property p = propertyRepo.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Favorite f = new Favorite(); f.setUser(me); f.setProperty(p);
        favoriteRepo.save(f);
    }

    public void remove(Long propertyId) {
        favoriteRepo.findByUserIdAndPropertyId(currentUser.get().getId(), propertyId).ifPresent(favoriteRepo::delete);
    }
}
