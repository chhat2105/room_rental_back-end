package com.chhatkh.dev.repositories;

import com.chhatkh.dev.entities.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    List<Inquiry> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    List<Inquiry> findByRoomIdOrderByCreatedAtDesc(Long roomId);
}
