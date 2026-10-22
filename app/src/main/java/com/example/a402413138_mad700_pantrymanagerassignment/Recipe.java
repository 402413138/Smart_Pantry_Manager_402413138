package com.example.a402413138_mad700_pantrymanagerassignment;

public class Recipe {
    public long id;
    public String name, ingredientsJson, method;
    public String almostThereMessage = "";

    public Recipe(long id, String name, String ingredientsJson, String method) {
        this.id = id;
        this.name = name;
        this.ingredientsJson = ingredientsJson;
        this.method = method;
    }
}
