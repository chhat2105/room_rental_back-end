package com.thonsokvechey.renthubapi.payload.request;

import jakarta.validation.constraints.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Set;

public final class RentHubReq {
    private RentHubReq() {}

    /** Tenant search + filters (bound from query string). amenityIds=1,2 means "must have all". */
    public record PropertyFilter(String q, PropertyType type, BigDecimal minPrice, BigDecimal maxPrice,
                                 Integer bedrooms, Boolean furnished, List<Long> amenityIds,
                                 Double lat, Double lng, Double radiusKm) {}

    public record PropertyReq(@NotBlank String title, String description, @NotNull PropertyType propertyType,
                              @NotBlank String address, Double latitude, Double longitude,
                              @NotNull @Positive BigDecimal price, Boolean furnished, Set<Long> amenityIds) {}
    public record ImageReq(@NotBlank String imageUrl) {}
    public record RoomReq(@NotBlank String roomNumber, Integer bedrooms, Integer bathrooms, Double size,
                          @NotNull @Positive BigDecimal price) {}
    public record AmenityReq(@NotBlank String name) {}
    public record ViewingReq(@NotNull Long propertyId, @NotNull @FutureOrPresent LocalDate date, @NotNull LocalTime time) {}
    public record ApplicationReq(@NotNull Long propertyId, Long roomId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {}
    public record ApproveReq(@Positive BigDecimal deposit) {}
    public record PayReq(@NotNull PaymentMethod method) {}
    public record RenewReq(@Min(1) @Max(60) int months) {}
    public record ReviewReq(@NotNull Long propertyId, @Min(1) @Max(5) int rating, String comment) {}
    public record MessageReq(@NotNull Long receiverId, @NotBlank String message) {}
    public record ComplaintReq(@NotNull Long propertyId, @NotBlank String description) {}

    // admin
    public record RejectReq(@NotBlank String reason) {}
    public record ComplaintResolveReq(@NotNull ComplaintStatus status, String resolution) {}
}
