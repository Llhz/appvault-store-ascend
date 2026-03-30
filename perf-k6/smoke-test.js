import http from "k6/http";
import { check, sleep } from "k6";
import { Rate } from "k6/metrics";

const errorRate = new Rate("errors");
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  stages: [
    { duration: "10s", target: 10 }, // ramp up
    { duration: "30s", target: 10 }, // hold steady
    { duration: "10s", target: 0 }, // ramp down
  ],
  thresholds: {
    http_req_duration: ["p(95)<1500"],
    http_req_failed: ["rate<0.01"],
    errors: ["rate<0.01"],
  },
};

export default function () {
  // Quick smoke test of critical endpoints
  const checks = [
    { url: "/", name: "home" },
    { url: "/browse", name: "browse" },
    { url: "/app/1", name: "app-detail" },
    { url: "/search?q=Focus", name: "search" },
    { url: "/auth/login", name: "login-page" },
    { url: "/auth/register", name: "register-page" },
  ];

  for (const c of checks) {
    const res = http.get(`${BASE_URL}${c.url}`);
    check(res, {
      [`${c.name}: status 200`]: (r) => r.status === 200,
      [`${c.name}: body not empty`]: (r) => r.body.length > 0,
    }) || errorRate.add(1);
  }

  sleep(1);
}
