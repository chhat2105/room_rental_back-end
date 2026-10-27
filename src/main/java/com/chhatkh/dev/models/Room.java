package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "rooms")
@Getter @Setter @NoArgsConstructor
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id") private Property property;
    @Column(name = "room_number", nullable = false) private String roomNumber;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double size;
    @Column(precision = 12, scale = 2) private BigDecimal price;
    @Enumerated(EnumType.STRING) private RoomStatus status = RoomStatus.AVAILABLE;
}
