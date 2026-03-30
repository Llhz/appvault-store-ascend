describe("Search Auto-Suggest", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("suggestions appear on typing", () => {
    cy.get("#searchInput").type("Foc");
    cy.get("#searchSuggest.active", { timeout: 5000 }).should("be.visible");
    cy.get(".search-suggest-item").should("have.length.greaterThan", 0);
  });

  it("shows matching apps", () => {
    cy.get("#searchInput").type("Focus");
    cy.get("#searchSuggest.active", { timeout: 5000 }).should("be.visible");
    cy.get(".suggest-name").first().should("contain.text", "Focus");
  });

  it("clicking suggestion navigates to app", () => {
    cy.get("#searchInput").type("Focus");
    cy.get("#searchSuggest.active", { timeout: 5000 }).should("be.visible");
    cy.get(".search-suggest-item").first().click();
    cy.url().should("include", "/app/");
  });

  it("dismisses on outside click", () => {
    cy.get("#searchInput").type("Focus");
    cy.get("#searchSuggest.active", { timeout: 5000 }).should("be.visible");
    cy.get("body").click(0, 0);
    cy.get("#searchSuggest.active").should("not.exist");
  });

  it("Enter submits full search", () => {
    cy.get("#searchInput").type("Focus{enter}");
    cy.url().should("include", "/search");
  });
});
