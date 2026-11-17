package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.request.RentHubReq.ImageReq;
import com.chhatkh.dev.payload.request.RentHubReq.PropertyReq;
import com.chhatkh.dev.payload.request.RentHubReq.RoomReq;
import com.chhatkh.dev.payload.response.RentHubRes.PropertyRes;
import com.chhatkh.dev.payload.response.RentHubRes.RoomRes;
import com.chhatkh.dev.services.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Landlord: My Properties -> add property, photos, rooms, price, publish */
@RestController
@RequestMapping("/api/landlord/properties")
@RequiredArgsConstructor
public class LandlordPropertyController {
    private final PropertyService service;

    @GetMapping
    public List<PropertyRes> mine() {
        return service.mine();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyRes create(@Valid @RequestBody PropertyReq req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PropertyRes update(@PathVariable Long id, @Valid @RequestBody PropertyReq req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // photos
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PropertyRes upload(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return service.addImage(id, file);
    }

    @PostMapping("/{id}/images/url")
    public PropertyRes addImageUrl(@PathVariable Long id, @Valid @RequestBody ImageReq req) {
        return service.addImageUrl(id, req.imageUrl());
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public PropertyRes removeImage(@PathVariable Long id, @PathVariable Long imageId) {
        return service.removeImage(id, imageId);
    }

    // rooms
    @PostMapping("/{id}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomRes addRoom(@PathVariable Long id, @Valid @RequestBody RoomReq req) {
        return service.addRoom(id, req);
    }

    @PutMapping("/rooms/{roomId}")
    public RoomRes updateRoom(@PathVariable Long roomId, @Valid @RequestBody RoomReq req) {
        return service.updateRoom(roomId, req);
    }

    @DeleteMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long roomId) {
        service.deleteRoom(roomId);
    }

    // publish
    @PostMapping("/{id}/publish")
    public PropertyRes publish(@PathVariable Long id) {
        return service.publish(id);
    }

    @PostMapping("/{id}/unpublish")
    public PropertyRes unpublish(@PathVariable Long id) {
        return service.unpublish(id);
    }
}
