import { test, expect } from "@playwright/test";

test.describe("Notifications - Logged In", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/auth/login");
    await page.fill('input[name="username"]', "user@appvault.com");
    await page.fill('input[name="password"]', "User123!");
    await page.click('button[type="submit"]');
    await page.waitForURL("**/");
  });

  test("notification bell is visible when logged in", async ({ page }) => {
    const bell = page.locator("#notificationBell");
    await expect(bell).toBeVisible();
  });

  test("notification dropdown opens on click", async ({ page }) => {
    await page.click("#notifDropdownToggle");
    const dropdown = page.locator("#notifDropdown.show");
    await expect(dropdown).toBeVisible();
  });

  test("shows notification items or empty state", async ({ page }) => {
    await page.click("#notifDropdownToggle");
    await expect(page.locator("#notifDropdown.show")).toBeVisible();

    // Wait for AJAX to load notification content
    await page.waitForTimeout(1000);

    const items = page.locator("#notifDropdown .notif-item");
    const hasItems = (await items.count()) > 0;
    const hasEmpty = await page.locator("#notifList").textContent()
      .then(text => text !== null && text.includes("No notifications"));
    expect(hasItems || hasEmpty).toBe(true);
  });
});

test.describe("Notifications - Anonymous", () => {
  test("bell is hidden when not logged in", async ({ page }) => {
    await page.goto("/");
    await expect(page.locator("#notificationBell")).toHaveCount(0);
  });
});
