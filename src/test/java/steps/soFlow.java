package steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Locator;

import io.cucumber.java.en.*;
import utils.DriverFactory;
import utils.ExcelReader;

import java.util.Map;

public class soFlow {

    @Given("so to ship")
    public void so_to_ship() throws InterruptedException {

        // Always get the active page
        Page page = DriverFactory.getPage();
        if (page == null) {
            throw new RuntimeException("❌ Page not initialized! Login must run first.");
        }

        // If you need Excel data
        Map<String, String> data = ExcelReader.getKeyValueData(
                "src/test/resources/testdata/SF_TestData.xlsx",
                "soFlow"
        );

        Thread.sleep(7000);
        System.out.println("now click app launcher");
        page.locator("//button[@title='App Launcher']").click();

        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search apps and items...")).fill("proposals");
        Thread.sleep(4000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Proposals")).click();
        page.waitForTimeout(9000);
        Thread.sleep(4000);

        page.waitForTimeout(9000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();

        Thread.sleep(3000);
       page.locator("//label[text()='Proposal']/following::input[1]").fill(data.get("Proposal"));
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Status")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(data.get("Status"))).locator("span").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Inventory Account")).click();

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Inventory Account")).pressSequentially(data.get("Inventory Account"));
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(3).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Proposal Type")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(data.get("Proposal Type"))).locator("span").nth(1).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Site")).click();

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Site")).pressSequentially(data.get("Site"));
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(3).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Price Book")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Price Book")).pressSequentially(data.get("Price Book"));
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Payment Terms")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(data.get("Payment Terms"))).locator("span").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Estimated Amount")).click();
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Estimated Amount")).fill(data.get("Estimated Amount"));
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Estimated Amount")).press("Enter");
        Thread.sleep(3000);
        page.locator("records-record-layout-row").filter(new Locator.FilterOptions().setHasText("*Estimated Amount")).locator("records-record-layout-item").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Account")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Account")).pressSequentially(data.get("Bill to Account"));
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(3).click();

        Thread.sleep(3000);

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Contact")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Contact")).pressSequentially(data.get("Bill to Contact"));
        Thread.sleep(5000);
        page.getByTitle(data.get("Bill to Contact"), new Page.GetByTitleOptions().setExact(true)).nth(0).click();
        Thread.sleep(3000);

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Account")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Account")).pressSequentially(data.get("Ship to Account"));
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(2).click();
        Thread.sleep(3000);

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Contact")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Contact")).pressSequentially(data.get("Ship to Contact"));
        Thread.sleep(3000);
        page.getByTitle(data.get("Ship to Contact"), new Page.GetByTitleOptions().setExact(true)).click();
        Thread.sleep(3000);

        // Save Proposal
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();

        page.waitForTimeout(9000);

        Thread.sleep(3000);

        System.out.println("Proposal created successfully");

        // Wait for the record to settle, then reload before continuing
        page.waitForTimeout(5000);
        page.reload();
        page.waitForLoadState();
        page.waitForTimeout(5000);

        // ------- ADD PRODUCTS -------
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Products")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Browse Catalog")).click();
        Thread.sleep(3000);
        page.getByPlaceholder("Search by product name, SKU or description...").click();
        page.getByPlaceholder("Search by product name, SKU or description...").pressSequentially(data.get("Product"));
        Thread.sleep(3000);
        page.locator("div.pe-prod-item").filter(new Locator.FilterOptions().setHasText(data.get("Product"))).locator("input[type='checkbox']").click();
        Thread.sleep(2000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("to Proposal")).click();
        Thread.sleep(2000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back")).click();
        Thread.sleep(5000);

        System.out.println("success created add products");

        // Wait for the record to settle, then reload before continuing
        page.waitForTimeout(5000);
        page.reload();
        page.waitForLoadState();
        page.waitForTimeout(5000);

        // click create quote button
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Quote")).click();
        page.waitForLoadState();
        Thread.sleep(8000);

        // Bill to Location
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Location")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Location")).pressSequentially(data.get("Bill to Location"));
        Thread.sleep(2000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(data.get("Bill to Location")).setExact(true)).click();
        Thread.sleep(1000);

        // Ship to Location
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Location")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Location")).pressSequentially(data.get("Ship to Location"));
        Thread.sleep(2000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(data.get("Ship to Location")).setExact(true)).click();
        Thread.sleep(1000);

        // Request Date (both calendar icon buttons share the identical accessible
        // name "Select a date for", so they are distinguished by DOM order)
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select a date for")).first().click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Today")).click();
        Thread.sleep(1000);

        // Planned Ship Date
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select a date for")).nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Today")).click();
        Thread.sleep(1000);

        // select proposed product lines and submit the quote
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select All Visible")).click();
        Thread.sleep(2000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Create Quote")).first().click();
        Thread.sleep(4000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back")).first().click();
        Thread.sleep(3000);

        System.out.println("success created quote");

        // open the newly created Customer Quote
        page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Orders")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("CQ-")).first().click();
        Thread.sleep(3000);

        // ============================================================
        // ------- SO FLOW: create Sales Order from the Customer Quote -------
        // ============================================================
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create SO")).click();
      page.waitForLoadState();
      Thread.sleep(8000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select All Visible")).click();
      Thread.sleep(2000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Create Sales Order")).first().click();
      Thread.sleep(4000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back")).first().click();
      Thread.sleep(4000);
      page.locator("(//div[@class='slds-scrollable_y'])[2]/table/tbody/tr/th/lightning-primitive-cell-factory/span").click();
      Thread.sleep(4000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit").setExact(true)).click();
      Thread.sleep(4000);
      page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Status")).click();
      page.getByText("Approved").click();
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
      Thread.sleep(4000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Allocate").setExact(true)).click();
      Thread.sleep(4000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes")).click();
      Thread.sleep(5000);
      page.reload();  // Refresh
      page.waitForLoadState();
      Thread.sleep(5000);
      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("PK-")).click();
      Thread.sleep(7000);
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Start Pick")).click();
      Thread.sleep(5000);
      // Save original tab before popup opens
      Page original = page;

      // --- OPEN POPUP ---
      Page popup = original.waitForPopup(() -> {
          original.getByRole(AriaRole.BUTTON,
                  new Page.GetByRoleOptions().setName("Start").setExact(true)).click();
      });

      popup.waitForLoadState();
      // Check the first checkbox
      popup.locator(".slds-checkbox_faux").first().click();

      // Click Save
      popup.getByRole(AriaRole.BUTTON,
              new Page.GetByRoleOptions().setName("Save")).click();

      // Click Back - POPUP WILL CLOSE HERE
      popup.getByRole(AriaRole.BUTTON,
              new Page.GetByRoleOptions().setName("Back")).click();

      // --- RETURN TO ORIGINAL TAB ---
      // The popup is now closed; do NOT use popup again

      original.bringToFront();
      original.waitForLoadState();

      // Click Back again on original page
      original.getByRole(AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Back")).click();

      Thread.sleep(5000);
      original.locator("((//div[@class='secondaryFields']/slot/records-highlights-details-item)[2]/div/p)[2]"
                  + "/slot/force-lookup/div/records-hoverable-link/div/a/span/slot/span/slot/span").click();
      Thread.sleep(5000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Invoice")).click();
      original.waitForLoadState();
      Thread.sleep(8000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select All Visible")).click();
      Thread.sleep(2000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Create Quote")).first().click();
      Thread.sleep(4000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back")).first().click();
      Thread.sleep(4000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create SM")).click();
      original.waitForLoadState();
      Thread.sleep(8000);

      // Logistics Partner / Logistics Contact (custom lookup widget, identified via data-lk attribute)
      original.locator("div.lk-ctrl[data-lk='logisticsPartner']").click();
      original.locator("input[data-lk='logisticsPartner']").pressSequentially(data.get("Logistics Partner"));
      Thread.sleep(2000);
      original.locator("div.lk-opt[data-lk='logisticsPartner']").filter(new Locator.FilterOptions().setHasText(data.get("Logistics Partner"))).first().click();
      Thread.sleep(1000);

      original.locator("div.lk-ctrl[data-lk='logisticsContact']").click();
      original.locator("input[data-lk='logisticsContact']").pressSequentially(data.get("Logistics Contact"));
      Thread.sleep(2000);
      original.locator("div.lk-opt[data-lk='logisticsContact']").filter(new Locator.FilterOptions().setHasText(data.get("Logistics Contact"))).first().click();
      Thread.sleep(1000);

      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select All Visible")).click();
      Thread.sleep(2000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Create Shipping Manifest")).first().click();
      Thread.sleep(4000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back")).first().click();
      Thread.sleep(5000);
      original.reload();  // Refresh
      original.waitForLoadState();
      Thread.sleep(5000);
      //page.keyboard().press("ArrowDown");
      for (int i = 0; i < 15; i++) {
      original.keyboard().press("ArrowDown");
      Thread.sleep(100);  // small delay
      }
      original.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("SM-")).click();
      Thread.sleep(4000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Ship Confirm")).click();
      Thread.sleep(4000);
      original.locator(".wrapped-header-datatable > .dt-outer-container > .slds-table_header-fixed_container > .slds-scrollable_y > .slds-table > thead > .slds-line-height_reset > th:nth-child(2) > .slds-cell-fixed > .slds-th__action > .slds-checkbox > .slds-checkbox__label > .slds-checkbox_faux").click();
      Thread.sleep(4000);
      original.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save")).click();
      Thread.sleep(4000);

        System.out.println("success completed so flow");
    }
}
