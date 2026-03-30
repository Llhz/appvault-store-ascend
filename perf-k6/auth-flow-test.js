import http from "k6/http";
import { check, group, sleep } from "k6";
import { Rate, Trend } from "k6/metrics";

const errorRate = new Rate("errors");
const loginDuration = new Trend("login_duration");
const profileDuration = new Trend("profile_duration");
const reviewDuration = new Trend("review_duration");

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  stages: [
    { duration: "20s", target: 5 },
    { duration: "1m", target: 5 },
    { duration: "20s", target: 0 },
  ],
  thresholds: {
    http_req_duration: ["p(95)<3000"],
    http_req_failed: ["rate<0.10"],
    errors: ["rate<0.10"],
  },
};

// Helper: login and return cookies
function login(email, password) {
  // Get login page (for CSRF token)
  const loginPage = http.get(`${BASE_URL}/auth/login`);
  const csrfMatch = loginPage.body.match(
    /name="_csrf"[^>]*value="([^"]+)"/
  );
  const csrf = csrfMatch ? csrfMatch[1] : "";

  const res = http.post(
    `${BASE_URL}/auth/login`,
    {
      username: email,
      password: password,
      _csrf: csrf,
    },
    {
      redirects: 5,
    }
  );

  loginDuration.add(res.timings.duration);
  return res;
}

export default function () {
  const users = [
    { email: "user@appvault.com", password: "User123!" },
    { email: "alice@example.com", password: "Alice123!" },
    { email: "bob@example.com", password: "Bob12345!" },
  ];

  const user = users[Math.floor(Math.random() * users.length)];

  group("Login Flow", () => {
    const res = login(user.email, user.password);
    check(res, {
      "login: redirect or 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);
  });

  group("Authenticated Browsing", () => {
    // View profile
    let res = http.get(`${BASE_URL}/user/profile`);
    profileDuration.add(res.timings.duration);
    check(res, {
      "profile: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);

    // Browse apps
    res = http.get(`${BASE_URL}/browse`);
    check(res, {
      "browse: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);

    // View an app
    const appId = Math.floor(Math.random() * 20) + 1;
    res = http.get(`${BASE_URL}/app/${appId}`);
    reviewDuration.add(res.timings.duration);
    check(res, {
      "app detail (authed): status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);

    // My reviews
    res = http.get(`${BASE_URL}/user/my-reviews`);
    check(res, {
      "my-reviews: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);
  });
}
