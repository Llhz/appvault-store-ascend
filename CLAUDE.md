# CLAUDE.md — AppVault Store

## Hard Rules

- Java 8, Spring Boot 2.4.13, Maven — do not upgrade without explicit approval
- Layered architecture: Controller → Service (interface + impl) → Repository → Entity
- Services **must** have an interface and a separate `*Impl` class
- Controllers return Thymeleaf view names, not REST/JSON (sole exception: `ReviewController#markHelpful`)
- DTOs (`dto/`) for form binding; entities (`model/`) for JPA — never use entities in controller signatures for form submission
- `@Transactional` on write methods; `@Transactional(readOnly = true)` on reads in service impls
- Throw `ResourceNotFoundException` for missing entities — never return raw HTTP status codes from controllers
- Any code that creates/updates/deletes reviews **must** call `ReviewServiceImpl#recalculateRating()`
- One review per user per app — enforced by DB unique constraint `(user_id, app_listing_id)`; service checks `hasUserReviewedApp()` before saving
- Passwords hashed with BCrypt — never store or log plaintext passwords
- Use Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`) — no manual getters/setters
- Use Spring Data derived query methods; `@Query` with JPQL only for complex queries
- Reuse template fragments in `templates/fragments/` (header, navbar, footer, app-card)
- Frontend: Bootstrap 5 + Font Awesome 6 + vanilla JS — no build tooling

## Authority & Links

- Primary instructions: `.github/copilot-instructions.md`
- Security config: `src/main/java/com/appvault/config/SecurityConfig.java`
- Data seeding: `src/main/java/com/appvault/config/DataInitializer.java`
- Global error handling: `src/main/java/com/appvault/exception/GlobalExceptionHandler.java`
- Custom validation example: `src/main/java/com/appvault/validation/` (`@PasswordMatches`)
- E2E tests: `e2e-cypress/`, `e2e-playwright/`, `e2e-robot/`
- Performance tests: `perf-k6/`

## Setup / Test

- **Requires**: Java 8, Maven
- H2 in-memory database — recreated on every startup (`create-drop`)
- Default admin: `admin@appvault.com` / `Admin123!`
- Two roles: `ROLE_ADMIN`, `ROLE_USER`
- Public routes: `/`, `/browse/**`, `/app/**`, `/search`, `/auth/**`
- Authenticated: `/user/**`, `/review/**`
- Admin-only: `/admin/**`

## Workflow

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run all tests
mvn test

# Run single test
mvn test -Dtest=UserServiceTest#registerNewUser

# Start app (port 8080)
mvn spring-boot:run

# Docker image (local daemon)
mvn compile jib:dockerBuild -DskipTests
```

## Stop Conditions

- **Refuse** to upgrade Java version, Spring Boot version, or swap H2 for another DB without explicit user confirmation
- **Refuse** to bypass security checks or expose admin routes publicly
- **Refuse** to delete `DataInitializer` seed data logic without confirmation
- **Ask** before adding new dependencies to `pom.xml`
- **Ask** before modifying `SecurityConfig` route access rules
- **Ask** before changing the service interface/impl pattern to a different style
