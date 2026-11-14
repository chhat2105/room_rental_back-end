package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.RentHubReq.ViewingReq;
import com.chhatkh.dev.payload.response.RentHubRes.ViewingRes;

import java.util.List;

public interface ViewingService {
    ViewingRes request(ViewingReq req);

    List<ViewingRes> mine();

    List<ViewingRes> incoming();

    ViewingRes decide(Long id, boolean approve);

    ViewingRes cancel(Long id);
}
