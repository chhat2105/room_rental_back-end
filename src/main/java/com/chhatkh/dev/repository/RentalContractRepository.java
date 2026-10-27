package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
    List<RentalContract> findByApplicationTenantIdOrderByIdDesc(Long tenantId);
    List<RentalContract> findByApplicationPropertyLandlordIdOrderByIdDesc(Long landlordId);
    List<RentalContract> findByApplicationTenantIdAndStatusAndEndDateBetween(Long tenantId, ContractStatus s, LocalDate from, LocalDate to);
    List<RentalContract> findByApplicationPropertyLandlordIdAndStatusAndEndDateBetween(Long landlordId, ContractStatus s, LocalDate from, LocalDate to);
    boolean existsByApplicationTenantIdAndApplicationPropertyIdAndStatus(Long tenantId, Long propertyId, ContractStatus s);
}
