describe("Dark Mode Toggle", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Toggle activates dark mode", () => {
    cy.get("#darkModeToggle").click();
    cy.get("html").should("have.attr", "data-theme", "dark");
  });

  it("Persists across pages", () => {
    cy.get("#darkModeToggle").click();
    cy.get("html").should("have.attr", "data-theme", "dark");
    cy.visit("/browse");
    cy.get("html").should("have.attr", "data-theme", "dark");
  });

  it("Persists after reload", () => {
    cy.get("#darkModeToggle").click();
    cy.get("html").should("have.attr", "data-theme", "dark");
    cy.reload();
    cy.get("html").should("have.attr", "data-theme", "dark");
  });

  it("Can be deactivated", () => {
    cy.get("#darkModeToggle").click();
    cy.get("html").should("have.attr", "data-theme", "dark");
    cy.get("#darkModeToggle").click();
    cy.get("html").should("not.have.attr", "data-theme", "dark");
  });
});
