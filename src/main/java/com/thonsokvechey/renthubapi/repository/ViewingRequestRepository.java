package com.thonsokvechey.renthubapi.repository;

import com.thonsokvechey.renthubapi.models.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface ViewingRequestRepository extends JpaRepository<ViewingRequest, Long> {
    List<ViewingRequest> findByTenantIdOrderByDateDesc(Long tenantId);
    List<ViewingRequest> findByPropertyLandlordIdOrderByDateDesc(Long landlordId);
    long countByPropertyLandlordIdAndStatus(Long landlordId, ViewingStatus status);
}
