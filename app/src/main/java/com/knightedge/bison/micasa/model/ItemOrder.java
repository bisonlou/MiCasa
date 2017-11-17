package com.knightedge.bison.micasa.model;

/**
 * Created by Bison on 25/09/2017.
 */
public class ItemOrder {
    String date;
    double quantity;
    int priority;

    public ItemOrder() {
    }

    public ItemOrder(String date, double quantity, int priority) {
        this.date = date;
        this.quantity = quantity;
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPriority() {
        return priority;
    }
}
