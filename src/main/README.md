# 🚀 JWT Secured Student API
### A Spring Boot Project Focused on Understanding Real-World Backend Systems

## 🧠 Purpose of This Project

This project was not built just to make an API “work”.

It was built to understand how modern backend systems actually operate under the hood.

In an era where AI can generate code instantly, the real difference is:

> Understanding the system behind the code — not just writing it.

This project focuses on learning and implementing:

- How JWT authentication works internally
- How Spring Security processes requests
- The difference between Authentication and Authorization
- How the Security Filter Chain operates
- How to design clean, consistent, and testable APIs

---

## 🧱 Project Overview

This is a production-style REST API that includes:

- 🔐 JWT-based authentication
- 🛡️ Role-based authorization (USER / ADMIN)
- 📦 Layered architecture (Controller / Service / Repository)
- ⚠️ Centralized exception handling with consistent responses
- 📊 Pagination, sorting, and search capabilities
- 📄 Swagger UI for interactive API exploration
- 🧪 Integration tests validating security behavior

> The focus is not only functionality, but clarity, structure, and correctness.

---

## ⚙️ Technologies Used

- Java 17+
- Spring Boot
- Spring Security
- JWT (`io.jsonwebtoken`)
- Spring Data JPA
- H2 Database (development)
- Swagger / OpenAPI
- JUnit & MockMvc
- Postman

---

## 🧩 Architecture & Design Decisions

This project follows a layered architecture:

`Controller → Service → Repository → Database`

### Controller Layer
Handles HTTP requests and responses.

- Uses `ResponseEntity` for proper HTTP control
- Applies role-based access using `@PreAuthorize`

### Service Layer
Contains business logic.

- Handles validation beyond annotations
- Enforces rules like unique email constraint
- Manages update logic with transactional consistency

### Repository Layer
Handles data access using Spring Data JPA.

- Uses `JpaRepository`
- Supports pagination and custom queries

### DTO Design

Entities are never exposed directly.

Instead:

- `StudentRequest` → incoming data
- `StudentResponse` → outgoing data

This ensures:
- API stability
- controlled data exposure
- separation between persistence and API layers

### Validation

Input validation is handled using annotations such as:

- `@NotBlank`
- `@Email`
- `@Size`

This prevents invalid data before it reaches business logic.

### Business Rules

Custom rules such as unique email validation are enforced at service level.

## Exception Handling

A global exception handler ensures consistent API responses such as:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/students"
}

```


### Stateless Authentication


The system is fully stateless:

- No session storage  
- Every request carries its own authentication (JWT)

This improves scalability and simplifies backend logic.

---

## 🔐 Authentication Flow

### 1. Register

`POST /auth/register`

- Creates a new user  
- Password is hashed with BCrypt  
- JWT token is generated  

---

### 2. Login

`POST /auth/login`

- Credentials are verified  
- A new JWT token is generated  

---

### 3. Authenticated Request

```http
Authorization: Bearer <token>
```

Each request:

1. Extracts the token
2. Parses the username
3. Loads the user from the database
4. Validates the token
5. Sets the SecurityContext

---

## 🔄 Security Filter Flow

```text
Request
   ↓
JwtAuthenticationFilter
   ↓
Extract token
   ↓
Validate token
   ↓
Load user
   ↓
Set SecurityContext
   ↓
Controller
```

---

## 🛡️ Authorization (Role-Based Access)

Access control is enforced using annotations like:

```java
@PreAuthorize("hasRole('ADMIN')")
```

### Example Access Rules

| Endpoint | Access |
|----------|--------|
| GET /students | USER, ADMIN |
| GET /students/{id} | USER, ADMIN |
| POST /students | ADMIN only |
| PUT /students/{id} | ADMIN only |
| DELETE /students/{id} | ADMIN only |
| GET /users/me | USER, ADMIN |

---

## ⚠️ Error Handling Strategy

### 401 Unauthorized

Triggered when:

- No token is provided
- Token is invalid or expired

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required"
}
```

---

### 403 Forbidden

Triggered when:

- The user is authenticated but does not have permission

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource"
}
```

---

## 📊 Pagination & Search

Example:

`GET /students?page=0&size=10&sort=id,asc&search=ali`

This supports scalable data access patterns.

---

## 🧪 Testing Strategy

Integration tests validate real system behavior:

- ✔ No token → 401
- ✔ USER accessing ADMIN endpoint → 403
- ✔ ADMIN access → success

```java
@SpringBootTest
@AutoConfigureMockMvc
```

---

## 📄 Swagger UI

Interactive API documentation is available at:

`http://localhost:8080/swagger-ui.html`

---

## ⚙️ Environment Setup

### Active Profile: Development

- H2 in-memory database
- SQL logging enabled
- Fast local testing

```properties
spring.profiles.active=dev
```

---

## 🚀 Running the Project

### 1. Clone the repository

```bash
git clone <your-repo-url>
```

### 2. Run the application

```bash
mvn spring-boot:run
```

---

## 🔑 Using JWT

After login, include the token in protected requests:

```http
Authorization: Bearer <your-token>
```

---

## 🧠 What I Learned

- JWT internals
- Spring Security filter chain
- Stateless authentication
- Clean API design
- Exception handling
- Integration testing

---

## 💬 Personal Note

Anyone can generate code with AI.  
Understanding it is what makes you an engineer.

---

## ⭐ If you found this project useful, consider giving it a star.