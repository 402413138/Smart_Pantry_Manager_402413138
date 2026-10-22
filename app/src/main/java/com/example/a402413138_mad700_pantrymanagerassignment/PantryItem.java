package com.example.a402413138_mad700_pantrymanagerassignment;

public class PantryItem {
    public long id;
    public String name, unit, expiry;
    public double quantity;
    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id=id; this.name=name; this.quantity=quantity; this.unit=unit; this.expiry=expiry;
    }
}
