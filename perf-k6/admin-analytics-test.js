import http from "k6/http";
import { check, group, sleep } from "k6";
import { Rate, Trend } from "k6/metrics";

const errorRate = new Rate("errors");
const downloadsDuration = new Trend("downloads_stats_duration");
const ratingsDuration = new Trend("ratings_stats_duration");
const categoriesDuration = new Trend("categories_stats_duration");

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  vus: 10,
  duration: "30s",
  thresholds: {
    http_req_duration: ["p(95)<500"],
    http_req_failed: ["rate<0.01"],
    errors: ["rate<0.01"],
  },
};

// Login as admin and return authenticated session (cookies via jar)
function loginAsAdmin() {
  const loginPage = http.get(`${BASE_URL}/auth/login`);
  const csrfMatch = loginPage.body.match(
    /name="_csrf"[^>]*value="([^"]+)"/
  );
  const csrf = csrfMatch ? csrfMatch[1] : "";

  const res = http.post(
    `${BASE_URL}/auth/login`,
    {
      username: "admin@appvault.com",
      password: "Admin123!",
      _csrf: csrf,
    },
    {
      redirects: 5,
    }
  );

  check(res, {
    "admin login: status 200": (r) => r.status === 200,
  }) || errorRate.add(1);

  return res;
}

export default function () {
  group("Admin Login", () => {
    loginAsAdmin();
    sleep(1);
  });

  group("Downloads Stats", () => {
    const res = http.get(`${BASE_URL}/admin/api/stats/downloads`);
    downloadsDuration.add(res.timings.duration);
    check(res, {
      "downloads: status 200": (r) => r.status === 200,
      "downloads: is JSON array": (r) => {
        try {
          const body = JSON.parse(r.body);
          return Array.isArray(body) && body.length > 0;
        } catch (e) {
          return false;
        }
      },
    }) || errorRate.add(1);
    sleep(0.5);
  });

  group("Ratings Stats", () => {
    const res = http.get(`${BASE_URL}/admin/api/stats/ratings`);
    ratingsDuration.add(res.timings.duration);
    check(res, {
      "ratings: status 200": (r) => r.status === 200,
      "ratings: is JSON array": (r) => {
        try {
          const body = JSON.parse(r.body);
          return Array.isArray(body) && body.length > 0;
        } catch (e) {
          return false;
        }
      },
    }) || errorRate.add(1);
    sleep(0.5);
  });

  group("Categories Stats", () => {
    const res = http.get(`${BASE_URL}/admin/api/stats/categories`);
    categoriesDuration.add(res.timings.duration);
    check(res, {
      "categories: status 200": (r) => r.status === 200,
      "categories: is JSON array": (r) => {
        try {
          const body = JSON.parse(r.body);
          return Array.isArray(body) && body.length > 0;
        } catch (e) {
          return false;
        }
      },
    }) || errorRate.add(1);
    sleep(0.5);
  });
}
