package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CatalogPage;
import pages.CheckoutPage;
import pages.ProductDetailPage;

/**
 * Coverage for the shopping cart: badge count accuracy, item removal,
 * reaching the checkout screen, and the empty-cart state.
 */
public class CartTests extends BaseTest {

    private CartPage addFirstProductAndOpenCart() {
        CatalogPage catalog = catalogPage();
        catalog.addProductToCartFromListing(0);
        return catalog.openCart();
    }

    @Test(description = "The cart badge count increments correctly as multiple distinct products are added")
    public void cartBadgeReflectsMultipleAddedItems() {
        CatalogPage catalog = catalogPage();

        catalog.addProductToCartFromListing(0);
        catalog.addProductToCartFromListing(1);

        Assert.assertEquals(catalog.getCartBadgeCount(), "2",
                "Cart badge should reflect two distinct items added from the listing");
    }

    @Test(description = "Opening the cart after adding one item shows exactly one line item")
    public void cartShowsAddedItem() {
        CartPage cart = addFirstProductAndOpenCart();

        Assert.assertTrue(cart.isDisplayed(), "Cart screen should be displayed");
        Assert.assertEquals(cart.getCartItemCount(), 1, "Cart should contain exactly one item");
    }

    @Test(description = "Removing the only item in the cart empties it and shows the empty-cart state")
    public void removingOnlyItemShowsEmptyCartState() {
        CartPage cart = addFirstProductAndOpenCart();

        cart.removeItem(0);

        Assert.assertEquals(cart.getCartItemCount(), 0, "Cart should have zero items after removal");
        Assert.assertTrue(cart.isEmptyCartMessageDisplayed(),
                "An empty-cart message should be shown once the last item is removed");
    }

    @Test(description = "Removing one of several items leaves the remaining items intact")
    public void removingOneOfSeveralItemsKeepsOthers() {
        CatalogPage catalog = catalogPage();
        catalog.addProductToCartFromListing(0);
        catalog.addProductToCartFromListing(1);
        CartPage cart = catalog.openCart();

        Assert.assertEquals(cart.getCartItemCount(), 2, "Precondition: cart should start with two items");

        cart.removeItem(0);

        Assert.assertEquals(cart.getCartItemCount(), 1,
                "Cart should contain exactly one item after removing one of two");
    }

    @Test(description = "The cart is empty and shows the empty-cart message on a fresh app session")
    public void freshSessionStartsWithEmptyCart() {
        CatalogPage catalog = catalogPage();
        CartPage cart = catalog.openCart();

        Assert.assertTrue(cart.isEmptyCartMessageDisplayed(),
                "A fresh app session should present an empty cart");
        Assert.assertEquals(cart.getCartItemCount(), 0, "Item count should be zero for an empty cart");
    }

    @Test(description = "Proceeding to checkout from a non-empty cart reaches the checkout information screen")
    public void proceedToCheckoutReachesCheckoutScreen() {
        CartPage cart = addFirstProductAndOpenCart();

        Assert.assertTrue(cart.isProceedToCheckoutEnabled(),
                "Proceed To Checkout should be enabled when the cart has at least one item");

        CheckoutPage checkout = cart.proceedToCheckout();

        Assert.assertTrue(checkout.isDisplayed(), "Checkout screen should be reached");
        Assert.assertTrue(checkout.areInformationFieldsPresent(),
                "Checkout screen should present the customer information fields");
    }

    @Test(description = "Adding to cart from the product detail screen and then removing it in the cart empties the cart")
    public void addFromDetailThenRemoveEmptiesCart() {
        CatalogPage catalog = catalogPage();
        ProductDetailPage detail = catalog.openFirstProduct();
        detail.addToCart();

        CartPage cart = detail.goToCart();
        Assert.assertEquals(cart.getCartItemCount(), 1, "Cart should have one item added from the detail screen");

        cart.removeItem(0);
        Assert.assertTrue(cart.isEmptyCartMessageDisplayed(),
                "Cart should show the empty state after removing the only item");
    }
}
