package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "property_id"}))
@Getter @Setter @NoArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tenant_id") private User tenant;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id") private Property property;
    private Integer rating;
    @Column(columnDefinition = "TEXT") private String comment;
}
