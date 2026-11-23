package utils;

import com.microsoft.playwright.*;
import java.nio.file.*;

public class DriverFactory {

    private static Playwright playwright;
    private static Browser browser;

    private static BrowserContext loginContext;   // Used ONLY for login scenario
    private static Page loginPage;

    private static BrowserContext scenarioContext; // Used per scenario
    private static Page scenarioPage;

    private static final Path STORAGE_STATE = Paths.get("target/storage-state.json");
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String VIDEO_DIR = "target/videos";

    private static String savedUrl;   // Reused for all scenarios

    // ----------------------------------------------------------
    // 1) Launch Browser Once → Login scenario uses this context
    // ----------------------------------------------------------
    public static void launchBrowserOnce() {
        try {
            if (playwright == null) {

                playwright = Playwright.create();
                browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
                );

                Files.createDirectories(Paths.get(SCREENSHOT_DIR));
                Files.createDirectories(Paths.get(VIDEO_DIR));

                loginContext = browser.newContext();
                loginPage = loginContext.newPage();

                System.out.println("🌐 Login browser launched successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Page getLoginPage() {
        return loginPage;
    }

    // ----------------------------------------------------------
    // 2) Start new context per scenario (OrgSetup, Opp, etc.)
    // ----------------------------------------------------------
    public static void startScenarioContext(String scenarioName) {
        try {

            if (loginContext == null || loginPage == null) {
                throw new RuntimeException("❌ Login context missing! Run @login scenario first.");
            }

            // Save session from login
            loginContext.storageState(
                new BrowserContext.StorageStateOptions().setPath(STORAGE_STATE)
            );

            savedUrl = loginPage.url();
            if (savedUrl == null || savedUrl.isEmpty())
                savedUrl = "https://login.salesforce.com";

            System.out.println("🔗 Using URL for scenario: " + savedUrl);

            // Create per-scenario video folder
            String safe = scenarioName.replaceAll("[^a-zA-Z0-9-_]", "_");
            Path videoDir = Paths.get(VIDEO_DIR, safe);
            Files.createDirectories(videoDir);

            // Create new scenario context
            scenarioContext = browser.newContext(
                new Browser.NewContextOptions()
                    .setStorageStatePath(STORAGE_STATE)
                    .setRecordVideoDir(videoDir)
                    .setRecordVideoSize(1280, 720)
            );

            scenarioPage = scenarioContext.newPage();

            scenarioPage.navigate(savedUrl);
            scenarioPage.waitForLoadState();
            scenarioPage.waitForTimeout(1500);

            System.out.println("🎥 Scenario context ready (recording): " + scenarioName);

        } catch (Exception e) {
            System.out.println("⚠ startScenarioContext FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Active page getter for test steps
    public static Page getPage() {
        if (scenarioPage != null) return scenarioPage;
        if (loginPage != null) return loginPage;

        throw new RuntimeException("❌ No active Playwright page available!");
    }

    // Screenshot
    public static Path takeScreenshot(String scenarioName) {
        try {
            Page page = getPage();
            String safe = scenarioName.replaceAll("[^a-zA-Z0-9-_]", "_");
            Path file = Paths.get(SCREENSHOT_DIR, safe + ".png");

            Files.createDirectories(file.getParent());
            page.waitForTimeout(300);

            page.screenshot(new Page.ScreenshotOptions()
                .setFullPage(true)
                .setPath(file));

            return file;
        } catch (Exception e) {
            System.out.println("⚠ Screenshot failed: " + e.getMessage());
            return null;
        }
    }

    // Stop video
    public static String stopAndSaveVideo(String scenarioName, boolean failed) {
        try {
            if (scenarioContext == null || scenarioPage == null)
                return null;

            Path raw = scenarioPage.video().path();

            scenarioContext.close();  // finishes the video

            scenarioPage = null;
            scenarioContext = null;

            if (!failed) {
                Files.deleteIfExists(raw);
                return null;
            }

            String safe = scenarioName.replaceAll("[^a-zA-Z0-9-_]", "_");
            Path finalPath = Paths.get(VIDEO_DIR, safe + ".webm");
            Files.move(raw, finalPath, StandardCopyOption.REPLACE_EXISTING);

            return finalPath.toAbsolutePath().toString();

        } catch (Exception e) {
            System.out.println("⚠ Video save error: " + e.getMessage());
            return null;
        }
    }

    // Cleanup after all scenarios
    public static void closeAll() {
        try {
            if (scenarioContext != null) scenarioContext.close();
            if (loginContext != null) loginContext.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();

            System.out.println("🧹 All browsers closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
