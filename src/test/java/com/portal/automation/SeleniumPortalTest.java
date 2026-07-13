package com.portal.automation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end UI automation for the Student Portal using Selenium WebDriver.
 *
 * <p>The application is booted on a random port ({@code webEnvironment =
 * RANDOM_PORT}) and driven through a headless Chromium browser. The tests
 * exercise the full workflow required by the assignment: opening the portal,
 * registering, logging in, navigating between the dashboard, profile and
 * results pages, and verifying that the correct student information is
 * displayed after authentication.</p>
 *
 * <p>Browser/driver binaries are resolved from environment variables so the
 * suite is portable:</p>
 * <ul>
 *   <li>{@code CHROME_BIN} — Chromium binary (default
 *       {@code /opt/pw-browsers/chromium})</li>
 *   <li>{@code CHROMEDRIVER_BIN} — matching chromedriver (default
 *       {@code /opt/chromedriver141/chromedriver}); if absent, Selenium
 *       Manager resolves the driver automatically.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumPortalTest {

    private static final String DEFAULT_CHROME_BIN = "/opt/pw-browsers/chromium";
    private static final String DEFAULT_CHROMEDRIVER_BIN = "/opt/chromedriver141/chromedriver";

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        String driverPath = env("CHROMEDRIVER_BIN", DEFAULT_CHROMEDRIVER_BIN);
        if (new File(driverPath).exists()) {
            // Use the pre-installed, version-matched driver and skip the
            // network round-trip that Selenium Manager would otherwise make.
            System.setProperty("webdriver.chrome.driver", driverPath);
        }

        ChromeOptions options = new ChromeOptions();
        String chromeBin = env("CHROME_BIN", DEFAULT_CHROME_BIN);
        if (new File(chromeBin).exists()) {
            options.setBinary(chromeBin);
        }
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1280,900");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("A newly registered student can log in and see their own profile")
    void registerLoginAndVerifyProfile() {
        String username = "auto_" + System.currentTimeMillis();
        String fullName = "Automation Tester";
        String email = username + "@student.portal";
        String program = "BSc Software Engineering";

        // 1. Open the portal and go to the registration page.
        driver.get(baseUrl() + "/register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("register-submit")));

        // 2. Fill in and submit the registration form.
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys("Sup3rSecret!");
        driver.findElement(By.id("fullName")).sendKeys(fullName);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("program")).sendKeys(program);
        driver.findElement(By.id("enrollmentYear")).sendKeys("2024");
        driver.findElement(By.id("register-submit")).click();

        // 3. Registration redirects to the login page with a success banner.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registered-message")));

        // 4. Log in with the newly created credentials.
        login(username, "Sup3rSecret!");

        // 5. The dashboard greets the student by their full name.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcome-heading")));
        assertTrue(driver.findElement(By.id("welcome-heading")).getText().contains(fullName),
                "Dashboard should greet the logged-in student by name");
        assertEquals(username, driver.findElement(By.id("dashboard-username")).getText());

        // 6. Navigate to the profile page and verify the stored details.
        driver.findElement(By.id("nav-profile")).click();
        wait.until(ExpectedConditions.urlContains("/profile"));
        assertEquals(fullName, driver.findElement(By.id("profile-fullName")).getText());
        assertEquals(email, driver.findElement(By.id("profile-email")).getText());
        assertEquals(program, driver.findElement(By.id("profile-program")).getText());

        // 7. A brand-new student has no results yet.
        driver.findElement(By.id("nav-results")).click();
        wait.until(ExpectedConditions.urlContains("/results"));
        assertTrue(driver.findElement(By.id("results-table")).getText().contains("No results recorded yet"),
                "A new student should have no academic results");
    }

    @Test
    @DisplayName("The seeded demo student sees the correct academic results")
    void demoStudentSeesResults() {
        driver.get(baseUrl() + "/login");
        login("demo", "Passw0rd!");

        // Dashboard reflects the seeded demo data.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcome-heading")));
        assertTrue(driver.findElement(By.id("welcome-heading")).getText().contains("Demo Student"));
        assertEquals("4", driver.findElement(By.id("stat-courses")).getText());

        // Results page lists the seeded courses and an average.
        driver.findElement(By.id("nav-results")).click();
        wait.until(ExpectedConditions.urlContains("/results"));
        String resultsTable = driver.findElement(By.id("results-table")).getText();
        assertTrue(resultsTable.contains("CS101"), "Results should include course CS101");
        assertTrue(resultsTable.contains("Database Systems"), "Results should include course titles");
        assertEquals("85.6", driver.findElement(By.id("results-average")).getText());

        // Logging out returns to the login page with a confirmation banner.
        driver.findElement(By.id("nav-logout")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-message")));
    }

    @Test
    @DisplayName("The Access Center lists the external university portals")
    void accessCenterShowsExternalLinks() {
        driver.get(baseUrl() + "/login");
        login("demo", "Passw0rd!");

        // Navigate to the Access Center from the nav bar.
        driver.findElement(By.id("nav-access")).click();
        wait.until(ExpectedConditions.urlContains("/access"));

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Student Access Center"), "Access Center heading should be shown");
        assertTrue(pageText.contains("Student Portal"), "Should list the Student Portal link");
        assertTrue(pageText.contains("Blended Learning Center"), "Should list the e-learn link");
        assertTrue(pageText.contains("Question Bank"), "Should list the Question Bank link");

        // The cards must actually point at the external university URLs.
        boolean linksToStudentPortal = driver.findElements(By.tagName("a")).stream()
                .anyMatch(a -> "https://studentportal.diu.edu.bd/".equals(a.getAttribute("href")));
        assertTrue(linksToStudentPortal, "A card should link to the real Student Portal URL");
    }

    /** Submits the login form and waits for the post-login redirect. */
    private void login(String username, String password) {
        if (!driver.getCurrentUrl().contains("/login")) {
            driver.get(baseUrl() + "/login");
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-submit")));
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-submit")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
