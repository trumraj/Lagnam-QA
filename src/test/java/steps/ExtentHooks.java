package steps;

import io.cucumber.java.*;
import utils.DriverFactory;
import utils.ExtentManager;

import com.aventstack.extentreports.*;
import com.microsoft.playwright.Page;

import java.nio.file.*;

public class ExtentHooks {

    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @BeforeAll
    public static void beforeAll() {
        DriverFactory.launchBrowserOnce();
        System.out.println("🌍 Browser launched before all scenarios.");
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        ExtentTest extentTest = extent.createTest(scenario.getName());
        test.set(extentTest);

        boolean isLogin = scenario.getSourceTagNames().contains("@login");

        if (!isLogin) {
            DriverFactory.startScenarioContext(scenario.getName());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {

        boolean failed = scenario.isFailed();
        ExtentTest extentTest = test.get();
        String scenarioName = scenario.getName().replaceAll(" ", "_");

        try {
            Page page = DriverFactory.getPage();

            if (failed && page != null) {

                extentTest.fail("❌ Scenario failed: " + scenarioName);

                // SAVE screenshot here (physical location)
                String screenshotDir = "target/screenshots/";
                Files.createDirectories(Paths.get(screenshotDir));

                String fullPath = screenshotDir + scenarioName + ".png";

                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(fullPath))
                        .setFullPage(true));

                // EXTENT REPORT IS HERE:
                // test-output/ExtentReport.html
                //
                // So relative path:
                // ../target/screenshots/<file>.png
                String relativePath = "../target/screenshots/" + scenarioName + ".png";

                extentTest.addScreenCaptureFromPath(relativePath);
            }

        } catch (Exception e) {
            extentTest.warning("⚠ Screenshot error: " + e.getMessage());
        }

        // VIDEO
        String video = DriverFactory.stopAndSaveVideo(scenarioName, failed);
        if (video != null) {
            extentTest.info("<a href='" + video + "' target='_blank'>📹 Watch Video</a>");
        }

        if (!failed) {
            extentTest.pass("✅ Scenario passed");
        }
    }

    @AfterAll
    public static void afterAll() {
        DriverFactory.closeAll();
        extent.flush();
        System.out.println("📄 Extent Report saved.");
    }
}
