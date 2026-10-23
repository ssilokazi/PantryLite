package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Saves a default unit using SharedPreferences.
 * The preference applies to new ingredients; editing preserves stored units.
 */
public class SettingsActivity extends BaseActivity {

    public static final String PREFERENCES = "pantry_settings";
    public static final String KEY_DEFAULT_UNIT = "default_unit";

    private static final String[] UNITS = {
            "piece", "g", "kg", "ml", "l", "slice", "clove"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(
                PREFERENCES, MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = column();
        scroll.addView(content);

        content.addView(text("Make it yours", 28));
        content.addView(text(
                "Choose the unit selected automatically when adding " +
                        "a new ingredient.", 17));

        content.addView(text("Default ingredient unit", 18));

        Spinner unitSpinner = new Spinner(this);
        // Stable ID allows Android to restore unsaved selection on rotation.
        unitSpinner.setId(0x00A10001);

        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS);
        unitsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitsAdapter);

        String savedUnit = preferences.getString(
                KEY_DEFAULT_UNIT, "piece");

        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(savedUnit)) {
                unitSpinner.setSelection(i);
                break;
            }
        }

        content.addView(unitSpinner, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56)));

        Button save = button("Save settings");
        content.addView(save);

        save.setOnClickListener(view -> {
            preferences.edit()
                    .putString(KEY_DEFAULT_UNIT,
                            unitSpinner.getSelectedItem().toString())
                    .apply();

            Toast.makeText(this, "Settings saved.",
                    Toast.LENGTH_SHORT).show();
        });

        content.addView(text("How matching works", 22));
        content.addView(text(
                "Suggestions require every ingredient in sufficient " +
                        "quantity. Common plurals are recognised, kilograms " +
                        "convert to grams, and litres convert to millilitres. " +
                        "Weight cannot be converted to volume automatically.", 16));

        content.addView(text("Your data", 22));
        content.addView(text(
                "Pantry items and recipes are stored locally in SQLite. " +
                        "Settings are stored on this device. No account, internet " +
                        "connection or location access is required.", 16));

        content.addView(text("Expiry dates", 22));
        content.addView(text(
                "Expiry dates are displayed for your reference. The " +
                        "current matcher checks names, units and quantities; " +
                        "it does not filter ingredients by expiry date.", 16));

        showScreen(scroll, "Settings");
    }
}