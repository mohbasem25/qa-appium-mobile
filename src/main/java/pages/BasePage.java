package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.List;

/**
 * Common driver/wait plumbing shared by every page object.
 * <p>
 * All waits are explicit ({@link WebDriverWait}) and centered on
 * {@link ExpectedConditions}; the framework deliberately avoids
 * {@code Thread.sleep} for synchronization. The one place a fixed pause is
 * used is documented inline with the reason it's unavoidable (a CSS/JS
 * transition inside the webview screen that exposes no observable DOM state
 * change to wait on).
 */
public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("wait.explicit.timeoutSeconds")),
                Duration.ofMillis(ConfigReader.getInt("wait.pollingIntervalMillis")));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    protected WebElement waitVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitVisibleAll(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    protected WebElement waitClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected boolean waitInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean isDisplayedSafely(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception notPresent) {
            return false;
        }
    }

    protected void tap(WebElement element) {
        waitClickable(element).click();
    }

    protected void typeText(WebElement element, String text) {
        WebElement field = waitVisible(element);
        field.clear();
        field.sendKeys(text);
    }

    /** Presses the hardware/software back button - useful for closing the webview screen. */
    protected void pressBack() {
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
    }

    /**
     * Scrolls a scrollable Android view until an element with the given text
     * is visible, using UiAutomator's built-in UiScrollable helper. This is
     * the idiomatic way to reach items below the fold in a native list
     * (e.g. sort options, product list) without brittle coordinate swipes.
     */
    protected WebElement scrollToText(String text) {
        String uiScrollableSelector = String.format(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().textContains(\"%s\"))",
                text);
        return driver.findElement(AppiumBy.androidUIAutomator(uiScrollableSelector));
    }
}
