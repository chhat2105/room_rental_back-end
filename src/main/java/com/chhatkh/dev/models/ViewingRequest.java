package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "viewing_requests")
@Getter @Setter @NoArgsConstructor
public class ViewingRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tenant_id") private User tenant;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id") private Property property;
    @Column(name = "date") private LocalDate date;
    @Column(name = "time") private LocalTime time;
    @Enumerated(EnumType.STRING) private ViewingStatus status = ViewingStatus.PENDING;
}
