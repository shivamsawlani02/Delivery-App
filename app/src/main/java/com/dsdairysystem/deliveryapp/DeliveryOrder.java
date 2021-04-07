package com.dsdairysystem.deliveryapp;

import java.util.Map;

public class DeliveryOrder {
    String ID;
    String date;
    Map<String,Object> map;

    public DeliveryOrder(String ID, String date, Map<String,Object> map) {
        this.ID=ID;
        this.date = date;
        this.map = map;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
