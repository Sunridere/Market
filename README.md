# Market

Full-stack e-commerce application with a Spring Boot backend and React frontend.

## Tech Stack

**Backend**
- Java 17, Kotlin
- Spring Boot 3.2.5 (Web, Security, Data JPA, Validation)
- PostgreSQL + Liquibase
- JWT authentication (access + refresh tokens)
- MapStruct, Lombok
- Swagger UI (`/swagger-ui.html`)

**Frontend**
- React 18 + TypeScript
- Vite, Tailwind CSS, React Router v6, Axios

**Infrastructure**
- Docker + Docker Compose
- Nginx (frontend)

---

## Running with Docker Compose

```bash
docker-compose up --build
```

| Service   | URL                        |
|-----------|----------------------------|
| Frontend  | http://localhost            |
| Backend   | http://localhost:8087       |
| Swagger   | http://localhost:8087/swagger-ui.html |

Default credentials for PostgreSQL: `postgres / postgres`

---

## Running Locally

**Requirements:** Java 17, Maven, Node.js 18+, PostgreSQL

**Backend**
```bash
# Set environment variables
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/market
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=your-secret-key

mvn spring-boot:run
```

Backend starts on `http://localhost:8080`

**Frontend**
```bash
cd frontend
npm install
npm run dev
```

Frontend starts on `http://localhost:5173`

---

## API Overview

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/sign-up` | Register | Public |
| POST | `/auth/sign-in` | Login | Public |
| POST | `/auth/refresh` | Refresh token | Public |
| POST | `/auth/logout` | Logout | User |
| GET | `/api/v1/user/me` | Current user | User |
| GET | `/api/v1/products` | Product list (paginated) | Public |
| GET | `/api/v1/products/{id}` | Product details | Public |
| POST | `/api/v1/products` | Create product | Admin |
| PUT | `/api/v1/products` | Update product | Admin |
| DELETE | `/api/v1/products/{id}` | Delete product | Admin |
| GET | `/api/v1/categories` | Category list | Public |
| POST | `/api/v1/categories` | Create category | Admin |
| GET | `/api/v1/cart` | Get cart | User |
| POST | `/api/v1/cart/items` | Add to cart | User |
| PUT | `/api/v1/cart/items/{productId}` | Update quantity | User |
| DELETE | `/api/v1/cart/items/{productId}` | Remove item | User |
| POST | `/api/v1/orders` | Place order | User |
| GET | `/api/v1/orders` | Order history | User |
| POST | `/api/v1/files/upload` | Upload file | Admin |

---

## Project Structure

```
Market/
├── src/main/java/org/sunrider/market/
│   ├── cart/          # Shopping cart
│   ├── product/       # Products & categories
│   ├── order/         # Orders
│   ├── user/          # Users & profiles
│   ├── security/      # JWT, rate limiting
│   ├── file/          # File upload
│   └── exception/     # Error handling
├── frontend/
│   └── src/
│       ├── components/ # UI components
│       ├── pages/      # Page components
│       ├── services/   # API layer
│       ├── context/    # Global state
│       └── hooks/      # Custom hooks
└── docker-compose.yml
```

---

## Features

- JWT authentication with refresh tokens
- Role-based access (USER / ADMIN)
- Brute-force protection on login
- Product catalog with image upload
- Shopping cart
- Order placement with delivery address
- Admin dashboard (product, category, user management)
- Liquibase database migrations
