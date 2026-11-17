package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.Role;
import com.chhatkh.dev.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r WHERE r.name = :name ORDER BY r.id ASC FETCH FIRST 1 ROWS ONLY")
    Optional<Role> findByName(@Param("name") UserRole name);
}
