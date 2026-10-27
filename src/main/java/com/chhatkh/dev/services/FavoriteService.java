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
