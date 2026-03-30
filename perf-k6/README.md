# AppVault Store — k6 Performance Tests

## Prerequisites

- [k6](https://k6.io/docs/getting-started/installation/) installed
- AppVault application running on `http://localhost:8080`

### Install k6 (macOS)

```bash
brew install k6
```

## Test Scenarios

| Script | Description | VUs | Duration |
|--------|-------------|-----|----------|
| `smoke-test.js` | Quick health check of all endpoints | 10 | ~50s |
| `load-test.js` | Standard load test with ramp pattern | 10→30 | ~3.5m |
| `stress-test.js` | High-load stress test | 50→100 | ~7m |
| `auth-flow-test.js` | Authenticated user journeys | 5 | ~1.5m |

## Running Tests

```bash
# Start the application first (in another terminal)
cd .. && mvn spring-boot:run

# Smoke test (quick validation)
k6 run perf-k6/smoke-test.js

# Load test (standard)
k6 run perf-k6/load-test.js

# Stress test (high load)
k6 run perf-k6/stress-test.js

# Authenticated flow test
k6 run perf-k6/auth-flow-test.js

# Override base URL
k6 run -e BASE_URL=http://staging:8080 perf-k6/load-test.js

# Output results to JSON
k6 run --out json=results.json perf-k6/load-test.js
```

## Thresholds

All tests include built-in thresholds:
- **p95 response time**: < 2s (load), < 3s (stress), < 1.5s (smoke)
- **Error rate**: < 5% (load), < 10% (stress), < 1% (smoke)

Tests will exit with non-zero status if thresholds are breached (CI-friendly).

## Custom Metrics

- `home_page_duration` — Home page response time trend
- `browse_page_duration` — Browse page response time trend  
- `app_detail_duration` — App detail page response time trend
- `search_duration` — Search endpoint response time trend
- `login_duration` — Login request duration
- `errors` — Custom error rate counter
