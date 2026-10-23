package com.example.smartpantrymanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows strict recipe suggestions by default.
 * A clearly labelled separate browsing mode displays the full collection.
 */
public class RecipesActivity extends BaseActivity {

    private DatabaseHelper database;
    private SwitchCompat browseAll;
    private TextView summary;
    private TextView emptyMessage;
    private ArrayAdapter<String> adapter;

    // Positions in this list correspond to positions in the visible adapter.
    private List<Recipe> displayedRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHelper(this);

        LinearLayout content = column();

        browseAll = new SwitchCompat(this);
        browseAll.setText("Browse all recipes (not just suggestions)");
        browseAll.setTextColor(INK);
        browseAll.setMinHeight(dp(56));
        content.addView(browseAll);

        summary = text("", 16);
        content.addView(summary);

        emptyMessage = text(
                "No recipes match your pantry yet. Add the required " +
                        "ingredients and quantities, then return here.", 18);
        content.addView(emptyMessage);

        ListView list = new ListView(this);
        list.setDividerHeight(dp(1));

        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,
                new ArrayList<>()) {

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                TextView row = (TextView) super.getView(
                        position, convertView, parent);
                row.setTextColor(INK);
                row.setTextSize(18);
                row.setBackgroundColor(Color.WHITE);
                row.setPadding(dp(14), dp(18), dp(14), dp(18));
                return row;
            }
        };

        list.setAdapter(adapter);
        list.setEmptyView(emptyMessage);

        content.addView(list, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        showScreen(content, "Recipes");

        if (savedInstanceState != null) {
            browseAll.setChecked(
                    savedInstanceState.getBoolean("browse_all", false));
        }

        browseAll.setOnCheckedChangeListener(
                (buttonView, checked) -> loadRecipes());

        // Intent extras connect list selection to the detail screen.
        list.setOnItemClickListener((parent, view, position, id) -> {
            Recipe recipe = displayedRecipes.get(position);
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recompute suggestions whenever the screen becomes visible.
        loadRecipes();
    }

    private void loadRecipes() {
        List<Recipe> allRecipes = database.getRecipes();

        if (browseAll.isChecked()) {
            displayedRecipes = allRecipes;
            summary.setText("Recipe collection: " + allRecipes.size()
                    + " recipes. These may require ingredients you lack.");
        } else {
            displayedRecipes = RecipeMatcher.findMatches(
                    allRecipes, database.getPantryItems());

            summary.setText("Suggested recipes: " + displayedRecipes.size()
                    + ". Every required ingredient and quantity is available.");
        }

        adapter.clear();

        for (Recipe recipe : displayedRecipes) {
            adapter.add(recipe.name);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("browse_all", browseAll.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}