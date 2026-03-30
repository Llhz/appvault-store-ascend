import { test, expect } from "@playwright/test";

test.describe("Search Auto-Suggest", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/");
  });

  test("suggestions appear on typing", async ({ page }) => {
    await page.locator("#searchInput").fill("Foc");
    await expect(page.locator("#searchSuggest.active")).toBeVisible();
  });

  test("shows matching apps", async ({ page }) => {
    await page.locator("#searchInput").fill("Focus");
    await expect(page.locator("#searchSuggest.active")).toBeVisible();
    await expect(
      page.locator(".search-suggest-item .suggest-name").first()
    ).toContainText("Focus");
  });

  test("clicking suggestion navigates to app", async ({ page }) => {
    await page.locator("#searchInput").fill("Focus");
    await expect(page.locator("#searchSuggest.active")).toBeVisible();
    await page.locator(".search-suggest-item").first().click();
    await expect(page).toHaveURL(/\/app\//);
  });

  test("suggestions dismiss on outside click", async ({ page }) => {
    await page.locator("#searchInput").fill("Focus");
    await expect(page.locator("#searchSuggest.active")).toBeVisible();
    await page.locator(".navbar-brand").click();
    await expect(page.locator("#searchSuggest.active")).not.toBeVisible();
  });

  test("enter key submits full search", async ({ page }) => {
    await page.locator("#searchInput").fill("Focus");
    await page.locator("#searchInput").press("Enter");
    await expect(page).toHaveURL(/\/search/);
  });
});
