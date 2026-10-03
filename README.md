# DPDMS - Rushinga Provincial Disaster Monitoring and Management System

## Overview
A microservices-based system for capturing, approving, and monitoring disaster
incidents (floods, droughts, fires, zoonotic diseases, mining accidents) across
Rushinga's wards, districts and the province.

## Architecture
- **discovery-service** (Eureka) - service registry
- **gateway** - single entry point, routes to all services
- **auth-service** - JWT login/registration; issues tokens carrying role, hazard, ward, district
- **flood-service, drought-service, fire-service, zoonotic-service, mining-service** - one independently deployable service per hazard, each with its own PostgreSQL database, full CRUD, and hazard-scoped security (a token's `hazard` claim must match the service)
- **dashboard-service** - aggregates approved/pending counts across all five hazards
- **report-service** - generates CSV exports per hazard
- **alert-service** - RabbitMQ-based async alerting with a persisted alert log(email/WhatsApp dispatch simulated; RabbitMQ pattern and logging are real)
- **web-service** - Spring Boot + Thymeleaf service that serves the frontend (`templates/index.html`, with client-side JavaScript in `static/app.js`) at http://localhost:8090. Thymeleaf renders the page server-side; the JavaScript then calls the backend services directly via fetch.
## Tech stack
Java 21, Spring Boot 4.1.1, Spring Cloud (Eureka, Gateway), Spring Data JPA,
PostgreSQL, Spring Security + JWT (jjwt), RabbitMQ.

## Why these choices
- **Microservices**: each hazard is independently deployable/scalable, matching the brief.
- **JWT with role/hazard/ward/district claims**: every hazard service independently verifies the caller's hazard claim, so scoping is enforced at the backend, not the UI.
- **Eureka + Gateway**: services register by name; a single entry point avoids the frontend needing to know every service's address.
- **RabbitMQ**: incident capture is never blocked by alert dispatch.
- **Thymeleaf**: chosen as the required front-end technology (per the brief's mandatory list of Thymeleaf/Angular/Vue/React) because it keeps the whole stack in Java/Spring without adding Node tooling, while the page's own JavaScript still drives all dynamic behaviour against the microservices.

## Roles
- WARD_RECORDER - scoped to one hazard, one ward
- PROVINCIAL_SUPERVISOR - scoped to one hazard; can approve/reject/request corrections
- PROVINCIAL_ADMIN
- NATIONAL_VIEWER - read-only, sees all hazards

## Workflow
PENDING -> APPROVED / REJECTED / CORRECTION_REQUESTED

## Running it
1. Start PostgreSQL and RabbitMQ (`docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management`)
2. Create the databases: authdb, flooddb, droughtdb, firedb, zoonoticdb, miningdb, alertdb
3. Start discovery-service first, then all other services (each: `.\mvnw spring-boot:run`)
4. Start web-service (`cd web-service && .\mvnw spring-boot:run`) and open http://localhost:8090
5. Register a user via POST `/auth/register` on auth-service (port 8081), then log in

## Known limitations (given project timeline)
- Report formats: CSV implemented; PDF/DOCX/XLSX designed but not built
- Alert dispatch: RabbitMQ + logging fully functional; real WhatsApp/email API
  integration not wired up (would require provisioning business API credentials)
- Automated tests: not yet written

