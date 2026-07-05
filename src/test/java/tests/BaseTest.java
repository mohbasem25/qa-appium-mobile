package tests;

import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.CatalogPage;
import pages.MenuPage;
import utils.DriverFactory;

/**
 * Shared lifecycle for all test classes.
 * <p>
 * A {@link ThreadLocal} driver instance is created fresh before every
 * {@code @Test} method and torn down afterwards. Starting from a clean app
 * session per test keeps native-app tests independent and re-runnable in any
 * order, at the cost of a slower suite - a deliberate, documented trade-off
 * (see README "Design decisions").
 * <p>
 * TestNG's {@code parallel="classes"} in {@code testng.xml} relies on this
 * ThreadLocal isolation to run {@code LoginTests}, {@code CatalogTests},
 * {@code CartTests} and {@code DrawerMenuTests} concurrently against
 * independent emulator/device sessions when more than one device is
 * available (e.g. a device farm or multiple local emulators). Against a
 * single local emulator, keep {@code parallel="none"} in testng.xml.
 */
public abstract class BaseTest {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DRIVER.set(DriverFactory.initDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver(DRIVER.get());
        DRIVER.remove();
    }

    protected AndroidDriver driver() {
        return DRIVER.get();
    }

    protected CatalogPage catalogPage() {
        return new CatalogPage(driver());
    }

    protected MenuPage menuPage() {
        return new MenuPage(driver());
    }
}
