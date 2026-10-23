package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * Displays persistent pantry records using a custom adapter.
 * Reloads records when returning from the add/edit screen.
 */
public class MainActivity extends BaseActivity {

    private DatabaseHelper database;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View content = getLayoutInflater().inflate(
                R.layout.activity_main, null, false);
        showScreen(content, "Smart Pantry Manager");

        database = new DatabaseHelper(this);
        adapter = new PantryAdapter(this);

        ListView list = findViewById(R.id.listPantry);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.textEmpty));

        findViewById(R.id.buttonAdd).setOnClickListener(view ->
                startActivity(new Intent(
                        MainActivity.this, IngredientActivity.class)));

        // Pass only the selected record's ID to the editing Activity.
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(
                    MainActivity.this, IngredientActivity.class);
            intent.putExtra(
                    IngredientActivity.EXTRA_ITEM_ID,
                    adapter.getItem(position).id);
            startActivity(intent);
        });

        list.setOnItemLongClickListener((parent, view, position, id) -> {
            PantryItem item = adapter.getItem(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete ingredient?")
                    .setMessage("Remove " + item.name + " from your pantry?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            int deleted = database.deletePantryItem(item.id);
                            loadPantry();

                            Toast.makeText(this,
                                    deleted > 0
                                            ? "Ingredient deleted."
                                            : "Ingredient already removed.",
                                    Toast.LENGTH_SHORT).show();

                        } catch (android.database.SQLException exception) {
                            Toast.makeText(this,
                                    "Could not delete the ingredient.",
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        adapter.updateItems(database.getPantryItems());
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}