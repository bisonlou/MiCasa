package com.knightedge.bison.micasa.model;

/**
 * Created by Bison on 22/09/2017.
 */
public class InventoryItem {

    String units;
    double unitPrice;

    public InventoryItem() {
    }

    public InventoryItem(String units, double unitPrice) {
        this.units = units;
        this.unitPrice = unitPrice;
    }

    public String getUnits() {
        return this.units;
    }

    public double getUnitPrice() {
        return this.unitPrice;
    }


}
