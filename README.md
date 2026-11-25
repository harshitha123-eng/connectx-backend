# ConnectX Backend

REST and WebSocket backend for the ConnectX chat application.

## Features
- JWT authentication
- Real-time private and group messaging over WebSocket
- Group management with admin controls
- Message status, reactions, deletion and notifications
- File upload and download
- Message search

## Tech Stack
Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, REST API, WebSocket (STOMP, SockJS), PostgreSQL (Neon), Maven, Docker, deployed on Render
## Configuration
Set these environment variables:
- `SPRING_DATASOURCE_URL` (jdbc:postgresql://HOST/DB?sslmode=require)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Run Locally
1. `git clone https://github.com/harshitha123-eng/connectx-backend.git`
2. Set the environment variables above
3. `./mvnw spring-boot:run`

## Notes
Uploaded files are stored on the server disk and may be cleared when the service restarts.

## Frontend
https://github.com/harshitha123-eng/connectx-frontend
