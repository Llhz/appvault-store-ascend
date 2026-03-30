import { test, expect } from "@playwright/test";

test.describe("Admin Analytics Dashboard", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/auth/login");
    await page.fill('input[name="username"]', "admin@appvault.com");
    await page.fill('input[name="password"]', "Admin123!");
    await page.click('button[type="submit"]');
    await page.waitForURL("**/");
    await page.goto("/admin/dashboard");
    await page.waitForLoadState("networkidle");
  });

  test("dashboard loads charts", async ({ page }) => {
    const canvases = page.locator("canvas");
    await expect(canvases.first()).toBeVisible();
    expect(await canvases.count()).toBeGreaterThanOrEqual(1);
  });

  test("downloads chart is rendered", async ({ page }) => {
    const chart = page.locator("#downloadsChart");
    await expect(chart).toBeVisible();
    const height = await chart.evaluate((el: HTMLCanvasElement) => el.height);
    expect(height).toBeGreaterThan(0);
  });

  test("stats cards show numbers", async ({ page }) => {
    const statValues = page.locator(".stat-value");
    const count = await statValues.count();
    expect(count).toBeGreaterThanOrEqual(1);
    for (let i = 0; i < count; i++) {
      const text = await statValues.nth(i).textContent();
      expect(text).toMatch(/\d+/);
    }
  });

  test("API returns JSON", async ({ request }) => {
    // Login via API to get an authenticated session
    const loginPage = await request.get("/auth/login");
    const html = await loginPage.text();
    const csrfMatch = html.match(/name="_csrf"[^>]*value="([^"]+)"/);
    const csrf = csrfMatch ? csrfMatch[1] : "";

    await request.post("/auth/login", {
      form: {
        username: "admin@appvault.com",
        password: "Admin123!",
        _csrf: csrf,
      },
    });

    const response = await request.get("/admin/api/stats/downloads");
    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(Array.isArray(body)).toBe(true);
    expect(body.length).toBeGreaterThan(0);
  });
});
