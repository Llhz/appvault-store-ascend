describe("Admin Features", () => {
  beforeEach(() => {
    cy.loginAsAdmin();
  });

  it("accesses admin dashboard", () => {
    cy.visit("/admin/dashboard");
    cy.contains("Dashboard").should("exist");
  });

  it("views manage apps page", () => {
    cy.visit("/admin/apps");
    cy.get("body").should("contain", "FocusFlow");
  });

  it("opens new app form", () => {
    cy.visit("/admin/apps/new");
    cy.get('input[name="name"]').should("exist");
    cy.get('textarea[name="description"]').should("exist");
  });

  it("creates a new app", () => {
    cy.visit("/admin/apps/new");
    cy.get('input[name="name"]').type("E2E Test App");
    cy.get('textarea[name="description"]').type("An app created during Cypress E2E testing");
    cy.get('input[name="developer"]').type("E2E Dev");
    cy.get('input[name="version"]').type("1.0");
    cy.get('input[name="size"]').type("5 MB");
    cy.get('input[name="name"]').closest("form").submit();
    cy.url().should("include", "/admin/apps");
  });

  it("edits an existing app", () => {
    cy.visit("/admin/apps/1/edit");
    cy.get('input[name="name"]').invoke("val").should("not.be.empty");
    cy.get('input[name="name"]').clear().type("FocusFlow Edited");
    cy.get('input[name="name"]').closest("form").submit();
    cy.url().should("include", "/admin/apps");
  });

  it("views manage users page", () => {
    cy.visit("/admin/users");
    cy.get("body").should("contain", "admin@appvault.com");
  });

  it("prevents regular user from accessing admin", () => {
    // Log out by clearing cookies and login as regular user
    cy.clearCookies();
    cy.loginAsUser();
    cy.request({ url: "/admin/dashboard", failOnStatusCode: false }).then(
      (response) => {
        expect(response.status).to.eq(403);
      }
    );
  });
});
