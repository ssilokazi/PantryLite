package com.example.smartpantrymanager;

/**
 * Represents one ingredient record saved in the pantry database.
 * Each record keeps its own quantity and unit.
 */
public class PantryItem {

    public long id;
    public String name;
    public double quantity;
    public String unit;

    // Optional expiry date stored as yyyy-MM-dd; empty means not supplied.
    public String expiryDate;

    public PantryItem(long id, String name, double quantity,
                      String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}