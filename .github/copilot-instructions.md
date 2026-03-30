# Copilot Instructions — AppVault Store

## Build & Test

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run a single test method
mvn test -Dtest=UserServiceTest#registerNewUser

# Run the application (starts on port 8080)
mvn spring-boot:run

# Build Docker image to local daemon (requires Docker running)
mvn compile jib:dockerBuild -DskipTests

# Build and push Docker image to a remote registry
mvn compile jib:build -DskipTests

# Build Docker image as a local tarball (no Docker needed)
mvn compile jib:buildTar -DskipTests
```

Java 8, Spring Boot 2.4.13, Maven. Docker images built with [Jib Maven Plugin](https://github.com/GoogleContainerTools/jib) (`eclipse-temurin:8-jre` base).

## Terminal Execution Rules

**NEVER run long-running terminal commands in the foreground.** All Maven builds, Docker builds, test runs, and server starts must follow the background execution pattern below. This prevents the IDE from interrupting or timing out the process.

### Background Execution Pattern

Every long-running command follows a 3-phase workflow:

#### Phase 1: Launch (Background)
```bash
# Run as a background process with output captured to a log file
# Set isBackground: true in the terminal tool
cd <workspace> && <command> > build-logs/<name>-run.log 2>&1
```

#### Phase 2: Poll (Separate Foreground Terminal)
```bash
# Check for completion markers — never touch the background terminal
while ! grep -qE '<MARKER>' build-logs/<name>-run.log 2>/dev/null; do sleep 10; done && echo 'DONE'
```

Completion markers:
- **Maven build/test**: `BUILD SUCCESS` or `BUILD FAILURE`
- **Jib Docker build**: `Built image` or `BUILD FAILURE`
- **Spring Boot run**: `Started .* in .* seconds`

#### Phase 3: Analyze (Read Log File)
Use the file read tool to read `build-logs/<name>-run.log`. Do **not** rely on terminal output.

### Rules

1. **NEVER interrupt a running terminal** — killing a build or test wastes all progress
2. **ALWAYS use `isBackground: true`** with `> logfile 2>&1` redirection for Maven, Docker, and test commands
3. **NEVER use `| tee`** — it requires a foreground terminal which can be interrupted
4. **Poll from a SEPARATE foreground terminal** — check the log file, never touch the build terminal
5. **Read the log file after completion** — use the file read tool, not terminal output

This pattern applies to **all** terminal commands that take more than a few seconds: `mvn compile`, `mvn test`, `mvn package`, `mvn jib:dockerBuild`, `mvn spring-boot:run`, and all E2E/performance test suites.

## Architecture

Server-rendered Spring Boot MVC app — an app store marketplace. Thymeleaf templates, Spring Security with role-based access, H2 in-memory database (recreated on every startup via `create-drop`).

### Layered structure

**Controller → Service (interface + impl) → Repository → Entity**

- Services always have an interface (`UserService`) and a separate implementation (`UserServiceImpl`). Follow this pattern when adding new services.
- Controllers return Thymeleaf view names (not REST/JSON), except `ReviewController#markHelpful` which returns `@ResponseBody` JSON for an AJAX call.
- DTOs (`dto/` package) are used for form binding; entities (`model/` package) are JPA-managed. Don't use entities directly in controller method signatures for form submission.

### Security model

- Two roles: `ROLE_ADMIN`, `ROLE_USER`. Configured in `SecurityConfig`.
- Public routes: `/`, `/browse/**`, `/app/**`, `/search`, `/auth/**`
- Authenticated routes: `/user/**`, `/review/**`
- Admin-only routes: `/admin/**`
- Passwords hashed with BCrypt. Remember-me token lasts 14 days.

### Data initialization

`DataInitializer` (CommandLineRunner) seeds roles, users, categories, apps, and reviews on startup when the database is empty. Default admin: `admin@appvault.com` / `Admin123!`.

## Key Conventions

- **Transaction annotations**: `@Transactional` on write methods, `@Transactional(readOnly = true)` on read methods in service impls.
- **Custom validation**: Class-level `@PasswordMatches` annotation on `UserRegistrationDto` with a custom `ConstraintValidator`. Follow this pattern for cross-field validation.
- **Review uniqueness**: One review per user per app, enforced by a DB unique constraint on `(user_id, app_listing_id)`. Service checks `hasUserReviewedApp()` before saving.
- **Rating recalculation**: `ReviewServiceImpl#recalculateRating()` updates `AppListing.rating` and `reviewCount` after every review save/delete. Any new review-modifying code must call this.
- **Exception handling**: Throw `ResourceNotFoundException` for missing entities — `GlobalExceptionHandler` (@ControllerAdvice) maps it to the `error/404` template. Don't return raw HTTP status codes from controllers.
- **Lombok**: Entities and DTOs use `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`. Avoid writing manual getters/setters.
- **Template fragments**: Shared UI in `templates/fragments/` (header, navbar, footer, app-card). Reuse these in new pages.
- **Frontend stack**: Bootstrap 5 + Font Awesome 6 + vanilla JS (`static/js/app.js`). No frontend build tooling — static files served directly.
- **Repository queries**: Use Spring Data derived query methods when possible. Use `@Query` with JPQL for complex queries (see `AppListingRepository#searchByQuery`).
