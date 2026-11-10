# Room Rental — Spring Boot Backend

Project សម្រាប់ប្រព័ន្ធជួលបន្ទប់ (Room Rental System) — Java + Spring Boot + Gradle
រៀបចំតាម **Package by Layer** structure (ដូចគម្រោង BookingEase)។

## Folder Structure

```
src/main/java/com/chhatkh/dev
 ├── advices        -> Global exception handling (@RestControllerAdvice)
 ├── configs        -> Security config, CORS, Beans
 ├── controllers    -> REST API endpoints (@RestController)
 ├── dtos           -> Request/Response objects
 ├── entities       -> JPA @Entity classes (map ទៅ database table)
 ├── exceptions     -> Custom exception classes
 ├── filters        -> Servlet filters (ឧ. JWT auth filter)
 ├── repositories   -> JpaRepository interfaces
 ├── services       -> Business logic
 ├── utils          -> Helper / utility classes
 └── RoomRentalApplication.java -> Main class
```

## Sample Feature Included: `Room`

ដើម្បីបង្ហាញលំហូរពេញលេញ (Controller → Service → Repository → Entity),
ខ្ញុំបានបង្កើត sample "Room" CRUD feature រួចហើយ៖

- `entities/Room.java`
- `dtos/RoomDto.java`
- `repositories/RoomRepository.java`
- `services/RoomService.java`
- `controllers/RoomController.java`
- `exceptions/ResourceNotFoundException.java` + `advices/GlobalExceptionHandler.java`

Endpoints:
| Method | Path | Description |
|---|---|---|
| GET | /api/rooms | List all rooms |
| GET | /api/rooms/{id} | Get one room |
| POST | /api/rooms | Create room |
| PUT | /api/rooms/{id} | Update room |
| DELETE | /api/rooms/{id} | Delete room |

## Setup — Option A: Docker (ណែនាំ)

តម្រូវឲ្យមាន Docker + Docker Compose ដំឡើងរួច។

```bash
docker compose up --build
```

នេះនឹង run ២ container៖
- `room-rental-db` → PostgreSQL 16 (port 5432)
- `room-rental-backend` → Spring Boot app (port 8080)

Test: `GET http://localhost:8080/api/rooms`

បញ្ឈប់៖ `docker compose down` (ឬ `docker compose down -v` បើចង់លុប database volume ផងដែរ)

## Setup — Option B: Run Local (មិនប្រើ Docker)

1. ដំឡើង PostgreSQL ក្នុងម៉ាស៊ីន, បង្កើត database ឈ្មោះ `room_rental_db`
2. កំណត់ environment variables (ឬកែផ្ទាល់ក្នុង `application.properties`)៖
   `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
3. Run:
   ```
   ./gradlew bootRun
   ```
4. Test: `GET http://localhost:8080/api/rooms`

## Database Config

`application.yml` អាន config ពី environment variables ដោយមាន default value សម្រាប់ local dev៖

| Env Var | Default |
|---|---|
| `DB_HOST` | localhost |
| `DB_PORT` | 5427 (ឬ 5432) |
| `DB_NAME` | roomrental_db |
| `DB_USERNAME` | postgres |
| `DB_PASSWORD` | student123 |
| `SERVER_PORT` | 1212 |

---

## 🧪 Test Data & Pre-seeded Accounts

Backend ត្រូវបានបំពាក់ដោយ **`DataInitializer.java`** ដែលនឹងបញ្ចូល test data ពេញលេញដោយស្វ័យប្រវត្តិនៅពេល Spring Boot ចាប់ផ្តើម (Run):
- ឬអ្នកអាច run ផ្ទាល់តាម SQL Script: [`database/seed_data.sql`](database/seed_data.sql)

### 🔑 Test Accounts (Password សម្រាប់គ្រប់ account: `password123`)

| Username | Role | Full Name | Phone | Purpose |
|---|---|---|---|---|
| `admin` | `ROLE_ADMIN` | System Admin | `012345678` | Test Admin Dashboard, property approval, user management |
| `landlord1` | `ROLE_LANDLORD` | Sokha Chan | `098765432` | Test landlord listing properties, approving applications |
| `landlord2` | `ROLE_LANDLORD` | Vannak Khem | `098765433` | Test second landlord listings & viewing requests |
| `tenant1` | `ROLE_TENANT` | Dara Keo | `011223344` | Test active contract, payment history, review & complaints |
| `tenant2` | `ROLE_TENANT` | Bopha Meng | `011223345` | Test pending applications, viewing request scheduling |
| `user1` | `ROLE_USER` | Channa Pheng | `088997766` | Test standard user browsing & registration |

### 📦 Pre-seeded Data Included
1. **11 Roles**: `ROLE_ADMIN`, `ROLE_LANDLORD`, `ROLE_TENANT`, `ROLE_USER`, etc.
2. **14 Amenities**: WiFi, AC, Swimming Pool, Gym, Parking, Balcony, Security 24/7, Washing Machine, Elevator, Furnished, Pet Friendly, Kitchen, Refrigerator, Hot Water.
3. **7 Properties**:
   - Modern 1-Bedroom Condo at BKK1 ($650, PUBLISHED)
   - Spacious 2-Bedroom Apartment in Toul Kork ($450, PUBLISHED)
   - Luxury Riverside View Penthouse ($1200, PUBLISHED)
   - Affordable Studio Room near Russian Market ($260, PUBLISHED)
   - Modern Family Villa in Sen Sok ($1500, PUBLISHED)
   - Budget Single Room near RUPP/ITC ($120, PUBLISHED)
   - Newly Built Studio Pending Approval ($280, PENDING_REVIEW for Admin test)
4. **Rooms & Photos**: Real room dimensions, prices, and photo links from Unsplash.
5. **Rental Application & Contract**: Active contract for Tenant 1 with moved-in date.
6. **Payments**: Deposit (PAID), Month 1 Rent (PAID), Month 2 Rent (PENDING), Overdue Utility (OVERDUE).
7. **Viewing Requests, Reviews, Complaints, Chat Messages, and Favorites**.

### 🚀 Quick Test Commands
1. **Swagger UI**: `http://localhost:1212/swagger-ui.html`
2. **Public Property Search**:
   ```bash
   curl -X GET "http://localhost:1212/api/properties"
   ```
3. **Login Example**:
   ```bash
   curl -X POST "http://localhost:1212/api/auth/token" \
        -H "Content-Type: application/json" \
        -d '{"username":"landlord1","password":"password123"}'
   ```

