import { test, expect } from "@playwright/test";

test.describe("App Of The Day", () => {
  test("App Of The Day card is visible", async ({ page }) => {
    await page.goto("/");
    await expect(page.locator(".aotd-slide.active .app-of-the-day")).toBeVisible();
  });

  test("Card shows app name", async ({ page }) => {
    await page.goto("/");
    const card = page.locator(".aotd-slide.active .app-of-the-day");
    const text = await card.textContent();
    expect(text?.trim()).not.toBe("");
  });

  test("Card links to app detail", async ({ page }) => {
    await page.goto("/");
    await page.locator(".aotd-slide.active .app-of-the-day").click();
    await expect(page).toHaveURL(/\/app\//);
  });

  test("Card has background image", async ({ page }) => {
    await page.goto("/");
    const bgImage = await page.locator(".aotd-slide.active .app-of-the-day").evaluate(
      (el) => getComputedStyle(el).backgroundImage
    );
    expect(bgImage).not.toBe("none");
    expect(bgImage).not.toBe("");
  });
});
