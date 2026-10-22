package com.example.a402413138_mad700_pantrymanagerassignment;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Custom Application class for Smart Pantry Manager.
 * Handles application-level initialization such as applying the saved dark mode theme setting on launch.
 */
public class SmartPantryApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Load theme preferences and apply Night Mode accordingly on startup
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
