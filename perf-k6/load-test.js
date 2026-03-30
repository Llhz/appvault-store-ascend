import http from "k6/http";
import { check, group, sleep } from "k6";
import { Rate, Trend } from "k6/metrics";

// Custom metrics
const errorRate = new Rate("errors");
const homePageDuration = new Trend("home_page_duration");
const browsePageDuration = new Trend("browse_page_duration");
const appDetailDuration = new Trend("app_detail_duration");
const searchDuration = new Trend("search_duration");

// Test configuration
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  stages: [
    { duration: "30s", target: 10 }, // ramp up to 10 users
    { duration: "1m", target: 10 }, // hold at 10 users
    { duration: "30s", target: 30 }, // ramp up to 30 users
    { duration: "1m", target: 30 }, // hold at 30 users
    { duration: "30s", target: 0 }, // ramp down
  ],
  thresholds: {
    http_req_duration: ["p(95)<2000"], // 95% of requests < 2s
    http_req_failed: ["rate<0.05"], // error rate < 5%
    errors: ["rate<0.05"],
  },
};

export default function () {
  group("Public Pages", () => {
    // Home page
    let res = http.get(`${BASE_URL}/`);
    homePageDuration.add(res.timings.duration);
    check(res, {
      "home: status 200": (r) => r.status === 200,
      "home: has content": (r) => r.body.includes("AppVault"),
    }) || errorRate.add(1);
    sleep(1);

    // Browse page
    res = http.get(`${BASE_URL}/browse`);
    browsePageDuration.add(res.timings.duration);
    check(res, {
      "browse: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);

    // Browse with pagination
    res = http.get(`${BASE_URL}/browse?page=1&size=10`);
    check(res, {
      "browse page 2: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(0.5);

    // App detail page (random app ID 1-20)
    const appId = Math.floor(Math.random() * 20) + 1;
    res = http.get(`${BASE_URL}/app/${appId}`);
    appDetailDuration.add(res.timings.duration);
    check(res, {
      "detail: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);

    // Search
    const queries = ["Focus", "Code", "Music", "Photo", "Game", "Weather"];
    const query = queries[Math.floor(Math.random() * queries.length)];
    res = http.get(`${BASE_URL}/search?q=${query}`);
    searchDuration.add(res.timings.duration);
    check(res, {
      "search: status 200": (r) => r.status === 200,
    }) || errorRate.add(1);
    sleep(1);

    // Category browse
    const categoryId = Math.floor(Math.random() * 10) + 1;
    res = http.get(`${BASE_URL}/browse/category/${categoryId}`);
    check(res, {
      "category: status 200 or 404": (r) =>
        r.status === 200 || r.status === 404,
    }) || errorRate.add(1);
    sleep(0.5);
  });
}
