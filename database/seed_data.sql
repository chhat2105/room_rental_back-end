-- ============================================================================
-- RentHub Room Rental System — Seed Data Script
-- Database: PostgreSQL
-- Default password for all seed accounts: password123
-- BCrypt Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================================================

-- 1. ROLES
INSERT INTO roles (id, name, created_at, created_by)
VALUES
    (1, 'ROLE_ADMIN', CURRENT_TIMESTAMP, 'SYS'),
    (2, 'ROLE_LANDLORD', CURRENT_TIMESTAMP, 'SYS'),
    (3, 'ROLE_TENANT', CURRENT_TIMESTAMP, 'SYS'),
    (4, 'ROLE_USER', CURRENT_TIMESTAMP, 'SYS'),
    (5, 'ROLE_MODERATOR', CURRENT_TIMESTAMP, 'SYS'),
    (6, 'ROLE_CUSTOMER', CURRENT_TIMESTAMP, 'SYS'),
    (7, 'ROLE_SHOP', CURRENT_TIMESTAMP, 'SYS'),
    (8, 'ROLE_CASHIER', CURRENT_TIMESTAMP, 'SYS'),
    (9, 'ROLE_MERCHANT', CURRENT_TIMESTAMP, 'SYS'),
    (10, 'ROLE_CREATOR', CURRENT_TIMESTAMP, 'SYS'),
    (11, 'ROLE_REVIEWER', CURRENT_TIMESTAMP, 'SYS')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('roles', 'id'), COALESCE((SELECT MAX(id) FROM roles), 1));

-- 2. USERS
-- Password for all: password123
INSERT INTO users (id, username, first_name, last_name, email, phone_number, password, status, change_password, profile, created_at, created_by)
VALUES
    (1, 'admin', 'System', 'Admin', 'admin@renthub.com', '012345678',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACT', 'N',
     'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200', CURRENT_TIMESTAMP, 'SYS'),

    (2, 'landlord1', 'Sokha', 'Chan', 'landlord1@renthub.com', '098765432',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACT', 'N',
     'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200', CURRENT_TIMESTAMP, 'SYS'),

    (3, 'landlord2', 'Vannak', 'Khem', 'landlord2@renthub.com', '098765433',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACT', 'N',
     'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200', CURRENT_TIMESTAMP, 'SYS'),

    (4, 'tenant1', 'Dara', 'Keo', 'tenant1@renthub.com', '011223344',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACT', 'N',
     'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=200', CURRENT_TIMESTAMP, 'SYS'),

    (5, 'tenant2', 'Bopha', 'Meng', 'tenant2@renthub.com', '011223345',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACT', 'N',
     'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200', CURRENT_TIMESTAMP, 'SYS'),

    (6, 'user1', 'Channa', 'Pheng', 'user1@renthub.com', '088997766',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACT', 'N',
     'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200', CURRENT_TIMESTAMP, 'SYS')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1));

-- 3. USER ROLES
INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1), -- admin -> ROLE_ADMIN
    (1, 4), -- admin -> ROLE_USER
    (2, 2), -- landlord1 -> ROLE_LANDLORD
    (2, 4), -- landlord1 -> ROLE_USER
    (3, 2), -- landlord2 -> ROLE_LANDLORD
    (3, 4), -- landlord2 -> ROLE_USER
    (4, 3), -- tenant1 -> ROLE_TENANT
    (4, 4), -- tenant1 -> ROLE_USER
    (5, 3), -- tenant2 -> ROLE_TENANT
    (5, 4), -- tenant2 -> ROLE_USER
    (6, 4)  -- user1 -> ROLE_USER
ON CONFLICT DO NOTHING;

-- 4. AMENITIES
INSERT INTO amenities (id, name)
VALUES
    (1, 'High-Speed WiFi'),
    (2, 'Air Conditioning'),
    (3, 'Swimming Pool'),
    (4, 'Fitness Gym'),
    (5, 'Motorbike / Car Parking'),
    (6, 'Private Balcony'),
    (7, '24/7 Security & CCTV'),
    (8, 'Washing Machine'),
    (9, 'Elevator'),
    (10, 'Fully Furnished'),
    (11, 'Pet Friendly'),
    (12, 'Kitchen & Stove'),
    (13, 'Refrigerator'),
    (14, 'Hot Water Heater')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('amenities', 'id'), COALESCE((SELECT MAX(id) FROM amenities), 1));

-- 5. PROPERTIES
INSERT INTO properties (id, landlord_id, title, description, property_type, address, latitude, longitude, furnished, price, status, created_at)
VALUES
    (1, 2, 'Modern 1-Bedroom Condo at BKK1',
     'Luxurious high-floor condo in the heart of Boeung Keng Kang 1. Walking distance to supermarkets, cafes, and international restaurants. Features high ceiling, floor-to-ceiling windows, and access to rooftop sky pool.',
     'CONDO', 'St 288, Boeung Keng Kang 1, Chamkarmon, Phnom Penh', 11.5508, 104.9282, TRUE, 650.00, 'PUBLISHED', CURRENT_TIMESTAMP),

    (2, 2, 'Spacious 2-Bedroom Apartment in Toul Kork',
     'Bright and quiet apartment located near TK Avenue. Includes private balcony, full kitchen, modern bathrooms, and secure motorbike parking. Perfect for professionals or small families.',
     'APARTMENT', 'St 315, Boeung Kak 1, Toul Kork, Phnom Penh', 11.5732, 104.8978, TRUE, 450.00, 'PUBLISHED', CURRENT_TIMESTAMP),

    (3, 3, 'Luxury Riverside View Penthouse',
     'Spectacular panoramic view of the Tonle Sap & Mekong River. Premium finishing with private elevator access, infinity swimming pool, concierge, and gym.',
     'CONDO', 'Sisowath Quay, Daun Penh, Phnom Penh', 11.5684, 104.9331, TRUE, 1200.00, 'PUBLISHED', CURRENT_TIMESTAMP),

    (4, 3, 'Affordable Studio Room near Russian Market',
     'Cozy studio apartment situated 5 minutes walk from Tuol Tompoung (Russian Market). Surrounded by lively local markets, cafes, and bakeries. Fully equipped with modern bed, working desk, and private bathroom.',
     'STUDIO', 'St 450, Toul Tompoung 1, Chamkarmon, Phnom Penh', 11.5375, 104.9145, TRUE, 260.00, 'PUBLISHED', CURRENT_TIMESTAMP),

    (5, 2, 'Modern Family Villa in Sen Sok',
     'Large 4-bedroom gated villa near AEON Mall Sen Sok City. Quiet residential neighborhood with beautiful green garden, 2-car garage, and open kitchen.',
     'VILLA', 'St 1003, Phnom Penh Thmey, Sen Sok, Phnom Penh', 11.5872, 104.8723, TRUE, 1500.00, 'PUBLISHED', CURRENT_TIMESTAMP),

    (6, 3, 'Budget Single Room near RUPP / ITC',
     'Clean, quiet room ideal for university students or interns. Close to Royal University of Phnom Penh and Institute of Technology of Cambodia. Shared kitchen and laundry area.',
     'ROOM', 'Russian Blvd, Tuol Kouk, Phnom Penh', 11.5680, 104.8920, FALSE, 120.00, 'PUBLISHED', CURRENT_TIMESTAMP),

    (7, 3, 'Newly Built Studio Pending Admin Approval',
     'Brand new studio listing waiting for admin review and approval.',
     'STUDIO', 'St 2004, Sen Sok, Phnom Penh', 11.5540, 104.8650, TRUE, 280.00, 'PENDING_REVIEW', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('properties', 'id'), COALESCE((SELECT MAX(id) FROM properties), 1));

-- 6. PROPERTY IMAGES
INSERT INTO property_images (id, property_id, image_url)
VALUES
    (1, 1, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop&q=60'),
    (2, 1, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop&q=60'),
    (3, 1, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&auto=format&fit=crop&q=60'),
    (4, 2, 'https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=800&auto=format&fit=crop&q=60'),
    (5, 2, 'https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800&auto=format&fit=crop&q=60'),
    (6, 3, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&auto=format&fit=crop&q=60'),
    (7, 3, 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800&auto=format&fit=crop&q=60'),
    (8, 4, 'https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800&auto=format&fit=crop&q=60'),
    (9, 5, 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800&auto=format&fit=crop&q=60'),
    (10, 6, 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800&auto=format&fit=crop&q=60'),
    (11, 7, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop&q=60')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('property_images', 'id'), COALESCE((SELECT MAX(id) FROM property_images), 1));

-- 7. ROOMS
INSERT INTO rooms (id, property_id, room_number, bedrooms, bathrooms, size, price, status)
VALUES
    (1, 1, 'Unit 801', 1, 1, 55.0, 650.00, 'OCCUPIED'),
    (2, 2, 'Room A1', 1, 1, 38.0, 220.00, 'AVAILABLE'),
    (3, 2, 'Room A2', 1, 1, 42.0, 230.00, 'AVAILABLE'),
    (4, 3, 'PH-18', 2, 2, 120.0, 1200.00, 'AVAILABLE'),
    (5, 4, 'Studio 301', 1, 1, 32.0, 260.00, 'AVAILABLE'),
    (6, 5, 'Master Suite', 4, 5, 260.0, 1500.00, 'AVAILABLE'),
    (7, 6, 'Room 101', 1, 1, 20.0, 120.00, 'AVAILABLE')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('rooms', 'id'), COALESCE((SELECT MAX(id) FROM rooms), 1));

-- 8. PROPERTY AMENITIES (JOIN TABLE)
INSERT INTO property_amenities (property_id, amenity_id)
VALUES
    -- Property 1
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 7), (1, 9), (1, 10),
    -- Property 2
    (2, 1), (2, 2), (2, 5), (2, 6), (2, 8), (2, 12),
    -- Property 3
    (3, 1), (3, 2), (3, 3), (3, 4), (3, 6), (3, 7), (3, 9),
    -- Property 4
    (4, 1), (4, 2), (4, 5), (4, 13), (4, 14),
    -- Property 5
    (5, 1), (5, 2), (5, 5), (5, 7), (5, 11), (5, 12),
    -- Property 6
    (6, 1), (6, 5),
    -- Property 7
    (7, 1), (7, 2), (7, 5)
ON CONFLICT DO NOTHING;

-- 9. RENTAL APPLICATIONS
INSERT INTO rental_applications (id, tenant_id, property_id, room_id, start_date, end_date, status)
VALUES
    (1, 4, 1, 1, CURRENT_DATE - INTERVAL '1 month', CURRENT_DATE + INTERVAL '11 month', 'APPROVED'),
    (2, 5, 2, 2, CURRENT_DATE + INTERVAL '10 day', CURRENT_DATE + INTERVAL '1 year', 'PENDING')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('rental_applications', 'id'), COALESCE((SELECT MAX(id) FROM rental_applications), 1));

-- 10. RENTAL CONTRACTS
INSERT INTO rental_contracts (id, application_id, rent, deposit, start_date, end_date, moved_in_at, status)
VALUES
    (1, 1, 650.00, 650.00, CURRENT_DATE - INTERVAL '1 month', CURRENT_DATE + INTERVAL '11 month', CURRENT_TIMESTAMP - INTERVAL '1 month', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('rental_contracts', 'id'), COALESCE((SELECT MAX(id) FROM rental_contracts), 1));

-- 11. PAYMENTS
INSERT INTO payments (id, contract_id, type, amount, due_date, payment_date, payment_method, status)
VALUES
    -- Deposit
    (1, 1, 'DEPOSIT', 650.00, CURRENT_DATE - INTERVAL '1 month', CURRENT_TIMESTAMP - INTERVAL '1 month', 'ABA_PAY', 'PAID'),
    -- Month 1 Rent
    (2, 1, 'RENT', 650.00, CURRENT_DATE - INTERVAL '1 month', CURRENT_TIMESTAMP - INTERVAL '1 month', 'BANK_TRANSFER', 'PAID'),
    -- Month 2 Rent (Upcoming)
    (3, 1, 'RENT', 650.00, CURRENT_DATE + INTERVAL '5 day', NULL, 'ABA_PAY', 'PENDING'),
    -- Overdue Payment (for testing overdue alert)
    (4, 1, 'RENT', 50.00, CURRENT_DATE - INTERVAL '5 day', NULL, NULL, 'OVERDUE')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('payments', 'id'), COALESCE((SELECT MAX(id) FROM payments), 1));

-- 12. VIEWING REQUESTS
INSERT INTO viewing_requests (id, tenant_id, property_id, date, time, status)
VALUES
    (1, 5, 3, CURRENT_DATE + INTERVAL '2 day', '14:30:00', 'PENDING'),
    (2, 4, 4, CURRENT_DATE - INTERVAL '5 day', '10:00:00', 'APPROVED')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('viewing_requests', 'id'), COALESCE((SELECT MAX(id) FROM viewing_requests), 1));

-- 13. REVIEWS
INSERT INTO reviews (id, tenant_id, property_id, rating, comment)
VALUES
    (1, 4, 1, 5, 'Amazing condo with stunning views! Landlord is very friendly, fast response, and everything works perfectly.')
ON CONFLICT (tenant_id, property_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('reviews', 'id'), COALESCE((SELECT MAX(id) FROM reviews), 1));

-- 14. COMPLAINTS
INSERT INTO complaints (id, tenant_id, property_id, description, resolution, status)
VALUES
    (1, 4, 1, 'Water leakage under the kitchen sink.', NULL, 'OPEN'),
    (2, 4, 1, 'Air conditioner in master bedroom needs gas refill.', 'Technician contacted and scheduled for Friday morning.', 'IN_PROGRESS')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('complaints', 'id'), COALESCE((SELECT MAX(id) FROM complaints), 1));

-- 15. CHAT MESSAGES
INSERT INTO messages (id, sender_id, receiver_id, message, created_at)
VALUES
    (1, 4, 2, 'Hello Sokha, could you please confirm if the key card is ready for collection?', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (2, 2, 4, 'Yes Dara! The building reception has your key card ready. Welcome!', CURRENT_TIMESTAMP - INTERVAL '23 hour')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('messages', 'id'), COALESCE((SELECT MAX(id) FROM messages), 1));

-- 16. FAVORITES
INSERT INTO favorites (id, user_id, property_id)
VALUES
    (1, 4, 2),
    (2, 4, 3),
    (3, 5, 1)
ON CONFLICT (user_id, property_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('favorites', 'id'), COALESCE((SELECT MAX(id) FROM favorites), 1));
