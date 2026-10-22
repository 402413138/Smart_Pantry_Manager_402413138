package com.example.a402413138_mad700_pantrymanagerassignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Main activity displaying the list of pantry items, ingredient count summary,
 * and navigation controls to add ingredients, view recipe suggestions, or modify settings.
 */
public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private RecyclerView recycler;
    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper and UI elements
        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.pantryRecycler);
        summary = findViewById(R.id.txtSummary);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Navigation button listeners
        findViewById(R.id.btnAdd).setOnClickListener(v -> 
            startActivity(new Intent(this, IngredientFormActivity.class)));
        findViewById(R.id.btnSuggestions).setOnClickListener(v -> 
            startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v -> 
            startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the pantry list whenever returning to this screen
        refreshPantry();
    }

    /**
     * Queries pantry items from SQLite and populates the RecyclerView adapter and summary count.
     */
    private void refreshPantry() {
        List<PantryItem> items = db.getPantryItems();
        summary.setText("Pantry ingredients: " + items.size());

        recycler.setAdapter(new PantryAdapter(items, db, new PantryAdapter.Listener() {
            @Override
            public void onEdit(PantryItem item) {
                // Open form pre-populated with item details for editing
                Intent i = new Intent(MainActivity.this, IngredientFormActivity.class);
                i.putExtra("id", item.id);
                i.putExtra("name", item.name);
                i.putExtra("quantity", item.quantity);
                i.putExtra("unit", item.unit);
                i.putExtra("expiry", item.expiry);
                startActivity(i);
            }

            @Override
            public void onChanged() {
                // Refresh list when an item is deleted
                refreshPantry();
            }
        }));
    }
}
