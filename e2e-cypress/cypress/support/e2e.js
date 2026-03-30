// Support file loaded before every spec
import "./commands";

// Capture a screenshot after every test (pass or fail)
afterEach(function () {
  cy.screenshot(`${this.currentTest.fullTitle()} -- final`, { capture: "viewport" });
});
