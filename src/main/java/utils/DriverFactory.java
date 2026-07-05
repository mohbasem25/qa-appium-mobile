package utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Centralizes {@link AndroidDriver} construction and teardown.
 * <p>
 * Kept as a plain static factory (rather than a Singleton-per-thread) so that
 * TestNG's {@code parallel="classes"} execution model works safely: each test
 * class calls {@link #initDriver()} in its own {@code @BeforeClass}/
 * {@code @BeforeMethod} and stores the resulting driver in a {@link ThreadLocal},
 * see {@code tests.BaseTest}.
 * <p>
 * All capabilities are sourced from {@link ConfigReader} - nothing device- or
 * path-specific is hardcoded here, which is what lets the same code run
 * unmodified against a local emulator, a CI emulator, or (with a config swap)
 * a real device / cloud device farm.
 */
public final class DriverFactory {

    private DriverFactory() {
        // static utility - no instances
    }

    public static AndroidDriver initDriver() {
        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName(ConfigReader.get("appium.platformName"));
        options.setDeviceName(ConfigReader.get("appium.deviceName"));
        options.setAutomationName(ConfigReader.get("appium.automationName"));
        options.setPlatformVersion(ConfigReader.get("appium.platformVersion", ""));

        options.setApp(resolveAppPath());
        options.setAppPackage(ConfigReader.get("appium.app.package"));
        options.setAppActivity(ConfigReader.get("appium.app.activity"));

        options.setNewCommandTimeout(
                Duration.ofSeconds(ConfigReader.getInt("appium.newCommandTimeout")));
        options.setNoReset(ConfigReader.getBoolean("appium.noReset"));
        options.setFullReset(ConfigReader.getBoolean("appium.fullReset"));
        options.setAutoGrantPermissions(ConfigReader.getBoolean("appium.autoGrantPermissions"));

        try {
            URL serverUrl = new URL(ConfigReader.get("appium.server.url"));
            return new AndroidDriver(serverUrl, options);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    "Invalid appium.server.url in config.properties", e);
        }
    }

    /**
     * Resolves the configured app path to an absolute path so the framework
     * behaves the same whether Maven is invoked from the repo root or a CI
     * working directory. If the configured path doesn't exist on disk, the
     * raw value is still passed through - it may be an absolute path outside
     * the project, or Appium may resolve it against its own home directory.
     */
    private static String resolveAppPath() {
        String configuredPath = ConfigReader.get("appium.app.path");
        File file = new File(configuredPath);
        return file.exists() ? file.getAbsolutePath() : configuredPath;
    }

    public static void quitDriver(AndroidDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }
}
