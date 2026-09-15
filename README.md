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

`application.properties` អាន config ពី environment variables ដោយមាន default value សម្រាប់ local dev៖

| Env Var | Default |
|---|---|
| `DB_HOST` | localhost |
| `DB_PORT` | 5432 |
| `DB_NAME` | room_rental_db |
| `DB_USERNAME` | postgres |
| `DB_PASSWORD` | postgres |
| `JWT_SECRET` | (default dev secret — **ត្រូវប្តូរនៅ production**) |

នៅក្នុង `docker-compose.yml`, backend service ដាក់ `DB_HOST=postgres` (ឈ្មោះ service) ដើម្បីភ្ជាប់ទៅ container postgres ដោយស្វ័យប្រវត្តិ។

## Next Steps (សម្រាប់ project របស់អ្នក)

- បន្ថែម `User` entity (Owner / Tenant roles)
- បន្ថែម `Booking`/`Rental` entity ភ្ជាប់ Room ទៅ User
- Implement JWT authentication ក្នុង `filters/` + `configs/SecurityConfig`
- Image upload សម្រាប់រូបភាពបន្ទប់ (ដាក់ក្នុង `utils/` ឬបង្កើត `FileStorageService`)
- **Deploy** — Docker image នេះអាចយកទៅ deploy លើ VPS (Docker Compose) ឬ cloud platform ណាមួយដែលគាំទ្រ Docker (ជំហាននេះនឹងធ្វើនៅពេលក្រោយ)
