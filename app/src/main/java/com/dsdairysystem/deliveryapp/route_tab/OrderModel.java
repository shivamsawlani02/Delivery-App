package com.dsdairysystem.deliveryapp.route_tab;

import java.util.Date;
import java.util.Map;

public class OrderModel {
    String Time,Date;
    java.util.Date timestamp;

    int Amount;
    Map<String,Long> Milk;


    public OrderModel() {
    }


    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.util.Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public Map<String, Long> getMilk() {
        return Milk;
    }

    public void setMilk(Map<String, Long> milk) {
        Milk = milk;
    }

    public OrderModel(String time, String date, java.util.Date timestamp, int amount, Map<String, Long> milk) {
        Time = time;
        Date = date;
        this.timestamp = timestamp;
        Amount = amount;
        Milk = milk;
    }
}
