package com.thonsokvechey.renthubapi.repository;

import com.thonsokvechey.renthubapi.models.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByContractIdOrderByDueDateAsc(Long contractId);
    List<Payment> findByStatusAndDueDateBefore(PaymentStatus status, LocalDate date);
    List<Payment> findByContractApplicationTenantIdAndStatusInOrderByDueDateAsc(Long tenantId, Collection<PaymentStatus> statuses);
    List<Payment> findByContractApplicationPropertyLandlordIdOrderByDueDateAsc(Long landlordId);
    long countByContractApplicationPropertyLandlordIdAndStatus(Long landlordId, PaymentStatus status);
    long countByStatus(PaymentStatus status);
    List<Payment> findByStatusOrderByDueDateAsc(PaymentStatus status);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.contract.application.property.landlord.id = :lid " +
           "and p.status = :status and p.paymentDate >= :from and p.paymentDate < :to")
    BigDecimal revenue(@Param("lid") Long landlordId, @Param("status") PaymentStatus status,
                       @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
