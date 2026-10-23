package com.example.smartpantrymanager;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Shared screen structure with a title and bottom navigation.
 * Layouts are built with standard Android Views in Java.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected static final int GREEN = Color.rgb(36, 92, 64);
    protected static final int INK = Color.rgb(23, 61, 42);
    protected static final int BACKGROUND = Color.rgb(245, 247, 242);

    protected int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    protected LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(12), dp(16), dp(12));
        return layout;
    }

    protected TextView text(String value, int size) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(INK);
        view.setPadding(0, dp(8), 0, dp(8));
        return view;
    }

    protected Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(INK);
        button.setAllCaps(false);
        button.setMinHeight(dp(48));
        return button;
    }

    /**
     * Places content between a heading and navigation bar.
     * Window insets keep controls clear of Android's system bars.
     */
    protected void showScreen(View content, String title) {
        EdgeToEdge.enable(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BACKGROUND);

        TextView heading = text(title, 20);
        heading.setTypeface(null, android.graphics.Typeface.BOLD);
        heading.setPadding(dp(16), dp(10), dp(16), dp(10));
        root.addView(heading);

        root.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setPadding(dp(4), 0, dp(4), dp(4));

        addNavigationButton(navigation, "Pantry", MainActivity.class);
        addNavigationButton(navigation, "Recipes", RecipesActivity.class);
        addNavigationButton(navigation, "Settings", SettingsActivity.class);

        root.addView(navigation);
        setContentView(root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void addNavigationButton(
            LinearLayout navigation, String label,
            Class<? extends AppCompatActivity> destination) {

        Button item = button(label);
        item.setTextSize(12);

        // Disable the current destination to avoid reopening the same screen.
        item.setEnabled(!getClass().equals(destination));

        item.setOnClickListener(view -> {
            Intent intent = new Intent(this, destination);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            // Keep the pantry as the root screen without stacking tabs.
            if (!(this instanceof MainActivity)) {
                finish();
            }
        });

        navigation.addView(item, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
    }
}