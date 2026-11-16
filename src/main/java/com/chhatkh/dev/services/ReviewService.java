package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.RentHubReq.ReviewReq;
import com.chhatkh.dev.payload.response.RentHubRes.ReviewRes;

import java.util.List;

public interface ReviewService {
    ReviewRes create(ReviewReq req);

    List<ReviewRes> forProperty(Long propertyId);
}
