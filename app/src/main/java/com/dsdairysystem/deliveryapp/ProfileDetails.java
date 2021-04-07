package com.dsdairysystem.deliveryapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ProfileDetails {
    String username;
    String email;
    String phone;
    String password;
    String device_token;
    @ServerTimestamp
    Date date;


    public ProfileDetails(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ProfileDetails(String username, String email, String phone,String device_token){
        this.email=email;
        this.phone=phone;
        this.username=username;
        this.device_token=device_token;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
