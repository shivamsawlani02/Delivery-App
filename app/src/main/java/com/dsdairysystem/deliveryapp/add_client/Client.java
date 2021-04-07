package com.dsdairysystem.deliveryapp.add_client;

import java.util.Map;

public class Client {
    String name,mobile,address;
    Map<String,Object> Milk;

    public Client(String name, String mobile, String address, Map<String,Object> Milk) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.Milk=Milk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Object> getMilk() {
        return Milk;
    }

    public void setMilk(Map<String, Object> Milk) {
        Milk = Milk;
    }
}
