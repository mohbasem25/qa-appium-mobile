package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Shopping cart screen: lists items added from the catalog/detail screens,
 * supports per-item removal, and exposes "Proceed To Checkout" which leads
 * into the (unauthenticated-friendly) checkout step-one screen.
 */
public class CartPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartTitle")
    private WebElement cartTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartItem")
    private List<WebElement> cartItems;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/removeIU")
    private List<WebElement> removeItemButtons;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/checkoutBTN")
    private WebElement proceedToCheckoutButton;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/emptyCartTV")
    private WebElement emptyCartMessage;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/backIU")
    private WebElement backButton;

    public CartPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayedSafely(cartTitle);
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public boolean isEmptyCartMessageDisplayed() {
        return isDisplayedSafely(emptyCartMessage);
    }

    public CartPage removeItem(int index) {
        waitClickable(removeItemButtons.get(index)).click();
        return this;
    }

    public CartPage removeAllItems() {
        while (!removeItemButtons.isEmpty()) {
            removeItemButtons.get(0).click();
        }
        return this;
    }

    public CheckoutPage proceedToCheckout() {
        tap(proceedToCheckoutButton);
        return new CheckoutPage(driver);
    }

    public boolean isProceedToCheckoutEnabled() {
        return isDisplayedSafely(proceedToCheckoutButton) && proceedToCheckoutButton.isEnabled();
    }

    public CatalogPage goBackToCatalog() {
        tap(backButton);
        return new CatalogPage(driver);
    }
}
