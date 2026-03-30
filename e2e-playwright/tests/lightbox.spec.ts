import { test, expect } from "@playwright/test";

test.describe("Screenshot Lightbox Gallery", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/app/1");
  });

  test("Screenshot thumbnails are visible", async ({ page }) => {
    const thumbnails = page.locator(".screenshot-img");
    await expect(thumbnails.first()).toBeVisible();
    expect(await thumbnails.count()).toBeGreaterThan(0);
  });

  test("Lightbox opens on click", async ({ page }) => {
    await page.locator(".screenshot-img").first().click();
    await expect(page.locator("#screenshotLightbox.active")).toBeVisible();
    await expect(page.locator(".lightbox-image")).toBeVisible();
  });

  test("Lightbox can be closed", async ({ page }) => {
    await page.locator(".screenshot-img").first().click();
    await expect(page.locator("#screenshotLightbox.active")).toBeVisible();
    await page.locator(".lightbox-close").click();
    await expect(page.locator("#screenshotLightbox.active")).not.toBeVisible();
  });

  test("Lightbox navigation works", async ({ page }) => {
    await page.locator(".screenshot-img").first().click();
    await expect(page.locator("#screenshotLightbox.active")).toBeVisible();
    const firstSrc = await page.locator(".lightbox-image").getAttribute("src");
    await page.locator(".lightbox-next").click();
    const secondSrc = await page.locator(".lightbox-image").getAttribute("src");
    expect(secondSrc).not.toBe(firstSrc);
  });
});
