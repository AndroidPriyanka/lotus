package com.prod.sudesi.lotusherbalsnew.Models;

import java.io.Serializable;

public class FocusModel implements Serializable {
    String db_id;
    String pro_name;
    String product_category;
    String product_type;
    String size;
    String price;
    String username;
    String target_qty;
    String target_amt;
    String android_created_date;
    String achievement_Unit;
    String achievement_value;

    public String getDb_id() {
        return db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTarget_qty() {
        return target_qty;
    }

    public void setTarget_qty(String target_qty) {
        this.target_qty = target_qty;
    }

    public String getTarget_amt() {
        return target_amt;
    }

    public void setTarget_amt(String target_amt) {
        this.target_amt = target_amt;
    }

    public String getAndroid_created_date() {
        return android_created_date;
    }

    public void setAndroid_created_date(String android_created_date) {
        this.android_created_date = android_created_date;
    }

    public String getAchievement_Unit() {
        return achievement_Unit;
    }

    public void setAchievement_Unit(String achievement_Unit) {
        this.achievement_Unit = achievement_Unit;
    }

    public String getAchievement_value() {
        return achievement_value;
    }

    public void setAchievement_value(String achievement_value) {
        this.achievement_value = achievement_value;
    }


}
