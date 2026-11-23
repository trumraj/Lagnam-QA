package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.*;
import utils.ExcelReader;
import utils.DriverFactory;

import java.util.Map;

public class startpick {

    
@Given("login to the site to test proposalui")
public void login_to_the_site_to_test_proposalui() throws InterruptedException {
        // ✅ Use the shared browser and page instance
        Page page = DriverFactory.getPage();
        if (page == null) {
            throw new RuntimeException("❌ Page not initialized! Make sure browser launched in hooks.");
        }

        Map<String, String> data = ExcelReader.getKeyValueData(
                "src/test/resources/testdata/SF_TestData.xlsx", "Login"
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

        // ============================================
        // ✔ ADD WAIT BEFORE CLICKING NEW (Option A)
        // ============================================
        page.waitForTimeout(9000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();

        // ============================================
        // ✔ ADD WAIT AFTER CLICKING NEW (Option A)
        // ============================================
    

        Thread.sleep(3000);
       page.locator("//label[text()='Proposal']/following::input[1]").fill("Test Auto001");
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Status")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Lead")).locator("span").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Inventory Account")).click();

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Inventory Account")).fill("apple Inc");
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(3).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Proposal Type")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Product and Services")).locator("span").nth(1).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Site")).click();

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Site")).fill("Test house");
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(3).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Price Book")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Price Book")).fill("standard price book");
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Payment Terms")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("NET 7")).locator("span").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Estimated Amount")).click();
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Estimated Amount")).fill("542154");
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Estimated Amount")).press("Enter");
        Thread.sleep(3000);
        page.locator("records-record-layout-row").filter(new Locator.FilterOptions().setHasText("*Estimated Amount")).locator("records-record-layout-item").nth(1).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Account")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Account")).fill("Test apple");
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(3).click();

        Thread.sleep(3000);

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Contact")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Bill to Contact")).fill("tester QA");
        Thread.sleep(5000);
        page.getByTitle("tester QA", new Page.GetByTitleOptions().setExact(true)).nth(0).click();
        Thread.sleep(3000);

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Account")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Account")).fill("pittwater");
        Thread.sleep(3000);
        page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName("Search Results")).locator("span").nth(2).click();
        Thread.sleep(3000);

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Contact")).click();
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Ship to Contact")).fill("dean Smith");
        Thread.sleep(3000);
        page.getByTitle("Dean Smith", new Page.GetByTitleOptions().setExact(true)).click();
        Thread.sleep(3000);

        // Save Proposal
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();

        // ============================================
        // ✔ ADD WAIT AFTER SAVE (Option A)
        // ============================================
        page.waitForTimeout(9000);

        Thread.sleep(3000);

        System.out.println("Proposal created successfully");

        // ------- ADD PRODUCTS -------
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Products")).click();
         Thread.sleep(3000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Products")).click();
         Thread.sleep(3000);
        page.getByPlaceholder("Search", new Page.GetByPlaceholderOptions().setExact(true)).click();
        page.getByPlaceholder("Search", new Page.GetByPlaceholderOptions().setExact(true)).fill("test-pd-0001");
        page.getByRole(AriaRole.GRIDCELL, new Page.GetByRoleOptions().setName("Choose a Row Select All")).locator("span").nth(2).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back")).click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit Product Grouping")).click();
        page.locator("//button[@name='picklist']").click();
        page.locator("(//div[@part='dropdown overlay'])[2]/lightning-base-combobox-item[1]").click();
        Thread.sleep(2000);
        page.locator("(//div[@class='slds-scrollable_y'])[2]/table/tbody/tr/td[8]/lightning-primitive-cell-factory/span/button/lightning-primitive-icon").click();
        Thread.sleep(2000);
        page.locator("(//div[@class='slds-scrollable_y'])[2]/table/tbody/tr/td[8]/lightning-primitive-cell-factory/span/button/lightning-primitive-icon").click();

        page.locator("//input[@name='dt-inline-edit-number']").clear();
        System.out.println("✅ Login successful, ready for next step.");
    }
@Given("start pick {int} qty")
public void start_pick_qty(Integer int1) {
    // Write code here that turns the phrase above into concrete actions

    System.out.println("✅ Login");
}
@Given("start pick partial qty")
public void start_pick_partial_qty() {
    // Write code here that turns the phrase above into concrete actions
 
    System.out.println("✅ Login successful, ready for next step.");
}
}
