package com.example.a402413138_mad700_pantrymanagerassignment;

/**
 * Model class representing an ingredient stored in the user's pantry.
 */
public class PantryItem {
    public long id;
    public String name;
    public String unit;
    public String expiry;
    public double quantity;

    /**
     * Constructs a new PantryItem.
     *
     * @param id       Unique database ID (0 if new)
     * @param name     Name of the ingredient
     * @param quantity Available quantity
     * @param unit     Unit of measurement (e.g. g, ml, piece)
     * @param expiry   Expiration date formatted as YYYY-MM-DD (optional)
     */
    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }
}
