import { test, expect } from "@playwright/test";

test.describe("Dark Mode", () => {
  test("Toggle activates dark mode", async ({ page }) => {
    await page.goto("/");
    await page.locator("#darkModeToggle").click();
    await expect(page.locator("html")).toHaveAttribute("data-theme", "dark");
  });

  test("Persists across pages", async ({ page }) => {
    await page.goto("/");
    await page.locator("#darkModeToggle").click();
    await expect(page.locator("html")).toHaveAttribute("data-theme", "dark");
    await page.goto("/browse");
    await expect(page.locator("html")).toHaveAttribute("data-theme", "dark");
  });

  test("Persists after reload", async ({ page }) => {
    await page.goto("/");
    await page.locator("#darkModeToggle").click();
    await expect(page.locator("html")).toHaveAttribute("data-theme", "dark");
    await page.reload();
    await expect(page.locator("html")).toHaveAttribute("data-theme", "dark");
  });

  test("Can be deactivated", async ({ page }) => {
    await page.goto("/");
    await page.locator("#darkModeToggle").click();
    await expect(page.locator("html")).toHaveAttribute("data-theme", "dark");
    await page.locator("#darkModeToggle").click();
    await expect(page.locator("html")).not.toHaveAttribute("data-theme", "dark");
  });
});
