package com.chhatkh.dev.config;

import com.chhatkh.dev.constants.Constants;
import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final ViewingRequestRepository viewingRequestRepository;
    private final RentalApplicationRepository rentalApplicationRepository;
    private final RentalContractRepository rentalContractRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final ComplaintRepository complaintRepository;
    private final MessageRepository messageRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking database data initialization...");
        initRoles();
        initUsers();
        initAmenities();
        initPropertiesAndRelations();
        log.info("Database data check/seeding finished.");
    }

    private void initRoles() {
        for (UserRole roleEnum : UserRole.values()) {
            if (roleRepository.findByName(roleEnum).isEmpty()) {
                Role role = new Role(roleEnum);
                role.setCreateBy(Constants.SYSTEM);
                role.setCreateAt(new Date());
                roleRepository.save(role);
                log.info("Initialized role: {}", roleEnum);
            }
        }
    }

    private void initUsers() {
        Role roleAdmin = roleRepository.findByName(UserRole.ROLE_ADMIN).orElseThrow();
        Role roleLandlord = roleRepository.findByName(UserRole.ROLE_LANDLORD).orElseThrow();
        Role roleTenant = roleRepository.findByName(UserRole.ROLE_TENANT).orElseThrow();
        Role roleUser = roleRepository.findByName(UserRole.ROLE_USER).orElseThrow();

        String defaultPassword = passwordEncoder.encode("password123");

        // 1. Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "admin@renthub.com", defaultPassword, "012345678");
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setStatus(Constants.STATUS_ACTIVE);
            admin.setChangePassword(Constants.No);
            admin.setProfile("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200");
            admin.setRoles(Set.of(roleAdmin, roleUser));
            userRepository.save(admin);
            log.info("Initialized user: admin");
        }

        // 2. Landlord 1
        if (userRepository.findByUsername("landlord1").isEmpty()) {
            User landlord1 = new User("landlord1", "landlord1@renthub.com", defaultPassword, "098765432");
            landlord1.setFirstName("Sokha");
            landlord1.setLastName("Chan");
            landlord1.setStatus(Constants.STATUS_ACTIVE);
            landlord1.setChangePassword(Constants.No);
            landlord1.setProfile("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200");
            landlord1.setRoles(Set.of(roleLandlord, roleUser));
            userRepository.save(landlord1);
            log.info("Initialized user: landlord1");
        }

        // 3. Landlord 2
        if (userRepository.findByUsername("landlord2").isEmpty()) {
            User landlord2 = new User("landlord2", "landlord2@renthub.com", defaultPassword, "098765433");
            landlord2.setFirstName("Vannak");
            landlord2.setLastName("Khem");
            landlord2.setStatus(Constants.STATUS_ACTIVE);
            landlord2.setChangePassword(Constants.No);
            landlord2.setProfile("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200");
            landlord2.setRoles(Set.of(roleLandlord, roleUser));
            userRepository.save(landlord2);
            log.info("Initialized user: landlord2");
        }

        // 4. Tenant 1
        if (userRepository.findByUsername("tenant1").isEmpty()) {
            User tenant1 = new User("tenant1", "tenant1@renthub.com", defaultPassword, "011223344");
            tenant1.setFirstName("Dara");
            tenant1.setLastName("Keo");
            tenant1.setStatus(Constants.STATUS_ACTIVE);
            tenant1.setChangePassword(Constants.No);
            tenant1.setProfile("https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=200");
            tenant1.setRoles(Set.of(roleTenant, roleUser));
            userRepository.save(tenant1);
            log.info("Initialized user: tenant1");
        }

        // 5. Tenant 2
        if (userRepository.findByUsername("tenant2").isEmpty()) {
            User tenant2 = new User("tenant2", "tenant2@renthub.com", defaultPassword, "011223345");
            tenant2.setFirstName("Bopha");
            tenant2.setLastName("Meng");
            tenant2.setStatus(Constants.STATUS_ACTIVE);
            tenant2.setChangePassword(Constants.No);
            tenant2.setProfile("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200");
            tenant2.setRoles(Set.of(roleTenant, roleUser));
            userRepository.save(tenant2);
            log.info("Initialized user: tenant2");
        }

        // 6. Regular User
        if (userRepository.findByUsername("user1").isEmpty()) {
            User user1 = new User("user1", "user1@renthub.com", defaultPassword, "088997766");
            user1.setFirstName("Channa");
            user1.setLastName("Pheng");
            user1.setStatus(Constants.STATUS_ACTIVE);
            user1.setChangePassword(Constants.No);
            user1.setProfile("https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200");
            user1.setRoles(Set.of(roleUser));
            userRepository.save(user1);
            log.info("Initialized user: user1");
        }

        log.info("Initialized default users: admin, landlord1, landlord2, tenant1, tenant2, user1 (password: password123)");
    }

    private void initAmenities() {
        if (amenityRepository.count() > 0) {
            log.info("Amenities table is not empty (count: {}). Skipping amenity seeding.", amenityRepository.count());
            return;
        }

        List<String> names = List.of(
                "High-Speed WiFi",
                "Air Conditioning",
                "Swimming Pool",
                "Fitness Gym",
                "Motorbike / Car Parking",
                "Private Balcony",
                "24/7 Security & CCTV",
                "Washing Machine",
                "Elevator",
                "Fully Furnished",
                "Pet Friendly",
                "Kitchen & Stove",
                "Refrigerator",
                "Hot Water Heater"
        );

        for (String name : names) {
            Amenity a = new Amenity();
            a.setName(name);
            amenityRepository.save(a);
        }
        log.info("Initialized default amenities.");
    }

    private void initPropertiesAndRelations() {
        if (propertyRepository.count() > 0) {
            log.info("Properties table is not empty (count: {}). Skipping properties seeding.", propertyRepository.count());
            return;
        }

        User landlord1 = userRepository.findByUsername("landlord1").orElse(null);
        User landlord2 = userRepository.findByUsername("landlord2").orElse(null);
        User tenant1 = userRepository.findByUsername("tenant1").orElse(null);
        User tenant2 = userRepository.findByUsername("tenant2").orElse(null);

        if (landlord1 == null || landlord2 == null || tenant1 == null || tenant2 == null) {
            log.warn("Required users not found. Skipping property & rental seeding.");
            return;
        }

        Map<String, Amenity> amenityMap = new HashMap<>();
        for (Amenity a : amenityRepository.findAll()) {
            amenityMap.put(a.getName(), a);
        }

        Function<String[], Set<Amenity>> getAmenities = (keys) -> {
            Set<Amenity> set = new HashSet<>();
            for (String k : keys) {
                if (amenityMap.containsKey(k)) {
                    set.add(amenityMap.get(k));
                }
            }
            return set;
        };

        // Property 1: BKK1 Luxury Condo
        Property p1 = new Property();
        p1.setLandlord(landlord1);
        p1.setTitle("Modern 1-Bedroom Condo at BKK1");
        p1.setDescription("Luxurious high-floor condo in the heart of Boeung Keng Kang 1. Walking distance to supermarkets, cafes, and international restaurants. Features high ceiling, floor-to-ceiling windows, and access to rooftop sky pool.");
        p1.setPropertyType(PropertyType.CONDO);
        p1.setAddress("St 288, Boeung Keng Kang 1, Chamkarmon, Phnom Penh");
        p1.setLatitude(11.5508);
        p1.setLongitude(104.9282);
        p1.setFurnished(true);
        p1.setPrice(new BigDecimal("650.00"));
        p1.setStatus(PropertyStatus.PUBLISHED);
        p1.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Air Conditioning", "Swimming Pool", "Fitness Gym", "Elevator", "24/7 Security & CCTV", "Fully Furnished"
        }));

        PropertyImage img1 = new PropertyImage();
        img1.setProperty(p1);
        img1.setImageUrl("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop&q=60");
        PropertyImage img2 = new PropertyImage();
        img2.setProperty(p1);
        img2.setImageUrl("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop&q=60");
        PropertyImage img3 = new PropertyImage();
        img3.setProperty(p1);
        img3.setImageUrl("https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&auto=format&fit=crop&q=60");
        p1.getImages().addAll(List.of(img1, img2, img3));

        Room r1 = new Room();
        r1.setProperty(p1);
        r1.setRoomNumber("Unit 801");
        r1.setBedrooms(1);
        r1.setBathrooms(1);
        r1.setSize(55.0);
        r1.setPrice(new BigDecimal("650.00"));
        r1.setStatus(RoomStatus.OCCUPIED);
        p1.getRooms().add(r1);

        propertyRepository.save(p1);

        // Property 2: Toul Kork 2-Bedroom Apartment
        Property p2 = new Property();
        p2.setLandlord(landlord1);
        p2.setTitle("Spacious 2-Bedroom Apartment in Toul Kork");
        p2.setDescription("Bright and quiet apartment located near TK Avenue. Includes private balcony, full kitchen, modern bathrooms, and secure motorbike parking. Perfect for professionals or small families.");
        p2.setPropertyType(PropertyType.APARTMENT);
        p2.setAddress("St 315, Boeung Kak 1, Toul Kork, Phnom Penh");
        p2.setLatitude(11.5732);
        p2.setLongitude(104.8978);
        p2.setFurnished(true);
        p2.setPrice(new BigDecimal("450.00"));
        p2.setStatus(PropertyStatus.PUBLISHED);
        p2.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Air Conditioning", "Motorbike / Car Parking", "Private Balcony", "Washing Machine", "Kitchen & Stove"
        }));

        PropertyImage p2Img1 = new PropertyImage();
        p2Img1.setProperty(p2);
        p2Img1.setImageUrl("https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=800&auto=format&fit=crop&q=60");
        PropertyImage p2Img2 = new PropertyImage();
        p2Img2.setProperty(p2);
        p2Img2.setImageUrl("https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800&auto=format&fit=crop&q=60");
        p2.getImages().addAll(List.of(p2Img1, p2Img2));

        Room p2r1 = new Room();
        p2r1.setProperty(p2);
        p2r1.setRoomNumber("Room A1");
        p2r1.setBedrooms(1);
        p2r1.setBathrooms(1);
        p2r1.setSize(38.0);
        p2r1.setPrice(new BigDecimal("220.00"));
        p2r1.setStatus(RoomStatus.AVAILABLE);

        Room p2r2 = new Room();
        p2r2.setProperty(p2);
        p2r2.setRoomNumber("Room A2");
        p2r2.setBedrooms(1);
        p2r2.setBathrooms(1);
        p2r2.setSize(42.0);
        p2r2.setPrice(new BigDecimal("230.00"));
        p2r2.setStatus(RoomStatus.AVAILABLE);

        p2.getRooms().addAll(List.of(p2r1, p2r2));
        propertyRepository.save(p2);

        // Property 3: Riverside View Penthouse
        Property p3 = new Property();
        p3.setLandlord(landlord2);
        p3.setTitle("Luxury Riverside View Penthouse");
        p3.setDescription("Spectacular panoramic view of the Tonle Sap & Mekong River. Premium finishing with private elevator access, infinity swimming pool, concierge, and gym.");
        p3.setPropertyType(PropertyType.CONDO);
        p3.setAddress("Sisowath Quay, Daun Penh, Phnom Penh");
        p3.setLatitude(11.5684);
        p3.setLongitude(104.9331);
        p3.setFurnished(true);
        p3.setPrice(new BigDecimal("1200.00"));
        p3.setStatus(PropertyStatus.PUBLISHED);
        p3.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Air Conditioning", "Swimming Pool", "Fitness Gym", "Elevator", "24/7 Security & CCTV", "Private Balcony"
        }));

        PropertyImage p3Img1 = new PropertyImage();
        p3Img1.setProperty(p3);
        p3Img1.setImageUrl("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&auto=format&fit=crop&q=60");
        PropertyImage p3Img2 = new PropertyImage();
        p3Img2.setProperty(p3);
        p3Img2.setImageUrl("https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800&auto=format&fit=crop&q=60");
        p3.getImages().addAll(List.of(p3Img1, p3Img2));

        Room p3r1 = new Room();
        p3r1.setProperty(p3);
        p3r1.setRoomNumber("PH-18");
        p3r1.setBedrooms(2);
        p3r1.setBathrooms(2);
        p3r1.setSize(120.0);
        p3r1.setPrice(new BigDecimal("1200.00"));
        p3r1.setStatus(RoomStatus.AVAILABLE);
        p3.getRooms().add(p3r1);
        propertyRepository.save(p3);

        // Property 4: Russian Market Studio
        Property p4 = new Property();
        p4.setLandlord(landlord2);
        p4.setTitle("Affordable Studio Room near Russian Market");
        p4.setDescription("Cozy studio apartment situated 5 minutes walk from Tuol Tompoung (Russian Market). Surrounded by lively local markets, cafes, and bakeries. Fully equipped with modern bed, working desk, and private bathroom.");
        p4.setPropertyType(PropertyType.STUDIO);
        p4.setAddress("St 450, Toul Tompoung 1, Chamkarmon, Phnom Penh");
        p4.setLatitude(11.5375);
        p4.setLongitude(104.9145);
        p4.setFurnished(true);
        p4.setPrice(new BigDecimal("260.00"));
        p4.setStatus(PropertyStatus.PUBLISHED);
        p4.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Air Conditioning", "Motorbike / Car Parking", "Hot Water Heater", "Refrigerator"
        }));

        PropertyImage p4Img1 = new PropertyImage();
        p4Img1.setProperty(p4);
        p4Img1.setImageUrl("https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800&auto=format&fit=crop&q=60");
        p4.getImages().add(p4Img1);

        Room p4r1 = new Room();
        p4r1.setProperty(p4);
        p4r1.setRoomNumber("Studio 301");
        p4r1.setBedrooms(1);
        p4r1.setBathrooms(1);
        p4r1.setSize(32.0);
        p4r1.setPrice(new BigDecimal("260.00"));
        p4r1.setStatus(RoomStatus.AVAILABLE);
        p4.getRooms().add(p4r1);
        propertyRepository.save(p4);

        // Property 5: Sen Sok Family Villa
        Property p5 = new Property();
        p5.setLandlord(landlord1);
        p5.setTitle("Modern Family Villa in Sen Sok");
        p5.setDescription("Large 4-bedroom gated villa near AEON Mall Sen Sok City. Quiet residential neighborhood with beautiful green garden, 2-car garage, and open kitchen.");
        p5.setPropertyType(PropertyType.VILLA);
        p5.setAddress("St 1003, Phnom Penh Thmey, Sen Sok, Phnom Penh");
        p5.setLatitude(11.5872);
        p5.setLongitude(104.8723);
        p5.setFurnished(true);
        p5.setPrice(new BigDecimal("1500.00"));
        p5.setStatus(PropertyStatus.PUBLISHED);
        p5.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Air Conditioning", "Motorbike / Car Parking", "Pet Friendly", "Kitchen & Stove", "24/7 Security & CCTV"
        }));

        PropertyImage p5Img1 = new PropertyImage();
        p5Img1.setProperty(p5);
        p5Img1.setImageUrl("https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800&auto=format&fit=crop&q=60");
        p5.getImages().add(p5Img1);

        Room p5r1 = new Room();
        p5r1.setProperty(p5);
        p5r1.setRoomNumber("Master Bedroom");
        p5r1.setBedrooms(4);
        p5r1.setBathrooms(5);
        p5r1.setSize(260.0);
        p5r1.setPrice(new BigDecimal("1500.00"));
        p5r1.setStatus(RoomStatus.AVAILABLE);
        p5.getRooms().add(p5r1);
        propertyRepository.save(p5);

        // Property 6: Student Single Room
        Property p6 = new Property();
        p6.setLandlord(landlord2);
        p6.setTitle("Budget Single Room near RUPP / ITC");
        p6.setDescription("Clean, quiet room ideal for university students or interns. Close to Royal University of Phnom Penh and Institute of Technology of Cambodia. Shared kitchen and laundry area.");
        p6.setPropertyType(PropertyType.ROOM);
        p6.setAddress("Russian Blvd, Tuol Kouk, Phnom Penh");
        p6.setLatitude(11.5680);
        p6.setLongitude(104.8920);
        p6.setFurnished(false);
        p6.setPrice(new BigDecimal("120.00"));
        p6.setStatus(PropertyStatus.PUBLISHED);
        p6.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Motorbike / Car Parking"
        }));

        PropertyImage p6Img1 = new PropertyImage();
        p6Img1.setProperty(p6);
        p6Img1.setImageUrl("https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800&auto=format&fit=crop&q=60");
        p6.getImages().add(p6Img1);

        Room p6r1 = new Room();
        p6r1.setProperty(p6);
        p6r1.setRoomNumber("Room 101");
        p6r1.setBedrooms(1);
        p6r1.setBathrooms(1);
        p6r1.setSize(20.0);
        p6r1.setPrice(new BigDecimal("120.00"));
        p6r1.setStatus(RoomStatus.AVAILABLE);
        p6.getRooms().add(p6r1);
        propertyRepository.save(p6);

        // Property 7: Pending Approval (for Admin testing)
        Property p7 = new Property();
        p7.setLandlord(landlord2);
        p7.setTitle("Newly Built Studio Pending Admin Approval");
        p7.setDescription("Brand new studio listing waiting for admin review and approval.");
        p7.setPropertyType(PropertyType.STUDIO);
        p7.setAddress("St 2004, Sen Sok, Phnom Penh");
        p7.setLatitude(11.5540);
        p7.setLongitude(104.8650);
        p7.setFurnished(true);
        p7.setPrice(new BigDecimal("280.00"));
        p7.setStatus(PropertyStatus.PENDING_REVIEW);
        p7.setAmenities(getAmenities.apply(new String[]{
                "High-Speed WiFi", "Air Conditioning", "Motorbike / Car Parking"
        }));
        PropertyImage p7Img1 = new PropertyImage();
        p7Img1.setProperty(p7);
        p7Img1.setImageUrl("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop&q=60");
        p7.getImages().add(p7Img1);
        propertyRepository.save(p7);

        // Rental Application & Contract for Tenant1 on Property 1 / Unit 801
        RentalApplication app1 = new RentalApplication();
        app1.setTenant(tenant1);
        app1.setProperty(p1);
        app1.setRoom(r1);
        app1.setStartDate(LocalDate.now().minusMonths(1));
        app1.setEndDate(LocalDate.now().plusMonths(11));
        app1.setStatus(ApplicationStatus.APPROVED);
        rentalApplicationRepository.save(app1);

        RentalContract contract1 = new RentalContract();
        contract1.setApplication(app1);
        contract1.setRent(new BigDecimal("650.00"));
        contract1.setDeposit(new BigDecimal("650.00"));
        contract1.setStartDate(LocalDate.now().minusMonths(1));
        contract1.setEndDate(LocalDate.now().plusMonths(11));
        contract1.setMovedInAt(LocalDateTime.now().minusMonths(1));
        contract1.setStatus(ContractStatus.ACTIVE);
        rentalContractRepository.save(contract1);

        // Payments for Contract 1
        Payment pDeposit = new Payment();
        pDeposit.setContract(contract1);
        pDeposit.setType(PaymentType.DEPOSIT);
        pDeposit.setAmount(new BigDecimal("650.00"));
        pDeposit.setDueDate(LocalDate.now().minusMonths(1));
        pDeposit.setPaymentDate(LocalDateTime.now().minusMonths(1));
        pDeposit.setPaymentMethod(PaymentMethod.ABA_PAY);
        pDeposit.setStatus(PaymentStatus.PAID);
        paymentRepository.save(pDeposit);

        Payment pMonth1 = new Payment();
        pMonth1.setContract(contract1);
        pMonth1.setType(PaymentType.RENT);
        pMonth1.setAmount(new BigDecimal("650.00"));
        pMonth1.setDueDate(LocalDate.now().minusMonths(1));
        pMonth1.setPaymentDate(LocalDateTime.now().minusMonths(1));
        pMonth1.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        pMonth1.setStatus(PaymentStatus.PAID);
        paymentRepository.save(pMonth1);

        Payment pMonth2 = new Payment();
        pMonth2.setContract(contract1);
        pMonth2.setType(PaymentType.RENT);
        pMonth2.setAmount(new BigDecimal("650.00"));
        pMonth2.setDueDate(LocalDate.now().plusDays(5));
        pMonth2.setPaymentMethod(PaymentMethod.ABA_PAY);
        pMonth2.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(pMonth2);

        // Overdue utility fee / payment for testing admin overdue endpoint
        Payment pOverdue = new Payment();
        pOverdue.setContract(contract1);
        pOverdue.setType(PaymentType.RENT);
        pOverdue.setAmount(new BigDecimal("50.00"));
        pOverdue.setDueDate(LocalDate.now().minusDays(5));
        pOverdue.setStatus(PaymentStatus.OVERDUE);
        paymentRepository.save(pOverdue);

        // Pending Application: Tenant 2 on Property 2
        RentalApplication app2 = new RentalApplication();
        app2.setTenant(tenant2);
        app2.setProperty(p2);
        app2.setRoom(p2r1);
        app2.setStartDate(LocalDate.now().plusDays(10));
        app2.setEndDate(LocalDate.now().plusYears(1));
        app2.setStatus(ApplicationStatus.PENDING);
        rentalApplicationRepository.save(app2);

        // Viewing Requests
        ViewingRequest vr1 = new ViewingRequest();
        vr1.setTenant(tenant2);
        vr1.setProperty(p3);
        vr1.setDate(LocalDate.now().plusDays(2));
        vr1.setTime(LocalTime.of(14, 30));
        vr1.setStatus(ViewingStatus.PENDING);
        viewingRequestRepository.save(vr1);

        ViewingRequest vr2 = new ViewingRequest();
        vr2.setTenant(tenant1);
        vr2.setProperty(p4);
        vr2.setDate(LocalDate.now().minusDays(5));
        vr2.setTime(LocalTime.of(10, 0));
        vr2.setStatus(ViewingStatus.APPROVED);
        viewingRequestRepository.save(vr2);

        // Review
        Review rev1 = new Review();
        rev1.setTenant(tenant1);
        rev1.setProperty(p1);
        rev1.setRating(5);
        rev1.setComment("Amazing condo with stunning views! Landlord is very friendly, fast response, and everything works perfectly.");
        reviewRepository.save(rev1);

        // Complaints
        Complaint c1 = new Complaint();
        c1.setTenant(tenant1);
        c1.setProperty(p1);
        c1.setDescription("Water leakage under the kitchen sink.");
        c1.setStatus(ComplaintStatus.OPEN);
        complaintRepository.save(c1);

        Complaint c2 = new Complaint();
        c2.setTenant(tenant1);
        c2.setProperty(p1);
        c2.setDescription("Air conditioner in master bedroom needs gas refill.");
        c2.setResolution("Technician contacted and scheduled for Friday morning.");
        c2.setStatus(ComplaintStatus.IN_PROGRESS);
        complaintRepository.save(c2);

        // Chat Messages
        Message m1 = new Message();
        m1.setSender(tenant1);
        m1.setReceiver(landlord1);
        m1.setMessage("Hello Sokha, could you please confirm if the key card is ready for collection?");
        messageRepository.save(m1);

        Message m2 = new Message();
        m2.setSender(landlord1);
        m2.setReceiver(tenant1);
        m2.setMessage("Yes Dara! The building reception has your key card ready. Welcome!");
        messageRepository.save(m2);

        // Favorites
        Favorite fav1 = new Favorite();
        fav1.setUser(tenant1);
        fav1.setProperty(p2);
        favoriteRepository.save(fav1);

        Favorite fav2 = new Favorite();
        fav2.setUser(tenant1);
        fav2.setProperty(p3);
        favoriteRepository.save(fav2);

        Favorite fav3 = new Favorite();
        fav3.setUser(tenant2);
        fav3.setProperty(p1);
        favoriteRepository.save(fav3);

        log.info("Initialized mock properties, rooms, images, contracts, payments, viewings, reviews, complaints, and messages.");
    }
}
