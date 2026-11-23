package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.*;
import utils.ExcelReader;
import utils.DriverFactory;

import java.util.Map;

public class orgSetup {

    // --------------------------------------------------------------------
    //  ACCOUNT CREATION
    // --------------------------------------------------------------------
    @Given("create account site etc and setup org")
    public void create_account_site_etc_and_setup_org() throws InterruptedException {

        Page page = DriverFactory.getPage();
        if (page == null) {
            throw new RuntimeException("❌ Page not initialized! Run login step first.");
        }

        Map<String, String> data = ExcelReader.getKeyValueData(
                "src/test/resources/testdata/SF_TestData.xlsx",
                "OrgSetup"
        );

        String[] accounts = {
                data.get("Account1"),
                data.get("Account2"),
                data.get("Account3")
        };

        System.out.println("🏗️ Starting org setup... Creating accounts");

        for (String acc : accounts) {
            if (acc != null && !acc.isEmpty()) {
                createAccount(page, acc);
            }
        }

        System.out.println("✅ All accounts created successfully");
    }

    private void createAccount(Page page, String accName) throws InterruptedException {

        System.out.println("➡️ Creating Account: " + accName);

        // APP LAUNCHER
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("App Launcher")).click();
        page.waitForTimeout(1500);

        page.getByRole(AriaRole.COMBOBOX,
                new Page.GetByRoleOptions().setName("Search apps and items..."))
                .fill("Accounts");

        page.waitForTimeout(1500);

        page.getByRole(AriaRole.OPTION,
                new Page.GetByRoleOptions().setName("Accounts").setExact(true))
                .click();

        page.waitForTimeout(2000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
        page.waitForTimeout(2000);

        // Some orgs show NEXT screen
        try {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();
            page.waitForTimeout(500);
        } catch (Exception ignored) {}

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Account Name"))
                .fill(accName);

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("DBA"))
                .fill("Test DBA");

        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Active")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Yes")).click();

        // PAYMENT TERMS
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Payment Terms")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("NET 7"))
                .locator("span").nth(1).click();

        // SAVE
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
        page.waitForTimeout(3000);

        // CONTACT CREATION
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New").setExact(true)).click();

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name"))
                .fill("tester");

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name"))
                .fill("QA");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();

        page.waitForTimeout(2500);
    }
 @Given("create site and name")
public void create_site_and_name() throws InterruptedException{
    // Write code here that turns the phrase above into concrete actions
    
    Page page = DriverFactory.getPage();
        if (page == null) {
            throw new RuntimeException("❌ Page not initialized! Run login step first.");
        }

        Map<String, String> data = ExcelReader.getKeyValueData(
                "src/test/resources/testdata/SF_TestData.xlsx",
                "OrgSetup"
        );

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("App Launcher")).click();
        page.waitForTimeout(1500);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search apps and items..."))
                .fill("Sites");
        page.waitForTimeout(1500);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Sites")).click();
        page.waitForTimeout(2000);

         page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Site").setExact(true)).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Site").setExact(true)).fill(data.get("Site"));
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Site Name")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Site Name")).fill(data.get("Site Name"));
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Site Account")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Site Account")).fill(data.get("Site Account"));
        page.getByTitle(data.get("Site Account")).nth(2).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
       Thread.sleep(3000);
    }
    @Given("create site rack and etc")
public void create_site_rack_and_etc() throws InterruptedException {

    Page page = DriverFactory.getPage();
    if (page == null) {
        throw new RuntimeException("❌ Page not initialized! Run login step first.");
    }

    String file = "src/test/resources/testdata/SF_TestData.xlsx";
    String sheet = "OrgSetup";
    int locationHeaderRow = 14; // "Location" row - start of the Locations sub-table
    int locationEndRow = 19;    // "Short Code" row - end of the Locations sub-table

    String site = ExcelReader.getKeyValueData(file, sheet).get("Site");

    // ▶ Loop for each location column
    int maxColumns = ExcelReader.getTotalColumns(file, sheet, locationHeaderRow);

    for (int col = 1; col <= maxColumns; col++) {

        Map<String, String> data = ExcelReader.getColumnData(file, sheet, col, locationHeaderRow, locationEndRow);

        String location  = data.get("Location");
        String rack      = data.get("Rack");
        String rackLevel = data.get("Rack Level");
        String bin       = data.get("Bin");
        String sortOrder = data.get("Sort Order");
        String shortCode = data.get("Short Code");

        System.out.println("➡️ Creating records for Excel column: " + col);

        if (location == null || location.isEmpty()) {
            System.out.println("⏭️ Skipping column " + col + " (No Location value)");
            continue;
        }

        // Create Location → Rack → Rack Level → Bin
        createLocation(page, site, location, sortOrder, shortCode, rack, rackLevel, bin);

        System.out.println("✅ Completed column: " + col);
    }
}

  private void createLocation(Page page, String site, String loc, String sortOrder, String code,
                            String rack, String rackLevel, String binName) throws InterruptedException {

    Thread.sleep(1000);

    // Go to Sites → Click the site created earlier
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("App Launcher")).click();
    page.waitForTimeout(1500);
    page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search apps and items..."))
            .fill("Sites");
    page.waitForTimeout(1500);
    page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Sites")).click();
    page.waitForTimeout(3000);

    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(site).setExact(true)).first().click();

    // ---------------------
    // CREATE LOCATION
    // ---------------------
     Thread.sleep(3000);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Inventory Location")).fill(loc);
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("SortOrder")).fill(sortOrder);
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Location Short Code")).fill(code);

    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enable Put Away")).check();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enable Heatmap")).check();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enable Inventory Calculation")).check();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enable Allocation")).check();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enable Inventory Actions")).check();

    Thread.sleep(1000);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
    page.waitForTimeout(15000);

    // Open saved location row
page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(loc).setExact(true)).first().click();


    Thread.sleep(3000);

    // ---------------------
    // CREATE RACK
    // ---------------------
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
    page.waitForTimeout(1500);
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Rack Name")).fill(rack);
    Thread.sleep(2000);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
        page.waitForTimeout(15000);

    Thread.sleep(3000);
   page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(rack).setExact(true)).first().click();



    Thread.sleep(2000);

    // ---------------------
    // CREATE RACK LEVEL
    // ---------------------
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
    page.waitForTimeout(1500);
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Rack Level")).fill(rackLevel);
    Thread.sleep(3000);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
page.waitForTimeout(15000);
 Thread.sleep(6000);
     page.locator("xpath=//*[text()='Rack Levels']/following::a[@class='slds-truncate']").nth(0).click();

    // ---------------------
    // CREATE BIN
    // ---------------------

        Thread.sleep(3000);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
    page.waitForTimeout(1500);
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Bin")).fill(binName);

    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Height")).fill("12");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Length")).fill("12");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Width")).fill("12");
Thread.sleep(2000);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
    Thread.sleep(2000);

}

@Given("create product and price book")
public void create_product_and_price_book() throws InterruptedException{
    // Write code here that turns the phrase above into concrete actions
    Page page = DriverFactory.getPage();
        if (page == null) {
            throw new RuntimeException("❌ Page not initialized! Run login step first.");
        }

        Map<String, String> data = ExcelReader.getKeyValueData(
                "src/test/resources/testdata/SF_TestData.xlsx",
                "OrgSetup"
        );
    Thread.sleep(6000);


        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("App Launcher")).click();
        Thread.sleep(2000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search apps and items...")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search apps and items...")).fill("Products");
        Thread.sleep(2000);

        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Products").setExact(true)).click();
        Thread.sleep(5000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("ProductItems purchased from")).locator("span").first().click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).fill("TjProduct-002");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Description")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Description")).fill("Test Product-001");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Active")).check();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Product Family")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Augmented Reality Headsets")).locator("span").nth(1).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("ABC")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("A")).locator("span").nth(1).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Manufacturer Name")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Manufacturer Name")).fill("Apple Inc");
        Thread.sleep(3000);
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Manufacturer Name")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Apple Inc").setExact(true)).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
        Thread.sleep(4000);
      //  page.navigate("https://velocity-innovation-4134-dev-ed.scratch.lightning.force.com/lightning/r/Product2/01tdi000007nhB3AAI/view");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Standard Price")).click();
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("List Price")).click();
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("List Price")).fill("12");
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Unit Cost")).click();
        page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Unit Cost")).fill("12");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
}
}