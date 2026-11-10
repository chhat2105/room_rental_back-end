package com.chhatkh.dev.payload.response;

import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public final class RentHubRes {
    private RentHubRes() {}

    public record ImageRes(Long id, String url) {}
    public record AmenityRes(Long id, String name) {}
    public record RoomRes(Long id, Long propertyId, String roomNumber, Integer bedrooms, Integer bathrooms,
                          Double size, BigDecimal price, RoomStatus status) {}
    public record PropertyRes(Long id, Long landlordId, String landlordName, String title, String description,
                              PropertyType propertyType, String address, Double latitude, Double longitude,
                              BigDecimal price, Boolean furnished, PropertyStatus status, String rejectionReason,
                              LocalDateTime createdAt, List<ImageRes> images, List<AmenityRes> amenities,
                              List<RoomRes> rooms) {}
    public record ViewingRes(Long id, Long tenantId, String tenantName, Long propertyId, String propertyTitle,
                             LocalDate date, LocalTime time, ViewingStatus status) {}
    public record ApplicationRes(Long id, Long tenantId, String tenantName, Long propertyId, String propertyTitle,
                                 Long roomId, LocalDate startDate, LocalDate endDate, ApplicationStatus status) {}
    public record ContractRes(Long id, Long applicationId, Long propertyId, String propertyTitle, Long tenantId,
                              String tenantName, BigDecimal rent, BigDecimal deposit, LocalDate startDate,
                              LocalDate endDate, ContractStatus status, LocalDateTime movedInAt) {}
    public record PaymentRes(Long id, Long contractId, PaymentType type, BigDecimal amount, LocalDate dueDate,
                             LocalDateTime paymentDate, PaymentMethod paymentMethod, PaymentStatus status) {}
    public record ReviewRes(Long id, Long tenantId, String tenantName, Long propertyId, Integer rating, String comment) {}
    public record ChatMessageRes(Long id, Long senderId, Long receiverId, String message, LocalDateTime createdAt) {}
    public record ContactRes(Long id, String name) {}
    public record ComplaintRes(Long id, Long tenantId, String tenantName, Long propertyId, String propertyTitle,
                               String description, ComplaintStatus status, String resolution) {}

    public record UserProfileRes(Long id, String name, String email, String phone, String profileImage,
                                 long completedRentals, long activeRentals) {}
    public record AdminUserRes(Long id, String name, String email, String phone, boolean enabled) {}

    public record TenantHomeRes(List<PropertyRes> latestProperties, int favoritesCount, List<ContractRes> activeContracts,
                                List<ViewingRes> upcomingViewings, List<PaymentRes> duePayments) {}
    public record LandlordDashboardRes(long totalProperties, long publishedProperties, long pendingReview,
                                       long pendingApplications, long pendingViewings, long activeContracts,
                                       long expiringIn30Days, long overduePayments, long openComplaints,
                                       BigDecimal revenueThisMonth) {}
    public record AdminDashboardRes(long totalUsers, long pendingListings, long publishedListings, long activeRentals,
                                    long overduePayments, long openComplaints) {}
}
