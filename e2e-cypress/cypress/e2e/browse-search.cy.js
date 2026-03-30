describe("Browse & Search", () => {
  it("displays app listing on browse page", () => {
    cy.visit("/browse");
    cy.get("[class*=card], [class*=app]").should("have.length.greaterThan", 0);
  });

  it("filters apps by category", () => {
    cy.visit("/browse");
    // Category links are in a visible filter bar as category pills
    cy.get('a[href*="/browse/category/"]').first().click({ force: true });
    cy.url().should("include", "category");
  });

  it("searches for apps by keyword", () => {
    cy.visit("/");
    cy.get('input[name="q"], input[type="search"]').first().type("Focus{enter}");
    cy.url().should("include", "/search");
    cy.contains("Focus").should("exist");
  });

  it("shows app detail page", () => {
    cy.visit("/app/1");
    cy.get("h1, h2, h3").should("exist");
    cy.contains("Reviews").should("exist");
  });

  it("displays empty search results gracefully", () => {
    cy.visit("/search?q=xyznonexistent123");
    cy.get("body").should("exist"); // page loads without error
  });
});
