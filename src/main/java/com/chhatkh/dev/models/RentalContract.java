package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "rental_contracts")
@Getter @Setter @NoArgsConstructor
public class RentalContract {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "application_id", unique = true) private RentalApplication application;
    @Column(precision = 12, scale = 2) private BigDecimal rent;
    @Column(precision = 12, scale = 2) private BigDecimal deposit;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Enumerated(EnumType.STRING) private ContractStatus status = ContractStatus.PENDING_PAYMENT;
}
