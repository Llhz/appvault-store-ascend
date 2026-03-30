import { test, expect } from "@playwright/test";

test.describe("Browse & Search", () => {
  test("browse page shows apps", async ({ page }) => {
    await page.goto("/browse");
    await expect(page.locator(".card, [class*=app]").first()).toBeVisible();
  });

  test("category filter works", async ({ page }) => {
    await page.goto("/browse");
    await page.locator(".category-pill", { hasText: "Productivity" }).click();
    await expect(page).toHaveURL(/category/);
  });

  test("search returns results for known app", async ({ page }) => {
    await page.goto("/search?q=Focus");
    await expect(page.locator("body")).toContainText("Focus");
  });

  test("search handles no results gracefully", async ({ page }) => {
    await page.goto("/search?q=xyznonexistent123");
    await expect(page.locator("body")).toBeVisible(); // no crash
  });

  test("app detail page loads", async ({ page }) => {
    await page.goto("/app/1");
    await expect(page.locator("h1, h2, h3").first()).toBeVisible();
    await expect(page.locator("body")).toContainText("Reviews");
  });
});
