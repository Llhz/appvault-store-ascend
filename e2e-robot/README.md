# AppVault Store — Robot Framework Tests

## Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| **Python** | 3.9+ | `brew install python` (macOS) |
| **pip** | latest | Bundled with Python |
| **Node.js** | 18+ | `brew install node` (required by Browser library) |
| **Robot Framework** | 7.1+ | Installed via `requirements.txt` |
| **robotframework-browser** | 18.9+ | Playwright-backed browser library |

## Setup (one-time)

```bash
cd e2e-robot

# Create a virtual environment (recommended)
python3 -m venv .venv
source .venv/bin/activate

# Install Robot Framework + Browser library
pip install -r requirements.txt

# Initialize Browser library (downloads Playwright browsers)
rfbrowser init
```

## Running Tests

Make sure the Spring Boot app is running on `http://localhost:8080` first:

```bash
# In another terminal
cd .. && mvn spring-boot:run
```

Then run the tests:

```bash
# Run all tests
robot --outputdir results tests/

# Run a single test file
robot --outputdir results tests/home.robot

# Run a single test case by name
robot --outputdir results --test "Home Page Loads Successfully" tests/

# Run with a different base URL
robot --outputdir results --variable BASE_URL:http://staging:8080 tests/

# Run headed (see the browser)
robot --outputdir results --variable BROWSER_HEADLESS:false tests/
```

## Test Reports

After running, open the HTML report:

```bash
open results/report.html   # macOS
# or
open results/log.html      # Detailed step-by-step log
```

Reports include:
- **report.html** — Summary dashboard with pass/fail stats
- **log.html** — Detailed execution log with screenshots on failure
- **output.xml** — Machine-readable results (CI integration)

## Screenshots & Video

Screenshots are captured automatically on failure. To capture on every keyword:

```bash
robot --outputdir results --listener RobotFramework:screenshot tests/
```

Or add to individual tests:

```robot
Take Screenshot
```

## Test Structure

```
e2e-robot/
├── requirements.txt          # Python dependencies
├── resources/
│   └── common.resource       # Shared keywords & variables
├── results/                  # Test output (gitignored)
└── tests/
    ├── home.robot            # Home page tests
    ├── auth.robot            # Authentication tests
    ├── browse_search.robot   # Browse & search tests
    ├── user_features.robot   # Authenticated user tests
    └── admin.robot           # Admin feature tests
```

## Test Scenarios

| File | Tests | Coverage |
|------|-------|----------|
| `home.robot` | 3 | Home page content, categories, navigation |
| `auth.robot` | 6 | Login, register, error handling, redirects |
| `browse_search.robot` | 5 | Browse, category filter, search, app detail |
| `user_features.robot` | 3 | Profile view/update, my reviews |
| `admin.robot` | 6 | Dashboard, app CRUD, users, RBAC |
| **Total** | **23** | |
