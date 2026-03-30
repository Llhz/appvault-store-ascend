describe("App of the Day", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("App Of The Day card is visible", () => {
    cy.get(".app-of-the-day").should("exist").and("be.visible");
  });

  it("Card shows app name", () => {
    cy.get(".app-of-the-day").invoke("text").then((text) => {
      expect(text.trim()).to.not.be.empty;
    });
  });

  it("Card links to app detail", () => {
    cy.get(".app-of-the-day").first().click();
    cy.url().should("include", "/app/");
  });

  it("Card has background image", () => {
    cy.get(".app-of-the-day")
      .should("have.css", "background-image")
      .and("not.eq", "none");
  });
});
