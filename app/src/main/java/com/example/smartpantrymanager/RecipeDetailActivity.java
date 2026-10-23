package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Collections;

/**
 * Displays a recipe's full ingredient list and preparation instructions.
 * Availability is calculated from the current pantry, not assumed.
 */
public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private DatabaseHelper database;
    private Recipe recipe;
    private TextView availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHelper(this);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        recipe = database.getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe could not be found.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = column();
        scroll.addView(content);

        TextView name = text(recipe.name, 28);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(name);

        availability = text("", 16);
        content.addView(availability);

        TextView ingredientsHeading = text("Required ingredients", 21);
        ingredientsHeading.setTypeface(
                null, android.graphics.Typeface.BOLD);
        content.addView(ingredientsHeading);

        DecimalFormat format = new DecimalFormat("0.###");

        for (Recipe.Requirement ingredient : recipe.ingredients) {
            content.addView(text(
                    "\u2022 " + ingredient.name + " — "
                            + format.format(ingredient.quantity)
                            + " " + ingredient.unit, 18));
        }

        TextView methodHeading = text("Preparation", 21);
        methodHeading.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(methodHeading);
        content.addView(text(recipe.instructions, 18));

        content.addView(text(
                "Quantities are for one recipe batch. Viewing a recipe " +
                        "does not deduct ingredients from your pantry.", 14));

        Button back = button("Back to recipe list");
        back.setOnClickListener(view -> finish());
        content.addView(back);

        showScreen(scroll, "Recipe details");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recipe == null) {
            return;
        }

        boolean canMake = !RecipeMatcher.findMatches(
                Collections.singletonList(recipe),
                database.getPantryItems()).isEmpty();

        availability.setText(canMake
                ? "Available: your pantry contains all required quantities."
                : "Not currently available: an ingredient is missing, " +
                "insufficient, or recorded in an incompatible unit.");
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}