package com.example.a402413138_mad700_pantrymanagerassignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        Switch expiry = findViewById(R.id.switchExpiry);
        expiry.setChecked(prefs.getBoolean("expiry_reminder", true));
        expiry.setOnCheckedChangeListener((button, checked) ->
            prefs.edit().putBoolean("expiry_reminder", checked).apply()
        );

        Switch darkMode = findViewById(R.id.switchDarkMode);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);
        darkMode.setChecked(isDarkMode);
        darkMode.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean("dark_mode", checked).apply();
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        Switch unitSwitch = findViewById(R.id.switchUnitSystem);
        boolean useMetric = prefs.getBoolean("use_south_african_units", true);
        unitSwitch.setChecked(useMetric);
        unitSwitch.setText(useMetric ? "Unit system: South African (Metric)" : "Unit system: American (Imperial)");

        unitSwitch.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean("use_south_african_units", checked).apply();
            unitSwitch.setText(checked ? "Unit system: South African (Metric)" : "Unit system: American (Imperial)");
        });
    }
}
