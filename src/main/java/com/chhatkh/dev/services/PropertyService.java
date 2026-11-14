package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.RentHubReq.PropertyFilter;
import com.chhatkh.dev.payload.request.RentHubReq.PropertyReq;
import com.chhatkh.dev.payload.request.RentHubReq.RoomReq;
import com.chhatkh.dev.payload.response.RentHubRes.PropertyRes;
import com.chhatkh.dev.payload.response.RentHubRes.RoomRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {
    Page<PropertyRes> search(PropertyFilter f, Pageable pageable);

    PropertyRes get(Long id);

    List<PropertyRes> mine();

    PropertyRes create(PropertyReq req);

    PropertyRes update(Long id, PropertyReq req);

    void delete(Long id);

    PropertyRes addImage(Long id, MultipartFile file);

    PropertyRes addImageUrl(Long id, String url);

    PropertyRes removeImage(Long id, Long imageId);

    RoomRes addRoom(Long propertyId, RoomReq req);

    RoomRes updateRoom(Long roomId, RoomReq req);

    void deleteRoom(Long roomId);

    PropertyRes publish(Long id);

    PropertyRes unpublish(Long id);
}
