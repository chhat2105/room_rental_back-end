package com.chhatkh.dev.payload.request;

import jakarta.validation.constraints.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.Set;

public final class RentHubReq {
    private RentHubReq() {}

    public record PropertyReq(@NotBlank String title, String description, @NotNull PropertyType propertyType,
                              @NotBlank String address, Double latitude, Double longitude,
                              @NotNull @Positive BigDecimal price, Set<Long> amenityIds) {}
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
}
