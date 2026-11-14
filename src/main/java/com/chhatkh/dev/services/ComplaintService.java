package com.chhatkh.dev.services;

import com.chhatkh.dev.models.enums.ComplaintStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ComplaintReq;
import com.chhatkh.dev.payload.response.RentHubRes.ComplaintRes;

import java.util.List;

public interface ComplaintService {
    ComplaintRes create(ComplaintReq req);

    List<ComplaintRes> mine();

    List<ComplaintRes> incoming();

    ComplaintRes updateStatus(Long id, ComplaintStatus status);
}
