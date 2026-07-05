package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Product detail screen, reached by tapping a product card in {@link CatalogPage}.
 * Shows the product image, name, price, description, a quantity stepper, and
 * an "Add To Cart" call-to-action.
 */
public class ProductDetailPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    private WebElement productTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/priceTV")
    private WebElement productPrice;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/descriptionTV")
    private WebElement productDescription;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/addToCartBTN")
    private WebElement addToCartButton;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/goToCartIU")
    private WebElement goToCartIcon;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/incrementQuantityIU")
    private WebElement incrementQuantityButton;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/decrementQuantityIU")
    private WebElement decrementQuantityButton;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/quantityTV")
    private WebElement quantityLabel;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/backIU")
    private WebElement backButton;

    public ProductDetailPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayedSafely(productTitle);
    }

    public String getProductTitle() {
        return waitVisible(productTitle).getText();
    }

    public String getProductPrice() {
        return waitVisible(productPrice).getText();
    }

    public boolean isDescriptionDisplayed() {
        return isDisplayedSafely(productDescription);
    }

    public int getQuantity() {
        return Integer.parseInt(waitVisible(quantityLabel).getText().trim());
    }

    public ProductDetailPage incrementQuantity() {
        tap(incrementQuantityButton);
        return this;
    }

    public ProductDetailPage decrementQuantity() {
        tap(decrementQuantityButton);
        return this;
    }

    public ProductDetailPage addToCart() {
        tap(addToCartButton);
        return this;
    }

    public CartPage goToCart() {
        tap(goToCartIcon);
        return new CartPage(driver);
    }

    public CatalogPage goBackToCatalog() {
        tap(backButton);
        return new CatalogPage(driver);
    }
}
