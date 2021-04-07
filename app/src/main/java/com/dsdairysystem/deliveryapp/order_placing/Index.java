package com.dsdairysystem.deliveryapp.order_placing;

import java.util.ArrayList;
import java.util.Map;

public class Index {
    public static int INDEX ;
    public static ArrayList<String> arrayList;
    public static Map<String,Long> product_detail_map;
    public static ArrayList<String> product_list;

    public static Long total_amount;
    public static Long totalQuantityRequire;
    public static Long total_amount_new_order;

    public static ArrayList<String> group_order;

    public static ArrayList<String> getGroup_order() {
        return group_order;
    }

    public static void setGroup_order(ArrayList<String> group_order) {
        Index.group_order = group_order;
    }

    public static Long getTotal_amount_new_order() {
        return total_amount_new_order;
    }

    public static void setTotal_amount_new_order(Long total_amount_new_order) {
        Index.total_amount_new_order = total_amount_new_order;
    }

    public static Long todayTotalMilkAvailable;
    public static long currentTotalMilkAvailable;

    public static ArrayList<Map> availableProducts;
    public static Boolean productEditable;

    public static Boolean getProductEditable() {
        return productEditable;
    }

    public static void setProductEditable(Boolean productEditable) {
        Index.productEditable = productEditable;
    }

    public static ArrayList<Map> getAvailableProducts() {
        return availableProducts;
    }

    public static void setAvailableProducts(ArrayList<Map> availableProducts) {
        Index.availableProducts = availableProducts;
    }

    public static long getCurrentTotalMilkAvailable() {
        return currentTotalMilkAvailable;
    }

    public static void setCurrentTotalMilkAvailable(long currentTotalMilkAvailable) {
        Index.currentTotalMilkAvailable = currentTotalMilkAvailable;
    }

    public static Map<String,Long> currentMilkTypeAvailability;

    public static Map<String, Long> getTotalMilkTypeAvailability() {
        return TotalMilkTypeAvailability;
    }

    public static void setTotalMilkTypeAvailability(Map<String, Long> totalMilkTypeAvailability) {
        TotalMilkTypeAvailability = totalMilkTypeAvailability;
    }

    public static Map<String,Long> TotalMilkTypeAvailability;


    public static Map<String, Long> getCurrentMilkTypeAvailability() {
        return currentMilkTypeAvailability;
    }

    public static void setCurrentMilkTypeAvailability(Map<String, Long> currentMilkTypeAvailability) {
        Index.currentMilkTypeAvailability = currentMilkTypeAvailability;
    }

    public static Long getTodayTotalMilkAvailable() {
        return todayTotalMilkAvailable;
    }

    public static void setTodayTotalMilkAvailable(Long todayTotalMilkAvailable) {
        Index.todayTotalMilkAvailable = todayTotalMilkAvailable;
    }

    public static Long getTotalQuantityRequire() {
        return totalQuantityRequire;
    }

    public static void setTotalQuantityRequire(Long totalQuantityRequire) {
        Index.totalQuantityRequire = totalQuantityRequire;
    }

    public static ArrayList<Long> getTotalRequirementArraylist() {
        return totalRequirementArraylist;
    }

    public static void setTotalRequirementArraylist(ArrayList<Long> totalRequirementArraylist) {
        Index.totalRequirementArraylist = totalRequirementArraylist;
    }

    public static ArrayList<Long> totalRequirementArraylist;

    public static Long getTotal_amount() {
        return total_amount;
    }

    public static void setTotal_amount(Long total_amount) {
        Index.total_amount = total_amount;
    }

    public static ArrayList<String> getProduct_list() {
        return product_list;
    }

    public static void setProduct_list(ArrayList<String> product_list) {
        Index.product_list = product_list;
    }

    public static Map<String, Long> getProduct_detail_map() {
        return product_detail_map;
    }

    public static void setProduct_detail_map(Map<String, Long> product_detail_map) {
        Index.product_detail_map = product_detail_map;
    }

    public static ArrayList<String> getArrayList() {
        return arrayList;
    }

    public static void setArrayList(ArrayList<String> arrayList) {
        Index.arrayList = arrayList;
    }

    public static int getINDEX() {
        return INDEX;
    }

    public static void setINDEX(int INDEX) {
        Index.INDEX = INDEX;
    }
}
