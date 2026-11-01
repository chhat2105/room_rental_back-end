package com.thonsokvechey.renthubapi.repository;

import com.thonsokvechey.renthubapi.models.*;
import com.thonsokvechey.renthubapi.models.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findByLandlordIdOrderByCreatedAtDesc(Long landlordId);
    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);
    List<Property> findTop6ByStatusOrderByCreatedAtDesc(PropertyStatus status);
    long countByStatus(PropertyStatus status);
}
