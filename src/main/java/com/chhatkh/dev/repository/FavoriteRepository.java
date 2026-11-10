package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);
}
