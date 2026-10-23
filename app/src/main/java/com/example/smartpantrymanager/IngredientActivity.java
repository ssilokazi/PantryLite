package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Handles both adding and editing pantry ingredients.
 * An optional Intent extra identifies the record to edit.
 */
public class IngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private static final String[] UNITS = {
            "piece", "g", "kg", "ml", "l", "slice", "clove"
    };

    private DatabaseHelper database;
    private EditText nameInput;
    private EditText quantityInput;
    private EditText expiryInput;
    private Spinner unitInput;

    // A negative ID means this screen is creating a new record.
    private long itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient);

        // Keep the form above system bars and the on-screen keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, windowInsets) -> {
                    Insets bars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    Insets keyboard = windowInsets.getInsets(
                            WindowInsetsCompat.Type.ime());

                    view.setPadding(bars.left, bars.top, bars.right,
                            Math.max(bars.bottom, keyboard.bottom));
                    return windowInsets;
                });

        database = new DatabaseHelper(this);
        nameInput = findViewById(R.id.editIngredientName);
        quantityInput = findViewById(R.id.editQuantity);
        expiryInput = findViewById(R.id.editExpiry);
        unitInput = findViewById(R.id.spinnerUnit);

        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS);
        unitsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        unitInput.setAdapter(unitsAdapter);

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);

        // Apply the saved default only when creating a new pantry item.
        if (itemId == -1 && savedInstanceState == null) {
            String preferredUnit = getSharedPreferences(
                    SettingsActivity.PREFERENCES, MODE_PRIVATE)
                    .getString(SettingsActivity.KEY_DEFAULT_UNIT, "piece");

            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(preferredUnit)) {
                    unitInput.setSelection(i);
                    break;
                }
            }
        }

        TextView heading = findViewById(R.id.textFormTitle);
        heading.setText(itemId == -1
                ? "Add ingredient" : "Edit ingredient");

        // Avoid overwriting Android's restored form state after rotation.
        if (itemId != -1 && savedInstanceState == null) {
            PantryItem item = database.getPantryItem(itemId);

            if (item == null) {
                Toast.makeText(this, "Ingredient no longer exists.",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            nameInput.setText(item.name);
            quantityInput.setText(String.valueOf(item.quantity));
            expiryInput.setText(item.expiryDate);

            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(item.unit)) {
                    unitInput.setSelection(i);
                    break;
                }
            }
        }

        findViewById(R.id.buttonSave).setOnClickListener(
                view -> saveIngredient());
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();

        nameInput.setError(null);
        quantityInput.setError(null);
        expiryInput.setError(null);

        if (name.isEmpty()) {
            nameInput.setError("Enter an ingredient name.");
            nameInput.requestFocus();
            return;
        }

        double quantity;

        try {
            // Accept a decimal comma as well as a decimal point.
            quantity = Double.parseDouble(quantityText.replace(',', '.'));
        } catch (NumberFormatException exception) {
            quantityInput.setError("Enter a valid quantity.");
            quantityInput.requestFocus();
            return;
        }

        if (Double.isNaN(quantity) || Double.isInfinite(quantity)
                || quantity <= 0 || quantity > 1000000) {
            quantityInput.setError("Enter a quantity above 0 and at most 1000000.");
            quantityInput.requestFocus();
            return;
        }

        if (!expiry.isEmpty() && !validDate(expiry)) {
            expiryInput.setError("Use a real date in yyyy-MM-dd format.");
            expiryInput.requestFocus();
            return;
        }

        String unit = unitInput.getSelectedItem().toString();

        try {
            if (itemId == -1) {
                database.addPantryItem(name, quantity, unit, expiry);
            } else {
                int updated = database.updatePantryItem(
                        itemId, name, quantity, unit, expiry);

                if (updated == 0) {
                    Toast.makeText(this, "Ingredient no longer exists.",
                            Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }

            Toast.makeText(this, "Ingredient saved.",
                    Toast.LENGTH_SHORT).show();

            // Return to the pantry; its onResume reloads the database.
            finish();

        } catch (android.database.SQLException exception) {
            Toast.makeText(this,
                    "Could not save the ingredient. Please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Strict parsing rejects impossible dates such as 2026-02-30.
    private boolean validDate(String value) {
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }

        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        return format.parse(value, position) != null
                && position.getIndex() == value.length();
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}