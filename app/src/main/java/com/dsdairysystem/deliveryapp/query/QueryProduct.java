package com.dsdairysystem.deliveryapp.query;

public class QueryProduct {
    String quality,quantity;

    public QueryProduct(String quality, String quantity) {
        this.quality = quality;
        this.quantity = quantity;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
