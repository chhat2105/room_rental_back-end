package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
    List<RentalContract> findByApplicationTenantIdOrderByIdDesc(Long tenantId);
    List<RentalContract> findByApplicationPropertyLandlordIdOrderByIdDesc(Long landlordId);
    List<RentalContract> findByApplicationTenantIdAndStatusAndEndDateBetween(Long tenantId, ContractStatus s, LocalDate from, LocalDate to);
    List<RentalContract> findByApplicationPropertyLandlordIdAndStatusAndEndDateBetween(Long landlordId, ContractStatus s, LocalDate from, LocalDate to);
    boolean existsByApplicationTenantIdAndApplicationPropertyIdAndStatus(Long tenantId, Long propertyId, ContractStatus s);
    List<RentalContract> findByApplicationTenantIdAndStatus(Long tenantId, ContractStatus s);
    long countByApplicationTenantIdAndStatus(Long tenantId, ContractStatus s);
    long countByApplicationPropertyLandlordIdAndStatus(Long landlordId, ContractStatus s);
    long countByStatus(ContractStatus s);
    List<RentalContract> findByStatusOrderByIdDesc(ContractStatus s);
    List<RentalContract> findAllByOrderByIdDesc();
}
