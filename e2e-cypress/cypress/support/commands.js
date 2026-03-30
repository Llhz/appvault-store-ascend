// ***********************************************
// Custom Cypress commands for AppVault Store
// ***********************************************

Cypress.Commands.add("login", (email, password) => {
  cy.visit("/auth/login");
  cy.get('input[name="username"]').type(email);
  cy.get('input[name="password"]').type(password);
  cy.get('form').submit();
  cy.url().should("not.include", "/auth/login");
});

Cypress.Commands.add("loginAsAdmin", () => {
  cy.login("admin@appvault.com", "Admin123!");
});

Cypress.Commands.add("loginAsUser", () => {
  cy.login("user@appvault.com", "User123!");
});

Cypress.Commands.add("logout", () => {
  cy.clearCookies();
});
