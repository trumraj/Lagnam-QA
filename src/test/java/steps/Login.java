package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.*;
import utils.ExcelReader;
import utils.DriverFactory;

import java.util.Map;

public class Login {

    @Given("login to the site")
    public void login_to_the_site() throws InterruptedException {
        // ✅ Use the shared browser and page instance
        Page page = DriverFactory.getPage();
        if (page == null) {
            throw new RuntimeException("❌ Page not initialized! Make sure browser launched in hooks.");
        }

        Map<String, String> data = ExcelReader.getKeyValueData(
                "src/test/resources/testdata/SF_TestData.xlsx", "Login"
        );

        String url = data.get("URL");
        String userName = data.get("username");
        String password = data.get("pwd");

        System.out.println("🌐 Navigating to: " + url);
        page.navigate(url);

    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill(userName);
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill(password);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in")).click();
     page.waitForLoadState();
        page.waitForTimeout(5000);
        System.out.println("✅ Login successful, ready for next step.");

  }
}

