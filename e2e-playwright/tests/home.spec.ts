import { test, expect } from "@playwright/test";

test.describe("Home Page", () => {
  test("loads the home page successfully", async ({ page }) => {
    await page.goto("/");
    await expect(page).toHaveTitle(/AppVault/i);
    await expect(page.locator("body")).toContainText("Top Free Apps");
  });

  test("displays app categories", async ({ page }) => {
    await page.goto("/");
    await expect(page.locator("body")).toContainText("Productivity");
    await expect(page.locator("body")).toContainText("Games");
  });

  test("has navigation to browse and login", async ({ page }) => {
    await page.goto("/");
    await expect(page.locator('.nav-link', { hasText: 'Browse' })).toBeVisible();
    await expect(page.locator('.nav-link', { hasText: 'Sign In' })).toBeVisible();
  });
});
