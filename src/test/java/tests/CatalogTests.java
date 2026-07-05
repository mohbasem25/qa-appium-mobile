package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CatalogPage;
import pages.ProductDetailPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Coverage for the product catalog: listing/loading, sort ordering, product
 * detail navigation, and add-to-cart from both the listing and the detail
 * screen.
 */
public class CatalogTests extends BaseTest {

    @Test(description = "The catalog screen loads with a non-empty, visible product listing")
    public void productListingLoadsSuccessfully() {
        CatalogPage catalog = catalogPage();

        Assert.assertTrue(catalog.isDisplayed(), "Catalog screen should be displayed on launch");
        Assert.assertTrue(catalog.getProductCount() > 0,
                "Catalog should list at least one product");
    }

    @Test(description = "Every listed product has both a name and a price rendered")
    public void everyProductHasNameAndPrice() {
        CatalogPage catalog = catalogPage();

        List<String> names = catalog.getProductNames();
        List<Double> prices = catalog.getProductPricesAsNumbers();

        Assert.assertEquals(names.size(), prices.size(),
                "Number of product names should match number of product prices");
        names.forEach(name -> Assert.assertFalse(name.isBlank(), "Product name should not be blank"));
        prices.forEach(price -> Assert.assertTrue(price > 0, "Product price should be a positive number"));
    }

    @Test(description = "Sorting by Name (A to Z) produces an alphabetically ascending listing")
    public void sortByNameAscendingOrdersAlphabetically() {
        CatalogPage catalog = catalogPage().sortByNameAscending();

        List<String> names = catalog.getProductNames();
        List<String> expectedSorted = new ArrayList<>(names);
        expectedSorted.sort(String.CASE_INSENSITIVE_ORDER);

        Assert.assertEquals(names, expectedSorted,
                "Product names should be sorted A to Z after selecting that sort option");
    }

    @Test(description = "Sorting by Name (Z to A) produces an alphabetically descending listing")
    public void sortByNameDescendingOrdersReverseAlphabetically() {
        CatalogPage catalog = catalogPage().sortByNameDescending();

        List<String> names = catalog.getProductNames();
        List<String> expectedSorted = new ArrayList<>(names);
        expectedSorted.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Assert.assertEquals(names, expectedSorted,
                "Product names should be sorted Z to A after selecting that sort option");
    }

    @Test(description = "Sorting by Price (low to high) produces an ascending price listing")
    public void sortByPriceAscendingOrdersCorrectly() {
        CatalogPage catalog = catalogPage().sortByPriceAscending();

        List<Double> prices = catalog.getProductPricesAsNumbers();
        List<Double> expectedSorted = new ArrayList<>(prices);
        expectedSorted.sort(Double::compareTo);

        Assert.assertEquals(prices, expectedSorted,
                "Product prices should be sorted low to high after selecting that sort option");
    }

    @Test(description = "Sorting by Price (high to low) produces a descending price listing")
    public void sortByPriceDescendingOrdersCorrectly() {
        CatalogPage catalog = catalogPage().sortByPriceDescending();

        List<Double> prices = catalog.getProductPricesAsNumbers();
        List<Double> expectedSorted = new ArrayList<>(prices);
        expectedSorted.sort(Double::compareTo);
        java.util.Collections.reverse(expectedSorted);

        Assert.assertEquals(prices, expectedSorted,
                "Product prices should be sorted high to low after selecting that sort option");
    }

    @Test(description = "Tapping a product in the catalog navigates to its product detail screen with matching title")
    public void tappingProductOpensMatchingDetailScreen() {
        CatalogPage catalog = catalogPage();
        String expectedName = TestData.SAMPLE_PRODUCT_NAME;

        ProductDetailPage detail = catalog.openProduct(expectedName);

        Assert.assertTrue(detail.isDisplayed(), "Product detail screen should be displayed");
        Assert.assertEquals(detail.getProductTitle(), expectedName,
                "Detail screen title should match the tapped product's name");
    }

    @Test(description = "The product detail screen renders a non-empty description for the selected product")
    public void productDetailShowsDescription() {
        ProductDetailPage detail = catalogPage().openFirstProduct();

        Assert.assertTrue(detail.isDescriptionDisplayed(),
                "Product description should be visible on the detail screen");
    }

    @Test(description = "Adding a product to the cart directly from the listing updates the cart badge count")
    public void addToCartFromListingUpdatesBadge() {
        CatalogPage catalog = catalogPage();
        Assert.assertFalse(catalog.isCartBadgeVisible(),
                "Cart badge should not be visible when the cart is empty");

        catalog.addProductToCartFromListing(0);

        Assert.assertTrue(catalog.isCartBadgeVisible(),
                "Cart badge should appear after adding an item from the listing");
        Assert.assertEquals(catalog.getCartBadgeCount(), "1",
                "Cart badge should read 1 after adding a single item");
    }

    @Test(description = "Adding a product to the cart from the product detail screen updates the cart badge count")
    public void addToCartFromDetailUpdatesBadge() {
        CatalogPage catalog = catalogPage();
        ProductDetailPage detail = catalog.openFirstProduct();

        detail.addToCart();
        CartPage cart = detail.goToCart();

        Assert.assertTrue(cart.isDisplayed(), "Cart screen should open after tapping the cart icon");
        Assert.assertEquals(cart.getCartItemCount(), 1,
                "Cart should contain exactly one item after a single add-to-cart action");
    }

    @Test(description = "Incrementing the quantity stepper on the detail screen adds that many units when added to the cart")
    public void quantityStepperAffectsCartLineItem() {
        ProductDetailPage detail = catalogPage().openFirstProduct();

        int initialQuantity = detail.getQuantity();
        detail.incrementQuantity().incrementQuantity();

        Assert.assertEquals(detail.getQuantity(), initialQuantity + 2,
                "Quantity label should increase by 2 after two taps on the increment control");
    }
}
