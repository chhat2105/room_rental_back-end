package com.chhatkh.dev.services;

import com.chhatkh.dev.models.enums.PaymentStatus;
import com.chhatkh.dev.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RentScheduler {
    private final PaymentRepository paymentRepo;

    /** Every day at 01:00 – unpaid payments past their due date become OVERDUE. */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markOverdue() {
        paymentRepo.findByStatusAndDueDateBefore(PaymentStatus.PENDING, LocalDate.now())
                .forEach(p -> p.setStatus(PaymentStatus.OVERDUE));
    }
}
