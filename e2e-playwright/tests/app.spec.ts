import { test, expect, Page } from "@playwright/test";

async function login(page: Page, email: string, password: string) {
  await page.goto("/auth/login");
  await page.fill('input[name="username"]', email);
  await page.fill('input[name="password"]', password);
  await page.locator("form").first().evaluate((form: HTMLFormElement) =>
    form.submit()
  );
  await page.waitForURL((url) => !url.pathname.includes("/auth/login"));
}

async function loginAsAdmin(page: Page) {
  await login(page, "admin@appvault.com", "Admin123!");
}

async function loginAsUser(page: Page) {
  await login(page, "user@appvault.com", "User123!");
}

test.describe("Authentication", () => {
  test("login page renders", async ({ page }) => {
    await page.goto("/auth/login");
    await expect(page.locator('input[name="username"]')).toBeVisible();
    await expect(page.locator('input[name="password"]')).toBeVisible();
  });

  test("register page renders", async ({ page }) => {
    await page.goto("/auth/register");
    await expect(page.locator('input[name="firstName"]')).toBeVisible();
    await expect(page.locator('input[name="email"]')).toBeVisible();
  });

  test("successful admin login", async ({ page }) => {
    await loginAsAdmin(page);
    await page.goto("/user/profile");
    await expect(page.locator("body")).toContainText("Admin");
  });

  test("failed login shows error", async ({ page }) => {
    await page.goto("/auth/login");
    await page.fill('input[name="username"]', "bad@email.com");
    await page.fill('input[name="password"]', "wrongpassword");
    await page.locator("form").first().evaluate((form: HTMLFormElement) =>
      form.submit()
    );
    await expect(page).toHaveURL(/error/);
  });

  test("register new user", async ({ page }) => {
    const uniqueEmail = `testuser_${Date.now()}@example.com`;
    await page.goto("/auth/register");
    await page.fill('input[name="firstName"]', "Test");
    await page.fill('input[name="lastName"]', "User");
    await page.fill('input[name="email"]', uniqueEmail);
    await page.fill('input[name="password"]', "Password1!");
    await page.fill('input[name="confirmPassword"]', "Password1!");
    await page.locator("form").first().evaluate((form: HTMLFormElement) =>
      form.submit()
    );
    await expect(page).toHaveURL(/registered/);
  });

  test("unauthenticated user redirected to login", async ({ page }) => {
    await page.goto("/user/profile");
    await expect(page).toHaveURL(/auth\/login/);
  });
});

test.describe("User Features (authenticated)", () => {
  test.beforeEach(async ({ page }) => {
    await loginAsUser(page);
  });

  test("profile page loads", async ({ page }) => {
    await page.goto("/user/profile");
    await expect(page.locator('input[name="firstName"]')).toBeVisible();
  });

  test("update profile", async ({ page }) => {
    await page.goto("/user/profile");
    await page.fill('input[name="firstName"]', "Updated");
    await page.fill('input[name="lastName"]', "Name");
    await page.locator('input[name="firstName"]').evaluate((el: HTMLInputElement) =>
      el.closest("form")!.submit()
    );
    await expect(page).toHaveURL(/saved/);
  });

  test("my reviews page loads", async ({ page }) => {
    await page.goto("/user/my-reviews");
    await expect(page.locator("body")).toBeVisible();
  });
});

test.describe("Admin Features (admin-only)", () => {
  test.beforeEach(async ({ page }) => {
    await loginAsAdmin(page);
  });

  test("admin dashboard loads", async ({ page }) => {
    await page.goto("/admin/dashboard");
    await expect(page.locator("body")).toContainText("Dashboard");
  });

  test("manage apps page shows apps", async ({ page }) => {
    await page.goto("/admin/apps");
    await expect(page.locator("body")).toContainText("FocusFlow");
  });

  test("new app form loads", async ({ page }) => {
    await page.goto("/admin/apps/new");
    await expect(page.locator('input[name="name"]')).toBeVisible();
  });

  test("edit app form loads with data", async ({ page }) => {
    await page.goto("/admin/apps/1/edit");
    const nameValue = await page.locator('input[name="name"]').inputValue();
    expect(nameValue.length).toBeGreaterThan(0);
  });

  test("manage users page loads", async ({ page }) => {
    await page.goto("/admin/users");
    await expect(page.locator("body")).toContainText("admin@appvault.com");
  });

  test("regular user gets 403 on admin pages", async ({ page }) => {
    // Logout and login as regular user
    await page.goto("/auth/logout");
    await loginAsUser(page);
    const response = await page.goto("/admin/dashboard");
    expect(response?.status()).toBe(403);
  });
});
