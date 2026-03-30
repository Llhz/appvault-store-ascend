describe("Home Page", () => {
  it("loads the home page with featured apps", () => {
    cy.visit("/");
    cy.get("h1, h2").should("exist");
    cy.contains("Top Free Apps").should("be.visible");
  });

  it("displays app categories", () => {
    cy.visit("/");
    cy.contains("Productivity").should("exist");
    cy.contains("Games").should("exist");
  });

  it("has navigation links", () => {
    cy.visit("/");
    cy.get('a[href="/browse"]').should("exist");
    cy.get('a[href="/auth/login"]').should("exist");
  });
});
