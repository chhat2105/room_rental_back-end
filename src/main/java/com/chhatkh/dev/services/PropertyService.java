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

import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {
    private final PropertyRepository propertyRepo;
    private final RoomRepository roomRepo;
    private final AmenityRepository amenityRepo;
    private final CurrentUser currentUser;
    private final FileStorageService storage;

    // ---------- Tenant: browse / search / details ----------
    @Transactional(readOnly = true)
    public Page<PropertyRes> search(String q, PropertyType type, BigDecimal min, BigDecimal max, Integer bedrooms, Pageable pageable) {
        Specification<Property> spec = (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), PropertyStatus.PUBLISHED));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase() + "%";
                ps.add(cb.or(cb.like(cb.lower(root.get("title")), like), cb.like(cb.lower(root.get("address")), like)));
            }
            if (type != null) ps.add(cb.equal(root.get("propertyType"), type));
            if (min != null) ps.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(root.get("price"), max));
            if (bedrooms != null) {
                Join<Property, Room> r = root.join("rooms", JoinType.LEFT);
                ps.add(cb.greaterThanOrEqualTo(r.get("bedrooms"), bedrooms));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return propertyRepo.findAll(spec, pageable).map(Mapper::property);
    }

    @Transactional(readOnly = true)
    public PropertyRes get(Long id) {
        Property p = find(id);
        if (p.getStatus() != PropertyStatus.PUBLISHED && p.getStatus() != PropertyStatus.RENTED) {
            // drafts are visible only to their owner
            Long me = null;
            try { me = currentUser.get().getId(); } catch (RuntimeException ignored) { }
            if (me == null || !p.getLandlord().getId().equals(me)) throw new ResourceNotFoundException("Property not found");
        }
        return Mapper.property(p);
    }

    // ---------- Landlord: my properties ----------
    @Transactional(readOnly = true)
    public List<PropertyRes> mine() {
        return propertyRepo.findByLandlordIdOrderByCreatedAtDesc(currentUser.get().getId())
                .stream().map(Mapper::property).toList();
    }

    public PropertyRes create(PropertyReq req) {
        Property p = new Property();
        p.setLandlord(currentUser.get());
        apply(p, req);
        return Mapper.property(propertyRepo.save(p));
    }

    public PropertyRes update(Long id, PropertyReq req) {
        Property p = owned(id);
        apply(p, req);
        return Mapper.property(p);
    }

    public void delete(Long id) { propertyRepo.delete(owned(id)); }

    // ---------- Photos ----------
    public PropertyRes addImage(Long id, MultipartFile file) { return addImageUrl(id, storage.save(file)); }

    public PropertyRes addImageUrl(Long id, String url) {
        Property p = owned(id);
        PropertyImage img = new PropertyImage();
        img.setProperty(p); img.setImageUrl(url);
        p.getImages().add(img);
        propertyRepo.flush();
        return Mapper.property(p);
    }

    public PropertyRes removeImage(Long id, Long imageId) {
        Property p = owned(id);
        if (!p.getImages().removeIf(i -> i.getId().equals(imageId))) throw new ResourceNotFoundException("Image not found");
        return Mapper.property(p);
    }

    // ---------- Rooms ----------
    public RoomRes addRoom(Long propertyId, RoomReq req) {
        Property p = owned(propertyId);
        Room r = new Room();
        r.setProperty(p);
        applyRoom(r, req);
        p.getRooms().add(r);
        propertyRepo.flush();
        return Mapper.room(r);
    }

    public RoomRes updateRoom(Long roomId, RoomReq req) {
        Room r = ownedRoom(roomId);
        applyRoom(r, req);
        return Mapper.room(r);
    }

    public void deleteRoom(Long roomId) {
        Room r = ownedRoom(roomId);
        if (r.getStatus() != RoomStatus.AVAILABLE) throw new BadRequestException("Room is reserved or occupied");
        r.getProperty().getRooms().remove(r);
    }

    // ---------- Publish ----------
    public PropertyRes publish(Long id) {
        Property p = owned(id);
        if (p.getImages().isEmpty()) throw new BadRequestException("Upload at least one photo before publishing");
        if (p.getPrice() == null) throw new BadRequestException("Set a price before publishing");
        p.setStatus(PropertyStatus.PUBLISHED);
        return Mapper.property(p);
    }

    public PropertyRes unpublish(Long id) {
        Property p = owned(id);
        p.setStatus(PropertyStatus.UNPUBLISHED);
        return Mapper.property(p);
    }

    // ---------- helpers ----------
    private void apply(Property p, PropertyReq r) {
        p.setTitle(r.title()); p.setDescription(r.description()); p.setPropertyType(r.propertyType());
        p.setAddress(r.address()); p.setLatitude(r.latitude()); p.setLongitude(r.longitude()); p.setPrice(r.price());
        if (r.amenityIds() != null) p.setAmenities(new HashSet<>(amenityRepo.findAllById(r.amenityIds())));
    }
    private void applyRoom(Room r, RoomReq q) {
        r.setRoomNumber(q.roomNumber()); r.setBedrooms(q.bedrooms()); r.setBathrooms(q.bathrooms());
        r.setSize(q.size()); r.setPrice(q.price());
    }
    private Property find(Long id) { return propertyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found")); }
    private Property owned(Long id) {
        Property p = find(id);
        if (!p.getLandlord().getId().equals(currentUser.get().getId())) throw new ForbiddenException("This is not your property");
        return p;
    }
    private Room ownedRoom(Long roomId) {
        Room r = roomRepo.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!r.getProperty().getLandlord().getId().equals(currentUser.get().getId())) throw new ForbiddenException("This is not your property");
        return r;
    }
}
