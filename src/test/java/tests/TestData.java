package tests;

/**
 * Central place for test-fixture constants (credentials, product names) so
 * they aren't duplicated/magic-stringed across test classes.
 * <p>
 * The credentials below are the public, documented demo credentials shipped
 * with the Sauce Labs "My Demo App" sample application - not a secret.
 */
public final class TestData {

    public static final String VALID_USERNAME = "bob@example.com";
    public static final String VALID_PASSWORD = "10203040";

    public static final String INVALID_USERNAME = "not_a_real_user@example.com";
    public static final String INVALID_PASSWORD = "wrongPassword!";

    public static final String SAMPLE_PRODUCT_NAME = "Sauce Labs Backpack";

    private TestData() {
        // constants holder - no instances
    }
}
