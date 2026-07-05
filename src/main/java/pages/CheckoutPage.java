package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Checkout step-one screen ("Your Information") - reached from
 * {@link CartPage#proceedToCheckout()}. The suite only asserts that this
 * screen is reached and its key fields are present; filling in the full
 * multi-step checkout flow is intentionally out of scope for the cart tests.
 */
public class CheckoutPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/checkoutTitle")
    private WebElement checkoutTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameField")
    private WebElement fullNameField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/addressLine1Field")
    private WebElement addressField;

    public CheckoutPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayedSafely(checkoutTitle);
    }

    public boolean areInformationFieldsPresent() {
        return isDisplayedSafely(fullNameField) && isDisplayedSafely(addressField);
    }
}
