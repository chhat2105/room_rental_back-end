package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.response.RentHubRes.PropertyRes;

import java.util.List;

public interface FavoriteService {
    List<PropertyRes> mine();

    void add(Long propertyId);

    void remove(Long propertyId);
}
