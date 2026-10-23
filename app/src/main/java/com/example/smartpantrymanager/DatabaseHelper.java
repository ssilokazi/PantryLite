package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the local SQLite database.
 * Pantry records persist after the application closes.
 * Recipes are seeded only when the database is first created.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(),
                DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        // Enforce the relationship between recipes and their ingredients.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Each pantry row represents a quantity of an ingredient.
        db.execSQL(
                "CREATE TABLE pantry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT NOT NULL DEFAULT '')"
        );

        // Recipe instructions are separate from ingredient requirements.
        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        // One recipe can have many required ingredients.
        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) " +
                        "ON DELETE CASCADE)"
        );

        db.execSQL(
                "CREATE INDEX idx_recipe_ingredients_recipe " +
                        "ON recipe_ingredients(recipe_id)"
        );

        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Version 1 is the initial schema.
        // Future versions must add explicit migrations that preserve data.
        throw new IllegalStateException(
                "No database migration defined from " +
                        oldVersion + " to " + newVersion);
    }

    /**
     * Creates the field/value mapping used by both insert and update.
     * ContentValues keeps user input separate from SQL statements.
     */
    private ContentValues pantryValues(String name, double quantity,
                                       String unit, String expiryDate) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name is required.");
        }

        if (Double.isNaN(quantity) || Double.isInfinite(quantity)
                || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be a positive finite number.");
        }

        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit is required.");
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry_date",
                expiryDate == null ? "" : expiryDate.trim());
        return values;
    }

    // CREATE: insert a pantry record and return its generated ID.
    public long addPantryItem(String name, double quantity,
                              String unit, String expiryDate) {
        return getWritableDatabase().insertOrThrow(
                "pantry", null,
                pantryValues(name, quantity, unit, expiryDate));
    }

    // READ: load all pantry records in alphabetical order.
    public List<PantryItem> getPantryItems() {
        List<PantryItem> items = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "pantry", null, null, null,
                null, null, "name COLLATE NOCASE ASC, id ASC")) {

            while (cursor.moveToNext()) {
                items.add(readPantryItem(cursor));
            }
        }

        return items;
    }

    // READ: retrieve one record when opening the edit screen.
    public PantryItem getPantryItem(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                "pantry", null, "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return readPantryItem(cursor);
            }
        }

        // The record may have been deleted since the screen was opened.
        return null;
    }

    // UPDATE: change only the pantry record with the supplied ID.
    public int updatePantryItem(long id, String name, double quantity,
                                String unit, String expiryDate) {
        return getWritableDatabase().update(
                "pantry",
                pantryValues(name, quantity, unit, expiryDate),
                "id = ?",
                new String[]{String.valueOf(id)});
    }

    // DELETE: remove only the selected pantry record.
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(
                "pantry", "id = ?",
                new String[]{String.valueOf(id)});
    }

    // Convert the current database row into a Java model object.
    private PantryItem readPantryItem(Cursor cursor) {
        return new PantryItem(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
        );
    }

    /**
     * Loads the recipe collection with every ingredient requirement.
     * The matching class will filter this collection against the pantry.
     */
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, null, null,
                null, null, "name COLLATE NOCASE ASC")) {

            while (cursor.moveToNext()) {
                Recipe recipe = readRecipe(cursor);
                loadRequirements(recipe);
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    // Load one recipe for its detail screen using an Intent-supplied ID.
    public Recipe getRecipe(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                Recipe recipe = readRecipe(cursor);
                loadRequirements(recipe);
                return recipe;
            }
        }

        return null;
    }

    private Recipe readRecipe(Cursor cursor) {
        return new Recipe(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(
                        cursor.getColumnIndexOrThrow("instructions"))
        );
    }

    // Retrieve the child rows belonging to a single recipe.
    private void loadRequirements(Recipe recipe) {
        try (Cursor cursor = getReadableDatabase().query(
                "recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(recipe.id)},
                null, null, "id ASC")) {

            while (cursor.moveToNext()) {
                recipe.ingredients.add(new Recipe.Requirement(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }
    }
}