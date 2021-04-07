package com.dsdairysystem.deliveryapp.add_client;

public class Milkman {
    String Name,Mobile,type;
    Long Due_Amount;

    public Milkman(String Name, String Mobile, String type) {
        this.Name = Name;
        this.Mobile = Mobile;
        this.type=type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        this.Mobile = mobile;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
