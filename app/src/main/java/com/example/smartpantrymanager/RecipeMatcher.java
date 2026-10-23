package com.example.smartpantrymanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Suggests a recipe only when every ingredient is available
 * in sufficient quantity and in compatible units.
 *
 * This class contains plain Java logic, independent of Android screens.
 */
public final class RecipeMatcher {

    private RecipeMatcher() {
        // Utility class: instances are not required.
    }

    /**
     * Returns only fully matching recipes.
     * Checking suggestions does not consume or change pantry stock.
     */
    public static List<Recipe> findMatches(
            List<Recipe> recipes, List<PantryItem> pantry) {

        Map<String, BigDecimal> available = new HashMap<>();

        // Combine duplicate pantry entries after normalising names and units.
        for (PantryItem item : pantry) {
            if (!validQuantity(item.quantity)) {
                continue;
            }

            String name = normaliseName(item.name);
            String unit = normaliseUnit(item.unit);

            if (name.isEmpty() || unit.isEmpty()) {
                continue;
            }

            String key = name + "|" + baseUnit(unit);
            BigDecimal amount = baseQuantity(item.quantity, unit);

            available.put(key,
                    available.getOrDefault(key, BigDecimal.ZERO)
                            .add(amount));
        }

        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (canMake(recipe, available)) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    /**
     * Aggregate recipe requirements before comparing them with stock.
     * This prevents duplicate ingredient rows from reusing the same stock.
     */
    private static boolean canMake(
            Recipe recipe, Map<String, BigDecimal> available) {

        if (recipe.ingredients.isEmpty()) {
            return false;
        }

        Map<String, BigDecimal> required = new HashMap<>();

        for (Recipe.Requirement ingredient : recipe.ingredients) {
            if (!validQuantity(ingredient.quantity)) {
                return false;
            }

            String name = normaliseName(ingredient.name);
            String unit = normaliseUnit(ingredient.unit);

            if (name.isEmpty() || unit.isEmpty()) {
                return false;
            }

            String key = name + "|" + baseUnit(unit);
            BigDecimal amount = baseQuantity(ingredient.quantity, unit);

            required.put(key,
                    required.getOrDefault(key, BigDecimal.ZERO)
                            .add(amount));
        }

        // One missing ingredient or insufficient quantity rejects the recipe.
        for (Map.Entry<String, BigDecimal> entry : required.entrySet()) {
            BigDecimal stock = available.getOrDefault(
                    entry.getKey(), BigDecimal.ZERO);

            if (stock.compareTo(entry.getValue()) < 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean validQuantity(double quantity) {
        return !Double.isNaN(quantity)
                && !Double.isInfinite(quantity)
                && quantity > 0;
    }

    /**
     * Handles capital letters, extra whitespace and common plurals.
     * Explicit aliases avoid unsafe guesses about unrelated ingredients.
     * Raw and cooked ingredients remain distinct.
     */
    public static String normaliseName(String value) {
        if (value == null) {
            return "";
        }

        String name = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (name) {
            case "eggs":
                return "egg";
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "cooked potatoes":
                return "cooked potato";
            case "bananas":
                return "banana";
            case "apples":
                return "apple";
            case "carrots":
                return "carrot";
            case "cucumbers":
                return "cucumber";
            case "onions":
                return "onion";
            case "garlic clove":
            case "garlic cloves":
                return "garlic";
            case "yogurt":
                return "yoghurt";
            case "cooked chickpea":
                return "cooked chickpeas";
            case "cooked pea":
                return "cooked peas";
            default:
                return name;
        }
    }

    // Convert different spellings of the same unit into one identifier.
    private static String normaliseUnit(String value) {
        if (value == null) {
            return "";
        }

        String unit = value.trim().toLowerCase(Locale.ROOT);

        switch (unit) {
            case "gram":
            case "grams":
                return "g";
            case "kilogram":
            case "kilograms":
                return "kg";
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";
            case "pieces":
            case "pc":
            case "pcs":
                return "piece";
            case "slices":
                return "slice";
            case "cloves":
                return "clove";
            default:
                return unit;
        }
    }

    /**
     * Only compatible units share a comparison key.
     * Grams cannot match millilitres, pieces, slices or cloves.
     */
    private static String baseUnit(String unit) {
        switch (unit) {
            case "kg":
                return "g";
            case "l":
                return "ml";
            default:
                return unit;
        }
    }

    /**
     * Convert kilograms to grams and litres to millilitres.
     * Decimal arithmetic avoids rounding errors when combining quantities.
     */
    private static BigDecimal baseQuantity(double quantity, String unit) {
        BigDecimal amount = BigDecimal.valueOf(quantity);

        if (unit.equals("kg") || unit.equals("l")) {
            return amount.multiply(BigDecimal.valueOf(1000));
        }

        return amount;
    }
}