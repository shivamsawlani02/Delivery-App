package com.dsdairysystem.deliveryapp.query;

public class Query {
    String date,clientName,description,clientMobile,OrderID,documentID;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Query(String date, String clientName, String description, String clientMobile, String orderID, String documentID) {
        this.date = date;
        this.clientName = clientName;
        this.description = description;
        this.clientMobile = clientMobile;
        OrderID = orderID;
        this.documentID = documentID;
    }

    public String getClientMobile() {
        return clientMobile;
    }

    public void setClientMobile(String clientMobile) {
        this.clientMobile = clientMobile;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
