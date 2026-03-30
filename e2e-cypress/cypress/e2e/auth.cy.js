describe("Authentication", () => {
  it("shows login page", () => {
    cy.visit("/auth/login");
    cy.get('input[name="username"]').should("exist");
    cy.get('input[name="password"]').should("exist");
  });

  it("shows register page", () => {
    cy.visit("/auth/register");
    cy.get('input[name="firstName"]').should("exist");
    cy.get('input[name="email"]').should("exist");
    cy.get('input[name="password"]').should("exist");
  });

  it("logs in with valid admin credentials", () => {
    cy.loginAsAdmin();
    cy.visit("/user/profile");
    cy.get("body").should("contain", "Admin");
  });

  it("rejects invalid login credentials", () => {
    cy.visit("/auth/login");
    cy.get('input[name="username"]').type("bad@email.com");
    cy.get('input[name="password"]').type("wrongpassword");
    cy.get("form").submit();
    cy.url().should("include", "error");
  });

  it("registers a new user", () => {
    const uniqueEmail = `testuser_${Date.now()}@example.com`;
    cy.visit("/auth/register");
    cy.get('input[name="firstName"]').type("Test");
    cy.get('input[name="lastName"]').type("User");
    cy.get('input[name="email"]').type(uniqueEmail);
    cy.get('input[name="password"]').type("Password1!");
    cy.get('input[name="confirmPassword"]').type("Password1!");
    cy.get("form").submit();
    cy.url().should("include", "registered");
  });

  it("rejects registration with mismatched passwords", () => {
    cy.visit("/auth/register");
    cy.get('input[name="firstName"]').type("Test");
    cy.get('input[name="lastName"]').type("User");
    cy.get('input[name="email"]').type("mismatch@example.com");
    cy.get('input[name="password"]').type("Password1!");
    cy.get('input[name="confirmPassword"]').type("Different1!");
    cy.get("form").submit();
    cy.url().should("include", "/auth/register");
  });

  it("redirects unauthenticated user from profile to login", () => {
    cy.visit("/user/profile");
    cy.url().should("include", "/auth/login");
  });
});
