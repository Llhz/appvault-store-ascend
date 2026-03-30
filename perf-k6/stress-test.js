import http from "k6/http";
import { check, sleep } from "k6";
import { Rate } from "k6/metrics";

const errorRate = new Rate("errors");
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  stages: [
    { duration: "1m", target: 50 }, // ramp up to 50 users
    { duration: "2m", target: 50 }, // hold at 50 users
    { duration: "1m", target: 100 }, // ramp to 100
    { duration: "2m", target: 100 }, // hold at 100
    { duration: "1m", target: 0 }, // ramp down
  ],
  thresholds: {
    http_req_duration: ["p(95)<3000"], // 95th percentile < 3s
    http_req_failed: ["rate<0.10"], // error rate < 10%
    errors: ["rate<0.10"],
  },
};

export default function () {
  // Simulate a realistic user journey
  const pages = [
    { url: "/", name: "home" },
    { url: "/browse", name: "browse" },
    { url: `/app/${Math.floor(Math.random() * 20) + 1}`, name: "detail" },
    { url: `/search?q=${["Focus", "Code", "Music"][Math.floor(Math.random() * 3)]}`, name: "search" },
    { url: "/browse?page=0&size=12", name: "browse-paginated" },
  ];

  const page = pages[Math.floor(Math.random() * pages.length)];
  const res = http.get(`${BASE_URL}${page.url}`);

  check(res, {
    [`${page.name}: status OK`]: (r) => r.status === 200,
    [`${page.name}: response time < 2s`]: (r) => r.timings.duration < 2000,
  }) || errorRate.add(1);

  sleep(Math.random() * 2 + 0.5); // 0.5-2.5s think time
}
