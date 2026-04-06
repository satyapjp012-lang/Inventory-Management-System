package inventory.management.system;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComboBox;

/**
 * Demo product lists per category (no database). Used by sales and purchase forms.
 */
public final class ProductCatalog {

    public static final String PLACEHOLDER = "— Select —";

    private static final Map<String, String[]> BY_CATEGORY = new LinkedHashMap<>();

    static {
        BY_CATEGORY.put("Hardware",
                new String[] { "Drill Machine", "Hammer", "Screwdriver Set", "LED Bulb Pack", "USB-C Cable" });
        BY_CATEGORY.put("Shoes", new String[] { "Running Shoes", "Formal Shoes", "Sports Shoes", "Sandals" });
        BY_CATEGORY.put("Grocery", new String[] { "Rice 10kg", "Wheat Flour 10kg", "Cooking Oil 5L", "Milk 1L" });
    }

    private ProductCatalog() {
    }

    /** Values for a category dropdown (placeholder + Hardware, Shoes, Grocery). */
    public static String[] categoryComboItems() {
        return new String[] { PLACEHOLDER, "Hardware", "Shoes", "Grocery" };
    }

    /** Refill product dropdown when category changes; always starts with placeholder. */
    public static void refreshProductCombo(JComboBox<String> productCombo, Object selectedCategory) {
        productCombo.removeAllItems();
        productCombo.addItem(PLACEHOLDER);
        if (selectedCategory == null || isPlaceholder(String.valueOf(selectedCategory))) {
            return;
        }
        String cat = String.valueOf(selectedCategory);
        String[] products = BY_CATEGORY.get(cat);
        if (products != null) {
            for (String p : products) {
                productCombo.addItem(p);
            }
        }
    }

    public static boolean isPlaceholder(String s) {
        return s == null || s.isEmpty() || PLACEHOLDER.equals(s);
    }
}
