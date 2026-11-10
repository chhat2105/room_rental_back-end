package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "rental_applications")
@Getter @Setter @NoArgsConstructor
public class RentalApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tenant_id") private User tenant;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id") private Property property;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "room_id") private Room room;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Enumerated(EnumType.STRING) private ApplicationStatus status = ApplicationStatus.PENDING;
}
