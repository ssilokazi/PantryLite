package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Inserts twenty simple recipes when SQLite creates the database.
 * All ingredients used in the instructions are explicitly required.
 * The user's pantry starts empty; recipes do not create pantry stock.
 */
public final class RecipeSeeder {

    private RecipeSeeder() {
        // Utility class: no instances are needed.
    }

    public static void seed(SQLiteDatabase db) {

        add(db, "Scrambled Eggs",
                "1. Beat the eggs with the milk.\n" +
                        "2. Melt the butter in a pan over low heat.\n" +
                        "3. Add the eggs and stir until fully set.",
                "egg|2|piece",
                "milk|30|ml",
                "butter|10|g");

        add(db, "Cheese Omelette",
                "1. Beat the eggs and grate the cheese.\n" +
                        "2. Melt the butter in a non-stick pan.\n" +
                        "3. Add the eggs and cook gently.\n" +
                        "4. Add the cheese, fold and cook until the eggs are set.",
                "egg|2|piece",
                "cheese|40|g",
                "butter|10|g");

        add(db, "Tomato Toast",
                "1. Toast the bread.\n" +
                        "2. Slice the tomato.\n" +
                        "3. Top the toast with tomato and drizzle with olive oil.",
                "bread|2|slice",
                "tomato|1|piece",
                "olive oil|5|ml");

        add(db, "Grilled Cheese",
                "1. Butter the outside of both bread slices.\n" +
                        "2. Place the cheese between the slices.\n" +
                        "3. Cook in a pan on both sides until golden and melted.",
                "bread|2|slice",
                "cheese|50|g",
                "butter|10|g");

        add(db, "Banana Pancakes",
                "1. Mash the banana and beat in the eggs.\n" +
                        "2. Heat the oil in a non-stick pan.\n" +
                        "3. Spoon in small portions.\n" +
                        "4. Cook both sides until firm and cooked through.",
                "banana|1|piece",
                "egg|2|piece",
                "oil|5|ml");

        add(db, "Banana Porridge",
                "1. Combine the oats and milk in a saucepan.\n" +
                        "2. Simmer gently, stirring until the oats soften.\n" +
                        "3. Mash or slice the banana and stir it in.",
                "oats|50|g",
                "milk|250|ml",
                "banana|1|piece");

        add(db, "Yoghurt Banana Bowl",
                "1. Spoon the yoghurt into a bowl.\n" +
                        "2. Slice the banana and place it on top.\n" +
                        "3. Drizzle with honey.",
                "yoghurt|150|g",
                "banana|1|piece",
                "honey|10|ml");

        add(db, "Apple Oat Bowl",
                "1. Chop the apple into small pieces.\n" +
                        "2. Mix the yoghurt and oats.\n" +
                        "3. Add the apple and allow the oats to soften.",
                "apple|1|piece",
                "oats|40|g",
                "yoghurt|150|g");

        add(db, "Tomato Cucumber Salad",
                "1. Chop the tomatoes and cucumber.\n" +
                        "2. Combine them in a bowl.\n" +
                        "3. Toss with the olive oil.",
                "tomato|2|piece",
                "cucumber|1|piece",
                "olive oil|10|ml");

        add(db, "Carrot Apple Salad",
                "1. Grate the carrot and apple.\n" +
                        "2. Combine them in a bowl.\n" +
                        "3. Stir through the lemon juice.",
                "carrot|1|piece",
                "apple|1|piece",
                "lemon juice|10|ml");

        add(db, "Egg Fried Rice",
                "1. Heat the oil in a pan.\n" +
                        "2. Add the beaten eggs and stir until set.\n" +
                        "3. Add the cooked rice and soy sauce.\n" +
                        "4. Stir-fry until steaming hot throughout.",
                "cooked rice|200|g",
                "egg|2|piece",
                "oil|10|ml",
                "soy sauce|10|ml");

        add(db, "Pea Fried Rice",
                "1. Heat the oil in a pan.\n" +
                        "2. Add the cooked peas and cooked rice.\n" +
                        "3. Stir in the soy sauce.\n" +
                        "4. Stir-fry until steaming hot throughout.",
                "cooked rice|200|g",
                "cooked peas|100|g",
                "oil|10|ml",
                "soy sauce|10|ml");

        add(db, "Tomato Pasta",
                "1. Chop the tomatoes and garlic.\n" +
                        "2. Heat the olive oil and gently fry the garlic.\n" +
                        "3. Add the tomatoes and cook until soft.\n" +
                        "4. Add the cooked pasta and heat thoroughly.",
                "cooked pasta|200|g",
                "tomato|2|piece",
                "garlic|1|clove",
                "olive oil|10|ml");

        add(db, "Cheesy Pasta",
                "1. Warm the milk and butter gently in a saucepan.\n" +
                        "2. Stir in the grated cheese until melted.\n" +
                        "3. Add the cooked pasta and heat thoroughly.",
                "cooked pasta|200|g",
                "milk|50|ml",
                "butter|10|g",
                "cheese|50|g");

        add(db, "Garlic Bread",
                "1. Finely chop the garlic and mix with softened butter.\n" +
                        "2. Spread the mixture over the bread.\n" +
                        "3. Bake at 180 degrees Celsius until golden.",
                "bread|2|slice",
                "garlic|1|clove",
                "butter|20|g");

        add(db, "Mashed Potato",
                "1. Mash the cooked potato.\n" +
                        "2. Add the milk and butter.\n" +
                        "3. Warm gently while stirring until hot and smooth.",
                "cooked potato|300|g",
                "milk|50|ml",
                "butter|15|g");

        add(db, "Potato Egg Hash",
                "1. Chop the cooked potato and onion.\n" +
                        "2. Heat the oil and fry the onion until softened.\n" +
                        "3. Add the potato and cook until golden.\n" +
                        "4. Add the eggs and stir until fully set.",
                "cooked potato|200|g",
                "onion|1|piece",
                "egg|2|piece",
                "oil|10|ml");

        add(db, "Spinach Omelette",
                "1. Heat the oil and wilt the spinach.\n" +
                        "2. Beat the eggs and pour them into the pan.\n" +
                        "3. Add grated cheese.\n" +
                        "4. Fold and cook until the eggs are fully set.",
                "spinach|50|g",
                "egg|2|piece",
                "cheese|30|g",
                "oil|5|ml");

        add(db, "Peanut Butter Banana Toast",
                "1. Toast the bread.\n" +
                        "2. Spread with peanut butter.\n" +
                        "3. Slice the banana and arrange it on top.",
                "bread|2|slice",
                "peanut butter|30|g",
                "banana|1|piece");

        add(db, "Chickpea Tomato Salad",
                "1. Chop the tomato and cucumber.\n" +
                        "2. Combine with the cooked chickpeas.\n" +
                        "3. Toss with the lemon juice and olive oil.",
                "cooked chickpeas|150|g",
                "tomato|1|piece",
                "cucumber|1|piece",
                "lemon juice|10|ml",
                "olive oil|10|ml");
    }

    /**
     * Inserts one recipe and its related ingredient records.
     * Each ingredient uses the format name|quantity|unit.
     * SQLiteOpenHelper runs onCreate inside a transaction.
     */
    private static void add(SQLiteDatabase db, String name,
                            String instructions, String... ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insertOrThrow(
                "recipes", null, recipeValues);

        for (String ingredient : ingredients) {
            String[] parts = ingredient.split("\\|");

            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Invalid seed ingredient: " + ingredient);
            }

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("name", parts[0]);
            ingredientValues.put("quantity",
                    Double.parseDouble(parts[1]));
            ingredientValues.put("unit", parts[2]);

            db.insertOrThrow(
                    "recipe_ingredients", null, ingredientValues);
        }
    }
}