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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final PropertyRepository propertyRepo;
    private final RentalApplicationRepository appRepo;
    private final RentalContractRepository contractRepo;
    private final ViewingRequestRepository viewingRepo;
    private final PaymentRepository paymentRepo;
    private final ComplaintRepository complaintRepo;
    private final FavoriteRepository favoriteRepo;
    private final CurrentUser currentUser;

    /** Tenant "Home" screen */
    public TenantHomeRes tenantHome() {
        Long me = currentUser.get().getId();
        return new TenantHomeRes(
                propertyRepo.findTop6ByStatusOrderByCreatedAtDesc(PropertyStatus.PUBLISHED).stream().map(Mapper::property).toList(),
                favoriteRepo.findByUserId(me).size(),
                contractRepo.findByApplicationTenantIdAndStatus(me, ContractStatus.ACTIVE).stream().map(Mapper::contract).toList(),
                viewingRepo.findByTenantIdOrderByDateDesc(me).stream()
                        .filter(v -> v.getStatus() == ViewingStatus.APPROVED && !v.getDate().isBefore(LocalDate.now()))
                        .map(Mapper::viewing).toList(),
                paymentRepo.findByContractApplicationTenantIdAndStatusInOrderByDueDateAsc(me,
                        List.of(PaymentStatus.PENDING, PaymentStatus.OVERDUE)).stream().map(Mapper::payment).toList());
    }

    /** Landlord dashboard */
    public LandlordDashboardRes landlord() {
        Long me = currentUser.get().getId();
        List<Property> props = propertyRepo.findByLandlordIdOrderByCreatedAtDesc(me);
        LocalDate today = LocalDate.now();
        LocalDateTime from = YearMonth.now().atDay(1).atStartOfDay();
        return new LandlordDashboardRes(
                props.size(),
                props.stream().filter(p -> p.getStatus() == PropertyStatus.PUBLISHED).count(),
                props.stream().filter(p -> p.getStatus() == PropertyStatus.PENDING_REVIEW).count(),
                appRepo.countByPropertyLandlordIdAndStatus(me, ApplicationStatus.PENDING),
                viewingRepo.countByPropertyLandlordIdAndStatus(me, ViewingStatus.PENDING),
                contractRepo.countByApplicationPropertyLandlordIdAndStatus(me, ContractStatus.ACTIVE),
                contractRepo.findByApplicationPropertyLandlordIdAndStatusAndEndDateBetween(me, ContractStatus.ACTIVE, today, today.plusDays(30)).size(),
                paymentRepo.countByContractApplicationPropertyLandlordIdAndStatus(me, PaymentStatus.OVERDUE),
                complaintRepo.countByPropertyLandlordIdAndStatus(me, ComplaintStatus.OPEN),
                paymentRepo.revenue(me, PaymentStatus.PAID, from, from.plusMonths(1)));
    }
}
