package com.example.a402413138_mad700_pantrymanagerassignment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

/**
 * Form Activity for adding new pantry ingredients or editing existing ones.
 * Provides validation for name, positive numerical quantity, unit string, and valid date formats.
 */
public class IngredientFormActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText name, quantity, unit, expiry;
    private long itemId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        db = new DatabaseHelper(this);

        name = findViewById(R.id.editName);
        quantity = findViewById(R.id.editQuantity);
        unit = findViewById(R.id.editUnit);
        expiry = findViewById(R.id.editExpiry);

        // Adjust unit input field hint based on user unit preference setting
        android.content.SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean useMetric = prefs.getBoolean("use_south_african_units", true);
        unit.setHint(useMetric ? "Unit (e.g. g, ml, piece, kg)" : "Unit (e.g. oz, fl oz, cup, lb)");

        name.requestFocus();

        // Configure DatePickerDialog on tap for the expiry field
        expiry.setFocusable(false);
        expiry.setClickable(true);
        expiry.setOnClickListener(v -> showDatePicker());

        // Load existing ingredient details if editing an item
        if (getIntent().hasExtra("id")) {
            itemId = getIntent().getLongExtra("id", 0);

            TextView title = findViewById(R.id.txtFormTitle);
            title.setText("Edit ingredient");

            name.setText(getIntent().getStringExtra("name"));
            quantity.setText(String.valueOf(getIntent().getDoubleExtra("quantity", 0)));
            unit.setText(getIntent().getStringExtra("unit"));
            expiry.setText(getIntent().getStringExtra("expiry"));
        }

        Button saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(v -> save());
    }

    /**
     * Displays a DatePickerDialog for selecting the ingredient expiration date.
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // If a date is already filled, parse it to pre-select in the date picker
        String currentDate = expiry.getText().toString().trim();
        if (currentDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                year = Integer.parseInt(currentDate.substring(0, 4));
                month = Integer.parseInt(currentDate.substring(5, 7)) - 1;
                day = Integer.parseInt(currentDate.substring(8, 10));

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setLenient(false);
                selectedDate.set(year, month, day);
                selectedDate.getTimeInMillis();
            } catch (IllegalArgumentException ex) {
                // Revert to current date if parsed date is invalid
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                IngredientFormActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String expiryDate = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );
                    expiry.setText(expiryDate);
                    expiry.setError(null);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    /**
     * Validates input fields and saves the pantry item to SQLite.
     */
    private void save() {
        String n = name.getText().toString().trim();
        String q = quantity.getText().toString().trim();
        String u = unit.getText().toString().trim();
        String e = expiry.getText().toString().trim();

        // Field validations
        if (n.isEmpty()) {
            name.setError("Enter an ingredient name");
            return;
        }

        if (q.isEmpty()) {
            quantity.setError("Enter a quantity");
            return;
        }

        double qty;
        try {
            qty = Double.parseDouble(q);
        } catch (NumberFormatException ex) {
            quantity.setError("Enter a valid number");
            return;
        }

        if (!Double.isFinite(qty) || qty <= 0) {
            quantity.setError("Quantity must be greater than zero");
            return;
        }

        if (u.isEmpty()) {
            unit.setError("Enter a unit");
            return;
        }

        if (!e.isEmpty()) {
            if (!e.matches("\\d{4}-\\d{2}-\\d{2}")) {
                expiry.setError("Select a valid expiry date");
                return;
            }

            // Validate calendar date correctness (e.g. leap years, valid months)
            try {
                Calendar validation = Calendar.getInstance();
                validation.setLenient(false);
                validation.set(
                        Integer.parseInt(e.substring(0, 4)),
                        Integer.parseInt(e.substring(5, 7)) - 1,
                        Integer.parseInt(e.substring(8, 10))
                );
                validation.getTimeInMillis();
            } catch (IllegalArgumentException ex) {
                expiry.setError("Select a valid expiry date");
                return;
            }
        }

        // Save to database
        long result = db.savePantryItem(new PantryItem(itemId, n, qty, u, e));

        if (result < 0) {
            Toast.makeText(this, "Could not save ingredient", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
