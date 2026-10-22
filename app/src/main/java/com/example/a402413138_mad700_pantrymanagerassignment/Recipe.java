package com.example.a402413138_mad700_pantrymanagerassignment;

/**
 * Model class representing a recipe stored in the database.
 */
public class Recipe {
    public long id;
    public String name;
    public String ingredientsJson;
    public String method;
    public String almostThereMessage = "";

    /**
     * Constructs a new Recipe.
     *
     * @param id              Unique database ID
     * @param name            Name of the recipe
     * @param ingredientsJson JSON array string listing required ingredients
     * @param method          Step-by-step cooking instructions
     */
    public Recipe(long id, String name, String ingredientsJson, String method) {
        this.id = id;
        this.name = name;
        this.ingredientsJson = ingredientsJson;
        this.method = method;
    }
}
