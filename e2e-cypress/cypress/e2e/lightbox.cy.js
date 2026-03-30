describe("Screenshot Lightbox Gallery", () => {
  beforeEach(() => {
    cy.visit("/app/1");
  });

  it("Screenshot thumbnails are visible", () => {
    cy.get(".screenshot-img").should("have.length.greaterThan", 0).first().should("be.visible");
  });

  it("Lightbox opens on click", () => {
    cy.get(".screenshot-img").first().click();
    cy.get("#screenshotLightbox.active").should("be.visible");
  });

  it("Lightbox can be closed", () => {
    cy.get(".screenshot-img").first().click();
    cy.get("#screenshotLightbox.active").should("be.visible");
    cy.get(".lightbox-close").click();
    cy.get("#screenshotLightbox.active").should("not.exist");
  });

  it("Lightbox navigation works", () => {
    cy.get(".screenshot-img").first().click();
    cy.get("#screenshotLightbox.active").should("be.visible");
    cy.get(".lightbox-image")
      .invoke("attr", "src")
      .then((firstSrc) => {
        cy.get(".lightbox-next").click();
        cy.get(".lightbox-image").invoke("attr", "src").should("not.eq", firstSrc);
      });
  });
});
