package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.exception.ForbiddenException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Amenity;
import com.chhatkh.dev.models.Property;
import com.chhatkh.dev.models.PropertyImage;
import com.chhatkh.dev.models.Room;
import com.chhatkh.dev.models.enums.PropertyStatus;
import com.chhatkh.dev.models.enums.RoomStatus;
import com.chhatkh.dev.payload.request.RentHubReq.PropertyFilter;
import com.chhatkh.dev.payload.request.RentHubReq.PropertyReq;
import com.chhatkh.dev.payload.request.RentHubReq.RoomReq;
import com.chhatkh.dev.payload.response.RentHubRes.PropertyRes;
import com.chhatkh.dev.payload.response.RentHubRes.RoomRes;
import com.chhatkh.dev.repository.AmenityRepository;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.repository.RoomRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.FileStorageService;
import com.chhatkh.dev.services.PropertyService;
import com.chhatkh.dev.util.Mapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepo;
    private final RoomRepository roomRepo;
    private final AmenityRepository amenityRepo;
    private final CurrentUser currentUser;
    private final FileStorageService storage;

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyRes> search(PropertyFilter f, Pageable pageable) {
        Specification<Property> spec = (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), PropertyStatus.PUBLISHED));
            if (f != null) {
                if (f.q() != null && !f.q().isBlank()) {
                    String like = "%" + f.q().toLowerCase() + "%";
                    ps.add(cb.or(cb.like(cb.lower(root.get("title")), like), cb.like(cb.lower(root.get("address")), like)));
                }
                if (f.type() != null) ps.add(cb.equal(root.get("propertyType"), f.type()));
                if (f.minPrice() != null) ps.add(cb.greaterThanOrEqualTo(root.get("price"), f.minPrice()));
                if (f.maxPrice() != null) ps.add(cb.lessThanOrEqualTo(root.get("price"), f.maxPrice()));
                if (f.furnished() != null) ps.add(cb.equal(root.get("furnished"), f.furnished()));
                if (f.bedrooms() != null) {
                    Join<Property, Room> r = root.join("rooms", JoinType.LEFT);
                    ps.add(cb.greaterThanOrEqualTo(r.get("bedrooms"), f.bedrooms()));
                }
                if (f.amenityIds() != null) {
                    for (Long id : f.amenityIds()) {
                        Join<Property, Amenity> a = root.join("amenities", JoinType.INNER);
                        ps.add(cb.equal(a.get("id"), id));
                    }
                }
                if (f.lat() != null && f.lng() != null) {
                    double km = f.radiusKm() != null ? f.radiusKm() : 5.0;
                    double dLat = km / 111.0;
                    double dLng = km / (111.0 * Math.max(0.01, Math.cos(Math.toRadians(f.lat()))));
                    ps.add(cb.between(root.<Double>get("latitude"), f.lat() - dLat, f.lat() + dLat));
                    ps.add(cb.between(root.<Double>get("longitude"), f.lng() - dLng, f.lng() + dLng));
                }
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return propertyRepo.findAll(spec, pageable).map(Mapper::property);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyRes get(Long id) {
        Property p = find(id);
        if (p.getStatus() != PropertyStatus.PUBLISHED && p.getStatus() != PropertyStatus.RENTED) {
            Long me = null;
            try {
                me = currentUser.get().getId();
            } catch (RuntimeException ignored) {
            }
            if (!currentUser.hasRole("ADMIN") && (me == null || !p.getLandlord().getId().equals(me)))
                throw new ResourceNotFoundException("Property not found");
        }
        return Mapper.property(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyRes> mine() {
        return propertyRepo.findByLandlordIdOrderByCreatedAtDesc(currentUser.get().getId())
                .stream().map(Mapper::property).toList();
    }

    @Override
    public PropertyRes create(PropertyReq req) {
        Property p = new Property();
        p.setLandlord(currentUser.get());
        apply(p, req);
        return Mapper.property(propertyRepo.save(p));
    }

    @Override
    public PropertyRes update(Long id, PropertyReq req) {
        Property p = owned(id);
        apply(p, req);
        return Mapper.property(p);
    }

    @Override
    public void delete(Long id) {
        propertyRepo.delete(owned(id));
    }

    @Override
    public PropertyRes addImage(Long id, MultipartFile file) {
        return addImageUrl(id, storage.save(file));
    }

    @Override
    public PropertyRes addImageUrl(Long id, String url) {
        Property p = owned(id);
        PropertyImage img = new PropertyImage();
        img.setProperty(p);
        img.setImageUrl(url);
        p.getImages().add(img);
        propertyRepo.flush();
        return Mapper.property(p);
    }

    @Override
    public PropertyRes removeImage(Long id, Long imageId) {
        Property p = owned(id);
        if (!p.getImages().removeIf(i -> i.getId().equals(imageId)))
            throw new ResourceNotFoundException("Image not found");
        return Mapper.property(p);
    }

    @Override
    public RoomRes addRoom(Long propertyId, RoomReq req) {
        Property p = owned(propertyId);
        Room r = new Room();
        r.setProperty(p);
        applyRoom(r, req);
        p.getRooms().add(r);
        propertyRepo.flush();
        return Mapper.room(r);
    }

    @Override
    public RoomRes updateRoom(Long roomId, RoomReq req) {
        Room r = ownedRoom(roomId);
        applyRoom(r, req);
        return Mapper.room(r);
    }

    @Override
    public void deleteRoom(Long roomId) {
        Room r = ownedRoom(roomId);
        if (r.getStatus() != RoomStatus.AVAILABLE) throw new BadRequestException("Room is reserved or occupied");
        r.getProperty().getRooms().remove(r);
    }

    @Override
    public PropertyRes publish(Long id) {
        Property p = owned(id);
        if (p.getImages().isEmpty()) throw new BadRequestException("Upload at least one photo before publishing");
        if (p.getPrice() == null) throw new BadRequestException("Set a price before publishing");
        p.setStatus(PropertyStatus.PENDING_REVIEW);
        p.setRejectionReason(null);
        return Mapper.property(p);
    }

    @Override
    public PropertyRes unpublish(Long id) {
        Property p = owned(id);
        p.setStatus(PropertyStatus.UNPUBLISHED);
        return Mapper.property(p);
    }

    private void apply(Property p, PropertyReq r) {
        p.setTitle(r.title());
        p.setDescription(r.description());
        p.setPropertyType(r.propertyType());
        p.setAddress(r.address());
        p.setLatitude(r.latitude());
        p.setLongitude(r.longitude());
        p.setPrice(r.price());
        p.setFurnished(r.furnished() != null && r.furnished());
        if (r.amenityIds() != null) p.setAmenities(new HashSet<>(amenityRepo.findAllById(r.amenityIds())));
    }

    private void applyRoom(Room r, RoomReq q) {
        r.setRoomNumber(q.roomNumber());
        r.setBedrooms(q.bedrooms());
        r.setBathrooms(q.bathrooms());
        r.setSize(q.size());
        r.setPrice(q.price());
    }

    private Property find(Long id) {
        return propertyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
    }

    private Property owned(Long id) {
        Property p = find(id);
        if (!p.getLandlord().getId().equals(currentUser.get().getId()))
            throw new ForbiddenException("This is not your property");
        return p;
    }

    private Room ownedRoom(Long roomId) {
        Room r = roomRepo.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!r.getProperty().getLandlord().getId().equals(currentUser.get().getId()))
            throw new ForbiddenException("This is not your property");
        return r;
    }
}
