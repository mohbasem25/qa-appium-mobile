package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product catalog / listing screen - the app's home screen once splash and
 * (optional) login are dismissed.
 */
public class CatalogPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    private List<WebElement> productTitles;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productPriceTV")
    private List<WebElement> productPrices;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartIU")
    private WebElement cartIcon;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartTV")
    private WebElement cartBadgeCount;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/sortIU")
    private WebElement sortIcon;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/menuIU")
    private WebElement menuButton;

    // Sort bottom-sheet options
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Name (A to Z)']")
    private WebElement sortNameAscending;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Name (Z to A)']")
    private WebElement sortNameDescending;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Price (low to high)']")
    private WebElement sortPriceAscending;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Price (high to low)']")
    private WebElement sortPriceDescending;

    public CatalogPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return !productTitles.isEmpty() && isDisplayedSafely(productTitles.get(0));
    }

    public int getProductCount() {
        waitVisibleAll(productTitles);
        return productTitles.size();
    }

    public List<String> getProductNames() {
        waitVisibleAll(productTitles);
        return productTitles.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<Double> getProductPricesAsNumbers() {
        waitVisibleAll(productPrices);
        return productPrices.stream()
                .map(el -> el.getText().replace("$", "").trim())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    public ProductDetailPage openProduct(String productName) {
        WebElement product = productTitles.stream()
                .filter(el -> el.getText().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementForProductException(productName));
        tap(product);
        return new ProductDetailPage(driver);
    }

    public ProductDetailPage openFirstProduct() {
        waitVisibleAll(productTitles);
        tap(productTitles.get(0));
        return new ProductDetailPage(driver);
    }

    public CartPage openCart() {
        tap(cartIcon);
        return new CartPage(driver);
    }

    public boolean isCartBadgeVisible() {
        return isDisplayedSafely(cartBadgeCount);
    }

    public String getCartBadgeCount() {
        return isCartBadgeVisible() ? cartBadgeCount.getText() : "0";
    }

    public MenuPage openMenu() {
        tap(menuButton);
        return new MenuPage(driver);
    }

    public CatalogPage openSortOptions() {
        tap(sortIcon);
        return this;
    }

    public CatalogPage sortByNameAscending() {
        openSortOptions();
        tap(waitVisible(sortNameAscending));
        return this;
    }

    public CatalogPage sortByNameDescending() {
        openSortOptions();
        tap(waitVisible(sortNameDescending));
        return this;
    }

    public CatalogPage sortByPriceAscending() {
        openSortOptions();
        tap(waitVisible(sortPriceAscending));
        return this;
    }

    public CatalogPage sortByPriceDescending() {
        openSortOptions();
        tap(waitVisible(sortPriceDescending));
        return this;
    }

    /**
     * Adds the given product to the cart directly from the listing view
     * using the per-card "add to cart" icon, identified by its position
     * relative to the product title via a relative UiAutomator lookup.
     */
    public CatalogPage addProductToCartFromListing(int productIndex) {
        List<WebElement> addToCartButtons = driver.findElements(
                AppiumBy.id("com.saucelabs.mydemoapp.android:id/addToCartIU"));
        wait.until(ExpectedConditions.visibilityOf(addToCartButtons.get(productIndex)));
        tap(addToCartButtons.get(productIndex));
        return this;
    }

    /** Thrown when a product referenced by name cannot be located in the current listing. */
    public static class NoSuchElementForProductException extends RuntimeException {
        public NoSuchElementForProductException(String productName) {
            super("No product found in catalog with name: " + productName);
        }
    }
}
