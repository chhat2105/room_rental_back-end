package com.thonsokvechey.renthubapi.repository;

import com.thonsokvechey.renthubapi.models.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {
    List<RentalApplication> findByTenantIdOrderByIdDesc(Long tenantId);
    List<RentalApplication> findByPropertyLandlordIdOrderByIdDesc(Long landlordId);
    boolean existsByTenantIdAndPropertyLandlordId(Long tenantId, Long landlordId);
    long countByPropertyLandlordIdAndStatus(Long landlordId, ApplicationStatus status);
}
