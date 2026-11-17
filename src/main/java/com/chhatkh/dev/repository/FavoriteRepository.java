package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.property.id = :propertyId ORDER BY f.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<Favorite> findByUserIdAndPropertyId(@Param("userId") Long userId, @Param("propertyId") Long propertyId);
}
