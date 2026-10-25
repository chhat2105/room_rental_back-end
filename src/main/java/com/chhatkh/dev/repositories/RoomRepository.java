package com.chhatkh.dev.repositories;

import com.chhatkh.dev.entities.Room;
import com.chhatkh.dev.entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Room> findByAvailableTrueOrderByCreatedAtDesc();

    @Query("SELECT r FROM Room r WHERE " +
            "(:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:city IS NULL OR LOWER(r.city) = LOWER(:city)) AND " +
            "(:district IS NULL OR LOWER(r.district) = LOWER(:district)) AND " +
            "(:roomType IS NULL OR r.roomType = :roomType) AND " +
            "(:minPrice IS NULL OR r.pricePerMonth >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.pricePerMonth <= :maxPrice) AND " +
            "(:available IS NULL OR r.available = :available) " +
            "ORDER BY r.createdAt DESC")
    List<Room> searchRooms(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("district") String district,
            @Param("roomType") RoomType roomType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("available") Boolean available
    );

    long countByAvailableTrue();

    long countByAvailableFalse();
}
