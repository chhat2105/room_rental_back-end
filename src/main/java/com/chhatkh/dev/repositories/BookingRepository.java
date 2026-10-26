package com.chhatkh.dev.repositories;

import com.chhatkh.dev.entities.Booking;
import com.chhatkh.dev.entities.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<Booking> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    @Query("SELECT b FROM Booking b WHERE b.room.owner.id = :landlordId ORDER BY b.createdAt DESC")
    List<Booking> findByLandlordIdOrderByCreatedAtDesc(@Param("landlordId") Long landlordId);

    long countByStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0.0) FROM Booking b WHERE b.status = com.chhatkh.dev.entities.BookingStatus.CONFIRMED OR b.status = com.chhatkh.dev.entities.BookingStatus.COMPLETED")
    Double calculateTotalRevenue();
}
