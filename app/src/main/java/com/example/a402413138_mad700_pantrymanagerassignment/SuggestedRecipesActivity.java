package com.example.a402413138_mad700_pantrymanagerassignment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Activity presenting recipe suggestions categorized into:
 * 1. Strict Matches (Recipes you can make right now with available ingredients).
 * 2. Almost There Matches (Recipes missing only one ingredient requirement).
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        db = new DatabaseHelper(this);

        RecyclerView strictRecycler = findViewById(R.id.recipeRecycler);
        RecyclerView almostRecycler = findViewById(R.id.almostThereRecycler);

        strictRecycler.setLayoutManager(new LinearLayoutManager(this));
        almostRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Disable nested scrolling on internal RecyclerViews since parent is inside a ScrollView
        strictRecycler.setNestedScrollingEnabled(false);
        almostRecycler.setNestedScrollingEnabled(false);

        TextView strictEmpty = findViewById(R.id.txtEmpty);
        TextView almostEmpty = findViewById(R.id.txtAlmostThereEmpty);

        // Load recipes for both categories
        loadRecipes(
                strictRecycler,
                strictEmpty,
                db.getStrictMatches(),
                "No recipes can be made with your current pantry yet."
        );

        loadRecipes(
                almostRecycler,
                almostEmpty,
                db.getAlmostThereMatches(),
                "No recipes are missing exactly one ingredient right now."
        );
    }

    /**
     * Binds recipe list to specified RecyclerView and handles empty view visibility.
     */
    private void loadRecipes(
            RecyclerView recycler,
            TextView empty,
            List<Recipe> recipes,
            String emptyMessage) {

        recycler.setAdapter(new RecipeAdapter(recipes));

        empty.setText(emptyMessage);
        empty.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh suggestions when returning from another screen
        if (db != null) {
            refreshRecipes();
        }
    }

    /**
     * Re-queries database and updates both recipe lists.
     */
    private void refreshRecipes() {
        RecyclerView strictRecycler = findViewById(R.id.recipeRecycler);
        RecyclerView almostRecycler = findViewById(R.id.almostThereRecycler);

        TextView strictEmpty = findViewById(R.id.txtEmpty);
        TextView almostEmpty = findViewById(R.id.txtAlmostThereEmpty);

        loadRecipes(
                strictRecycler,
                strictEmpty,
                db.getStrictMatches(),
                "No recipes can be made with your current pantry yet."
        );

        loadRecipes(
                almostRecycler,
                almostEmpty,
                db.getAlmostThereMatches(),
                "No recipes are missing exactly one ingredient right now."
        );
    }
}
