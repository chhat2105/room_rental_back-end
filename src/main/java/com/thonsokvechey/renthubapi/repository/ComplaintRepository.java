package com.thonsokvechey.renthubapi.repository;

import com.thonsokvechey.renthubapi.models.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByTenantIdOrderByIdDesc(Long tenantId);
    List<Complaint> findByPropertyLandlordIdOrderByIdDesc(Long landlordId);
    long countByStatus(ComplaintStatus status);
    long countByPropertyLandlordIdAndStatus(Long landlordId, ComplaintStatus status);
    List<Complaint> findByStatusOrderByIdDesc(ComplaintStatus status);
    List<Complaint> findAllByOrderByIdDesc();
}
