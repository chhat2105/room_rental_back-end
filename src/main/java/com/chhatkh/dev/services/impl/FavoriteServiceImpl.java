package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Favorite;
import com.chhatkh.dev.models.Property;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.payload.response.RentHubRes.PropertyRes;
import com.chhatkh.dev.repository.FavoriteRepository;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.FavoriteService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepo;
    private final PropertyRepository propertyRepo;
    private final CurrentUser currentUser;

    @Override
    @Transactional(readOnly = true)
    public List<PropertyRes> mine() {
        return favoriteRepo.findByUserId(currentUser.get().getId()).stream()
                .map(f -> Mapper.property(f.getProperty())).toList();
    }

    @Override
    public void add(Long propertyId) {
        User me = currentUser.get();
        if (favoriteRepo.findByUserIdAndPropertyId(me.getId(), propertyId).isPresent()) return;
        Property p = propertyRepo.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Favorite f = new Favorite();
        f.setUser(me);
        f.setProperty(p);
        favoriteRepo.save(f);
    }

    @Override
    public void remove(Long propertyId) {
        favoriteRepo.findByUserIdAndPropertyId(currentUser.get().getId(), propertyId).ifPresent(favoriteRepo::delete);
    }
}
