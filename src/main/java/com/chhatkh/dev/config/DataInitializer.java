package com.chhatkh.dev.config;

import com.chhatkh.dev.constants.Constants;
import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.PropertyStatus;
import com.chhatkh.dev.models.enums.PropertyType;
import com.chhatkh.dev.models.enums.RoomStatus;
import com.chhatkh.dev.repository.AmenityRepository;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.repository.RoleRepository;
import com.chhatkh.dev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String[] dropConstraintSqls = {
                "ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check",
                "ALTER TABLE properties DROP CONSTRAINT IF EXISTS properties_status_check",
                "ALTER TABLE properties DROP CONSTRAINT IF EXISTS properties_property_type_check",
                "ALTER TABLE complaints DROP CONSTRAINT IF EXISTS complaints_status_check",
                "ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_status_check",
                "ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_type_check",
                "ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_payment_method_check",
                "ALTER TABLE rental_applications DROP CONSTRAINT IF EXISTS rental_applications_status_check",
                "ALTER TABLE rental_contracts DROP CONSTRAINT IF EXISTS rental_contracts_status_check",
                "ALTER TABLE rooms DROP CONSTRAINT IF EXISTS rooms_status_check",
                "ALTER TABLE viewing_requests DROP CONSTRAINT IF EXISTS viewing_requests_status_check",
                """
                DO $$
                DECLARE r RECORD;
                BEGIN
                    FOR r IN (
                        SELECT tc.table_name, tc.constraint_name
                        FROM information_schema.table_constraints tc
                        WHERE tc.table_schema = 'public'
                          AND tc.constraint_type = 'CHECK'
                          AND tc.table_name IN ('properties', 'roles', 'complaints', 'payments', 'rental_applications', 'rental_contracts', 'rooms', 'viewing_requests')
                    ) LOOP
                        EXECUTE 'ALTER TABLE ' || quote_ident(r.table_name) || ' DROP CONSTRAINT IF EXISTS ' || quote_ident(r.constraint_name);
                    END LOOP;
                END $$;
                """
        };

        for (String sql : dropConstraintSqls) {
            try {
                jdbcTemplate.execute(sql);
            } catch (Exception ignored) {
                // Ignore if table or constraint does not exist or dialect differs
            }
        }

        try {
            jdbcTemplate.execute("UPDATE properties SET status = 'PUBLISHED' WHERE status = 'PENDING_REVIEW'");
        } catch (Exception ignored) {
        }

        for (UserRole userRole : UserRole.values()) {
            try {
                if (roleRepository.findByName(userRole).isEmpty()) {
                    roleRepository.save(new Role(userRole));
                }
            } catch (Exception e) {
                log.warn("Could not seed role {}: {}", userRole, e.getMessage());
            }
        }

        try {
            seedAmenities();
            User landlord = seedDemoUsers();
            seedSampleProperties(landlord);
        } catch (Exception e) {
            log.warn("Optional sample data seeding skipped: {}", e.getMessage());
        }
    }

    private void seedAmenities() {
        List<String> defaults = List.of(
                "Wi-Fi", "Air Conditioning", "Free Parking", "Swimming Pool",
                "Gym & Fitness", "24/7 Security", "Private Balcony", "Washing Machine",
                "Full Kitchen", "Elevator"
        );
        for (String name : defaults) {
            try {
                if (!amenityRepository.existsByNameIgnoreCase(name)) {
                    Amenity a = new Amenity();
                    a.setName(name);
                    amenityRepository.save(a);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private User seedDemoUsers() {
        User landlord = ensureUser(
                "landlord", "Sokha", "Chan", "landlord@renthub.com", "012345678",
                "123456", UserRole.ROLE_LANDLORD
        );
        ensureUser(
                "tenant", "Dara", "Sok", "tenant@renthub.com", "098765432",
                "123456", UserRole.ROLE_TENANT
        );
        ensureUser(
                "admin", "System", "Admin", "admin@renthub.com", "011223344",
                "123456", UserRole.ROLE_ADMIN
        );
        return landlord;
    }

    private User ensureUser(String username, String firstName, String lastName,
                            String email, String phone, String rawPass, UserRole userRole) {
        Role role = roleRepository.findByName(userRole)
                .orElseGet(() -> roleRepository.save(new Role(userRole)));
        return userRepository.findByUsername(username).map(existing -> {
            if (!Constants.STATUS_ACTIVE.equals(existing.getStatus())) {
                existing.setStatus(Constants.STATUS_ACTIVE);
            }
            if (existing.getRoles() == null || existing.getRoles().isEmpty()) {
                existing.setRoles(Set.of(role));
            }
            return userRepository.save(existing);
        }).orElseGet(() -> {
            User u = new User(username, email, passwordEncoder.encode(rawPass), phone);
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setStatus(Constants.STATUS_ACTIVE);
            u.setChangePassword(Constants.No);
            u.setRoles(Set.of(role));
            return userRepository.save(u);
        });
    }

    private void seedSampleProperties(User landlord) {
        if (landlord == null || propertyRepository.count() > 0) {
            return;
        }
        List<Amenity> allAmenities = amenityRepository.findAll();

        createSampleProperty(
                landlord,
                "Skyline Modern Condo — BKK1",
                "Luxury fully-furnished condo in the heart of Boeung Keng Kang 1 with rooftop infinity pool, high-speed fiber Wi-Fi, and 24/7 security.",
                PropertyType.CONDO,
                "Street 302, BKK1, Phnom Penh",
                11.5515, 104.9265,
                new BigDecimal("450.00"),
                true,
                List.of(
                        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=900&q=80",
                        "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=900&q=80"
                ),
                allAmenities,
                List.of(
                        new RoomSeed("A-101", 1, 1, 45.0, new BigDecimal("450.00")),
                        new RoomSeed("A-202", 2, 2, 72.0, new BigDecimal("650.00"))
                )
        );

        createSampleProperty(
                landlord,
                "Cozy Studio Loft — Toul Kork",
                "Bright studio room near TK Avenue and Royal University of Phnom Penh. Ideal for students and young professionals.",
                PropertyType.STUDIO,
                "Street 315, Boeung Kak 1, Toul Kork, Phnom Penh",
                11.5760, 104.8980,
                new BigDecimal("180.00"),
                true,
                List.of(
                        "https://images.unsplash.com/photo-1536376072261-38c75010e6c9?auto=format&fit=crop&w=900&q=80",
                        "https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&w=900&q=80"
                ),
                allAmenities.subList(0, Math.min(5, allAmenities.size())),
                List.of(
                        new RoomSeed("101", 1, 1, 28.0, new BigDecimal("180.00")),
                        new RoomSeed("102", 1, 1, 30.0, new BigDecimal("195.00")),
                        new RoomSeed("201", 1, 1, 32.0, new BigDecimal("210.00"))
                )
        );

        createSampleProperty(
                landlord,
                "Russian Market Boutique Apartment",
                "Spacious serviced apartment just 3 minutes walk from Tuol Tom Poung Market, surrounded by cafes and coworking spaces.",
                PropertyType.APARTMENT,
                "Street 454, Tuol Tom Poung 1, Phnom Penh",
                11.5395, 104.9145,
                new BigDecimal("320.00"),
                true,
                List.of(
                        "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=900&q=80"
                ),
                allAmenities,
                List.of(
                        new RoomSeed("B1", 1, 1, 40.0, new BigDecimal("320.00")),
                        new RoomSeed("B2", 2, 1, 58.0, new BigDecimal("420.00"))
                )
        );

        createSampleProperty(
                landlord,
                "Student Private Room — Sensok",
                "Clean, affordable private room with en-suite bathroom, air conditioning, and free motorbike parking.",
                PropertyType.ROOM,
                "Street 1986, Phnom Penh Thmey, Sen Sok",
                11.5830, 104.8810,
                new BigDecimal("110.00"),
                false,
                List.of(
                        "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=900&q=80"
                ),
                allAmenities.subList(0, Math.min(4, allAmenities.size())),
                List.of(
                        new RoomSeed("R-01", 1, 1, 20.0, new BigDecimal("110.00")),
                        new RoomSeed("R-02", 1, 1, 22.0, new BigDecimal("125.00"))
                )
        );
    }

    private record RoomSeed(String roomNumber, int beds, int baths, double size, BigDecimal price) {}

    private void createSampleProperty(
            User landlord,
            String title,
            String description,
            PropertyType type,
            String address,
            double lat,
            double lng,
            BigDecimal price,
            boolean furnished,
            List<String> imageUrls,
            List<Amenity> amenities,
            List<RoomSeed> rooms
    ) {
        Property p = new Property();
        p.setLandlord(landlord);
        p.setTitle(title);
        p.setDescription(description);
        p.setPropertyType(type);
        p.setAddress(address);
        p.setLatitude(lat);
        p.setLongitude(lng);
        p.setPrice(price);
        p.setFurnished(furnished);
        p.setStatus(PropertyStatus.PUBLISHED);
        p.getAmenities().addAll(amenities);

        for (String url : imageUrls) {
            PropertyImage img = new PropertyImage();
            img.setProperty(p);
            img.setImageUrl(url);
            p.getImages().add(img);
        }

        for (RoomSeed rs : rooms) {
            Room r = new Room();
            r.setProperty(p);
            r.setRoomNumber(rs.roomNumber());
            r.setBedrooms(rs.beds());
            r.setBathrooms(rs.baths());
            r.setSize(rs.size());
            r.setPrice(rs.price());
            r.setStatus(RoomStatus.AVAILABLE);
            p.getRooms().add(r);
        }

        propertyRepository.save(p);
    }
}
