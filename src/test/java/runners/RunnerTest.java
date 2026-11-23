package runners;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"steps", "utils"},
  // include utils package
    plugin = {
        "pretty",
        "html:target/cucumber-report.html",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },

        monochrome = true,
         tags = "@login or @soflow",
    dryRun = false
)
public class RunnerTest {

    // Main method to run tests from IDE (e.g., VS Code Run button)
    public static void main(String[] args) {
        JUnitCore.main("runners.RunnerTest");
    }
}
