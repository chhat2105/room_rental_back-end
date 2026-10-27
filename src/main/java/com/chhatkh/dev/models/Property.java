package com.chhatkh.dev.models;

import jakarta.persistence.*;
import lombok.*;
import com.chhatkh.dev.models.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "properties")
@Getter @Setter @NoArgsConstructor
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "landlord_id") private User landlord;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Enumerated(EnumType.STRING) @Column(name = "property_type") private PropertyType propertyType;
    private String address;
    private Double latitude;
    private Double longitude;
    @Column(precision = 12, scale = 2) private BigDecimal price;
    @Enumerated(EnumType.STRING) private PropertyStatus status = PropertyStatus.DRAFT;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true) private List<PropertyImage> images = new ArrayList<>();
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true) private List<Room> rooms = new ArrayList<>();
    @ManyToMany @JoinTable(name = "property_amenities",
            joinColumns = @JoinColumn(name = "property_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities = new HashSet<>();

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
