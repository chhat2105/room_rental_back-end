# Room Rental — Spring Boot Backend

Complete Room Rental System backend built with **Java 17**, **Spring Boot 3.3.4**, **Spring Security (JWT)**, **Spring Data JPA**, **PostgreSQL**, and **Springdoc OpenAPI (Swagger UI)**.

---

## 🏗️ Architecture & Folder Structure

Organized cleanly by layer:
```
src/main/java/com/chhatkh/dev
 ├── advices        -> Global exception handling (@RestControllerAdvice)
 ├── configs        -> SecurityConfig (JWT + CORS), OpenApiConfig (Swagger), DataSeeder
 ├── controllers    -> REST endpoints (Auth, Room, Booking, Review, Favorite, Inquiry, File, Dashboard)
 ├── dtos           -> Request & Response DTOs
 ├── entities       -> JPA entities (User, Room, Booking, Review, Favorite, Inquiry, Role, RoomType, BookingStatus)
 ├── exceptions     -> Custom exceptions (ResourceNotFoundException, BadRequestException, UnauthorizedException)
 ├── filters        -> JWT Authentication Filter
 ├── repositories   -> Spring Data JPA Repositories
 ├── security       -> CustomUserDetails, CustomUserDetailsService, JwtTokenProvider
 ├── services       -> Business logic services
 └── RoomRentalApplication.java
```

---

## 🔑 Default Seeded Accounts

The application automatically seeds initial data if the database is empty:

| Role | Username | Email | Password |
|---|---|---|---|
| **Admin** | `admin` | admin@roomrental.com | `admin123` |
| **Landlord** | `chhat_landlord` | landlord@roomrental.com | `landlord123` |
| **Tenant** | `sokha_tenant` | sokha@roomrental.com | `tenant123` |

---

## 📡 REST API Endpoints Overview

### 1. Authentication & Profile (`/api/auth`)
- `POST /api/auth/register` - Register a new user (`ROLE_TENANT` or `ROLE_LANDLORD`)
- `POST /api/auth/login` - Login with username/email and password, returns Bearer token
- `GET /api/auth/me` - Get current authenticated user profile
- `PUT /api/auth/profile` - Update full name, phone number, bio, avatar
- `PUT /api/auth/change-password` - Change password

### 2. Rooms (`/api/rooms`)
- `GET /api/rooms` - Search & filter rooms (query params: `keyword`, `city`, `district`, `roomType`, `minPrice`, `maxPrice`, `available`)
- `GET /api/rooms/{id}` - Get room details by ID (including average rating and review count)
- `GET /api/rooms/my-rooms` - List rooms owned by the authenticated landlord
- `POST /api/rooms` - Create a new room (Landlord)
- `PUT /api/rooms/{id}` - Update room details (Owner / Admin)
- `PATCH /api/rooms/{id}/toggle-availability` - Toggle available/rented status
- `DELETE /api/rooms/{id}` - Delete room

### 3. Bookings & Reservations (`/api/bookings`)
- `POST /api/bookings` - Create booking request for a room (Tenant)
- `GET /api/bookings/{id}` - View booking details
- `GET /api/bookings/my-bookings` - Tenant's booking history
- `GET /api/bookings/landlord/requests` - Landlord's incoming booking requests
- `PUT /api/bookings/{id}/status` - Update status (`CONFIRMED`, `REJECTED`, `CANCELLED`, `COMPLETED`)

### 4. Reviews & Ratings (`/api/rooms/{roomId}/reviews` & `/api/reviews`)
- `POST /api/rooms/{roomId}/reviews` - Submit review and rating (1–5 stars)
- `GET /api/rooms/{roomId}/reviews` - View all reviews for a room
- `DELETE /api/reviews/{id}` - Delete a review

### 5. Favorites / Wishlist (`/api/favorites`)
- `POST /api/favorites/{roomId}` - Toggle bookmark / favorite
- `GET /api/favorites/my-favorites` - List saved rooms for the user
- `GET /api/favorites/{roomId}/status` - Check if room is bookmarked

### 6. Inquiries / Messages (`/api/inquiries`)
- `POST /api/inquiries` - Send direct inquiry/message to room landlord
- `GET /api/inquiries/received` - Landlord receives tenant inquiries
- `GET /api/inquiries/sent` - Tenant checks sent inquiries

### 7. File & Image Uploads (`/api/uploads`)
- `POST /api/uploads/image` - Upload single image (room photo or avatar)
- `POST /api/uploads/images` - Upload multiple room photos
- `GET /api/uploads/{filename}` - Retrieve / stream uploaded image

### 8. Dashboard & Users (`/api/dashboard` & `/api/users`)
- `GET /api/dashboard/stats` - Total rooms, available rooms, bookings, total revenue (Admin only)
- `GET /api/users` - Admin user listing
- `PATCH /api/users/{id}/toggle-status` - Admin activate/deactivate account

---

## 📖 Swagger API Documentation

When the application is running, access Swagger UI in your browser:
- **Swagger UI**: [http://localhost:9091/swagger-ui.html](http://localhost:9091/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:9091/v3/api-docs](http://localhost:9091/v3/api-docs)

To test secured endpoints in Swagger:
1. Call `POST /api/auth/login`
2. Copy the `token` from response
3. Click the **Authorize** button (top right of Swagger UI)
4. Enter: `Bearer <your_token>` and click Authorize.

---

## 🚀 Running Locally

```bash
# 1. Build and run Spring Boot
./gradlew bootRun

# Or build the jar
./gradlew build -x test
```

The default profile uses PostgreSQL on `localhost:5432` with database/user/password
`room_rental_db` / `postgres` / `postgres`. Override these using `DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` if your local database differs.

## 🐳 Run with Docker

```bash
docker compose up --build
```

The API is then available on `http://localhost:8080`, and Swagger UI is at
`http://localhost:8080/swagger-ui.html`. Set a long, unique `JWT_SECRET` environment
variable before deploying outside local development.

## ✅ Verification

```bash
./gradlew test
```
