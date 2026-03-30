describe("Notification System", () => {
  describe("when logged in", () => {
    beforeEach(() => {
      cy.loginAsUser();
      cy.visit("/");
    });

    it("bell icon is visible in navbar", () => {
      cy.get("#notificationBell").should("be.visible");
    });

    it("dropdown opens on click", () => {
      cy.get("#notifDropdownToggle").click();
      cy.get("#notifDropdown").should("be.visible");
    });

    it("shows notification items or empty state", () => {
      cy.get("#notifDropdownToggle").click();
      cy.get("#notifDropdown").should("be.visible");
      // AJAX replaces #notifList innerHTML — wait for content to load
      cy.wait(1000);
      cy.get("#notifDropdown").then(($dropdown) => {
        if ($dropdown.find(".notif-item").length > 0) {
          cy.get("#notifDropdown .notif-item").should("have.length.greaterThan", 0);
        } else {
          cy.get("#notifDropdown").should("contain.text", "No notifications");
        }
      });
    });
  });

  describe("when anonymous", () => {
    it("bell icon is not visible", () => {
      cy.visit("/");
      cy.get("#notificationBell").should("not.exist");
    });
  });
});
