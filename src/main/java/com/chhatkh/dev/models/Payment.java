package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "payments")
@Getter @Setter @NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "contract_id") private RentalContract contract;
    @Enumerated(EnumType.STRING) private PaymentType type;          // added: DEPOSIT / RENT
    @Column(precision = 12, scale = 2) private BigDecimal amount;
    @Column(name = "due_date") private LocalDate dueDate;           // added: monthly rent schedule
    @Column(name = "payment_date") private LocalDateTime paymentDate;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method") private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING) private PaymentStatus status = PaymentStatus.PENDING;
}
