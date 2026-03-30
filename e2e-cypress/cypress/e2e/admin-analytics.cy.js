describe("Admin Analytics Dashboard", () => {
  beforeEach(() => {
    cy.loginAsAdmin();
    cy.visit("/admin/dashboard");
  });

  it("dashboard loads charts", () => {
    cy.get("canvas").should("have.length.at.least", 1);
  });

  it("downloads chart is rendered", () => {
    cy.get("#downloadsChart").should("be.visible");
  });

  it("stats cards show numbers", () => {
    cy.get(".stat-value").each(($el) => {
      cy.wrap($el).invoke("text").should("match", /\d+/);
    });
  });

  it("API endpoint returns JSON", () => {
    cy.request("/admin/api/stats/downloads").then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.be.an("array").and.not.be.empty;
    });
  });
});
