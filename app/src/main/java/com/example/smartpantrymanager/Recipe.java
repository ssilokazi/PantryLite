package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe and the complete list of ingredients it requires.
 * Recipes and their requirements will be loaded from SQLite.
 */
public class Recipe {

    public long id;
    public String name;
    public String instructions;

    // Every requirement must be satisfied before this recipe is suggested.
    public final List<Requirement> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    /**
     * Stores the amount of one ingredient needed by a recipe.
     * A static nested class groups this information with its parent model.
     */
    public static class Requirement {

        public String name;
        public double quantity;
        public String unit;

        public Requirement(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}