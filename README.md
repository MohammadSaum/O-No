# O-No

A personal DSA practice tracker — log every problem you solve with difficulty, source, and notes, and follow your progress from an actual dashboard instead of a spreadsheet.

[Live Demo](https://o-no-jse1.onrender.com/) • [GitHub](https://github.com/MohammadSaum/O-No)

## Overview

O-No replaces a messy Excel sheet with a real full-stack app for tracking coding-interview prep. It stores every problem you solve — source link, difficulty, topic, notes — and surfaces progress, revision scheduling, and favorites through a dashboard, backed by a Spring Boot REST API with a React frontend.

## Features

- JWT-based authentication with per-user data isolation
- Full CRUD for questions — source link, difficulty, topic, notes
- Filtering and pagination across the question list
- Progress tracking and revision scheduling
- Favorites and per-question notes
- Dashboard analytics (question counts, difficulty breakdown, favorites, revision due)

## Tech Stack

- **Frontend:** React, Tailwind CSS
- **Backend:** Spring Boot, Spring Security, Spring Data JPA / Hibernate
- **Database:** MySQL
- **Auth:** JWT (stateless), BCrypt password hashing
- **Infra:** Docker, Maven

## Architecture

```
                ┌──────────────────┐
                │   React Frontend │
                └────────┬─────────┘
                         │ REST / JWT
                         ▼
                ┌──────────────────┐
                │  Spring Boot API │
                │  Controller      │
                │       ↓          │
                │  Service         │
                │       ↓          │
                │  JPA / Hibernate │
                └────────┬─────────┘
                         ▼
                ┌──────────────────┐
                │  MySQL (Aiven)   │
                └──────────────────┘
```

**Deployment**

```
React       → Render
Spring Boot → Render
MySQL       → Aiven
```

## API Overview

17+ REST endpoints across 5 modules:

| Module | Example endpoints |
|---|---|
| Auth | `POST /api/users/register`, `POST /api/users/login` |
| Questions | `GET / POST / PUT / DELETE /api/questions` |
| Progress | `GET /api/progress/{questionId}`, `PATCH /api/progress/{questionId}/favorite`, `PATCH /api/progress/{questionId}/confidence`, `PATCH /api/progress/{questionId}/revise` |
| Notes | `GET / POST / PUT / DELETE /api/notes` |
| Dashboard | `GET /api/dashboard/summary` |

## Performance

Load-tested with [k6](https://k6.io/) at 100 concurrent virtual users:

- **637 req/s** sustained throughput
- **169 ms** p95 latency
- **99.97%** request success rate

## Running Locally

```bash
git clone https://github.com/MohammadSaum/O-No.git
cd o-no

# Backend
cd prepForge-backend
./mvnw spring-boot:run

# Frontend
cd ../frontend
npm install
npm run dev
```

Copy `.env.example` to `.env` and set your DB credentials and JWT secret before running.

## Docker Setup

```bash
docker-compose up --build
```

Spins up the frontend, backend, and MySQL together with environment-based configuration.

## Project Structure

```
o-no/
├── prepforge-backend/     # Spring Boot API (Controller–Service–Repository)
├── frontend/    # React app
├── docker-compose.yml
├── loadtest.js
└── README.md
```

## Screenshots

<div align="center">

### Landing Page
<img width="850" alt="Landing Page" src="https://github.com/user-attachments/assets/bd45b433-07fa-44b5-a0a1-7fd5ccd37de8" />

---

### Dashboard
<img width="850" alt="Dashboard" src="https://github.com/user-attachments/assets/3e519133-6327-4f6f-83bf-cf1de5bdc1ef" />

---

### Questions List
<img width="850" alt="Questions List" src="https://github.com/user-attachments/assets/3356d1d6-3fa3-48e0-9e2a-f511d2457c35" />

---

### Question Detail
<img width="850" alt="Question Detail" src="https://github.com/user-attachments/assets/1b60467b-d79c-4853-90ed-56968e1e5e02" />

---

### Add Question
<img width="850" alt="Add Question" src="https://github.com/user-attachments/assets/137c75c8-9266-4b50-88cc-1cacf4a70ff5" />

</div>

## Future Improvements

- Dedicated tag system for multi-topic questions
- GitHub OAuth login
- CI/CD pipeline for automated deployment
- Export progress history (CSV/PDF)
