package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByContractIdOrderByDueDateAsc(Long contractId);
    List<Payment> findByStatusAndDueDateBefore(PaymentStatus status, LocalDate date);
}
