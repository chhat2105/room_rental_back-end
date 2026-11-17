package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyIdOrderByIdDesc(Long propertyId);
    boolean existsByTenantIdAndPropertyId(Long tenantId, Long propertyId);

    @Query("SELECT r FROM Review r WHERE r.tenant.id = :tenantId AND r.property.id = :propertyId ORDER BY r.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<Review> findByTenantIdAndPropertyId(@Param("tenantId") Long tenantId, @Param("propertyId") Long propertyId);
}
