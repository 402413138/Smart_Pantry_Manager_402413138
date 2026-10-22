
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

        android.content.SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean useMetric = prefs.getBoolean("use_south_african_units", true);
        unit.setHint(useMetric ? "Unit (e.g. g, ml, piece, kg)" : "Unit (e.g. oz, fl oz, cup, lb)");

        name.requestFocus();

        // Open the date picker when the expiry field is tapped.
        expiry.setFocusable(false);
        expiry.setClickable(true);

        expiry.setOnClickListener(v -> showDatePicker());

        // Load existing ingredient details when editing.
        if (getIntent().hasExtra("id")) {
            itemId = getIntent().getLongExtra("id", 0);

            TextView title = findViewById(R.id.txtFormTitle);
            title.setText("Edit ingredient");

            name.setText(getIntent().getStringExtra("name"));

            quantity.setText(String.valueOf(
                    getIntent().getDoubleExtra("quantity", 0)
            ));

            unit.setText(getIntent().getStringExtra("unit"));
            expiry.setText(getIntent().getStringExtra("expiry"));
        }

        Button saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // Default to today's date.
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // If a date is already selected, use it as the initial date.
        String currentDate = expiry.getText().toString().trim();

        if (currentDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                year = Integer.parseInt(currentDate.substring(0, 4));
                month = Integer.parseInt(currentDate.substring(5, 7)) - 1;
                day = Integer.parseInt(currentDate.substring(8, 10));

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setLenient(false);
                selectedDate.set(year, month, day);

                // Validate the existing date.
                selectedDate.getTimeInMillis();

            } catch (IllegalArgumentException ex) {
                // If the stored date is invalid, use today's date.
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

    private void save() {
        String n = name.getText().toString().trim();
        String q = quantity.getText().toString().trim();
        String u = unit.getText().toString().trim();
        String e = expiry.getText().toString().trim();

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

            // Validate the actual calendar date, not just its format.
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

        long result = db.savePantryItem(
                new PantryItem(itemId, n, qty, u, e)
        );

        if (result < 0) {
            Toast.makeText(
                    this,
                    "Could not save ingredient",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Toast.makeText(
                this,
                "Ingredient saved",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}
