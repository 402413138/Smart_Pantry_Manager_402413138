package com.example.a402413138_mad700_pantrymanagerassignment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * RecyclerView Adapter for displaying recipe items in a list.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {
    private final List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Recipe r = recipes.get(pos);
        h.name.setText(r.name);

        // Format required ingredients from JSON string into readable summary text
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray a = new JSONArray(r.ingredientsJson);
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                if (i > 0) sb.append(", ");
                sb.append(o.getString("name"))
                  .append(" (")
                  .append(o.getDouble("quantity"))
                  .append(" ")
                  .append(o.getString("unit"))
                  .append(")");
            }
        } catch (Exception ignored) {}

        h.ingredients.setText(sb.toString());

        // Launch recipe detail activity when card is tapped
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_id", r.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * ViewHolder class holding references to row layout elements.
     */
    static class Holder extends RecyclerView.ViewHolder {
        TextView name, ingredients;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.txtRecipeName);
            ingredients = v.findViewById(R.id.txtRecipeIngredients);
        }
    }
}
