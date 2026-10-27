package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "amenities")
@Getter @Setter @NoArgsConstructor
public class Amenity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String name;
}
