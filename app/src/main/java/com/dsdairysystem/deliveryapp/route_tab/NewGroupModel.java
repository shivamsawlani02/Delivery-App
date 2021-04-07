package com.dsdairysystem.deliveryapp.route_tab;

import java.util.List;
import java.util.Map;

public class NewGroupModel {
    public int priority;
    public List<Map<String,Object>> Client;

    public NewGroupModel() {
    }


    public NewGroupModel(int priority, List<Map<String, Object>> Client) {
        this.priority = priority;
        this.Client = Client;
    }
}
