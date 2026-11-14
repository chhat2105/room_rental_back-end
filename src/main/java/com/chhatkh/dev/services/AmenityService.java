package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.RentHubReq.AmenityReq;
import com.chhatkh.dev.payload.response.RentHubRes.AmenityRes;

import java.util.List;

public interface AmenityService {
    List<AmenityRes> all();

    AmenityRes create(AmenityReq req);
}
