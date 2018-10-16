package com.prod.sudesi.lotusherbalsnew.Models;


import java.io.Serializable;

public class ReportModel implements Serializable {

    public String product_id;
    public String db_id;
    public String eancode;
    public String product_category;
    public String product_type;
    public String product_name;
    public String size;
    public String price;
    public String emp_id;
    public String opening_stock;
    public String opening_amt;
    public String stock_received;
    public String stock_in_hand;
    public String close_bal;
    public String close_amt;
    public String return_saleable;
    public String return_non_saleable;
    public String sold_stock;
    public String sold_amt;
    public String total_gross_amount;
    public String total_net_amount;
    public String discount;
    public String savedServer;
    public String insert_date;
    public String FLRCode;


    public ReportModel(String product_name, String product_type,  String price, String size, String opening_stock, String opening_amt,
                       String stock_received, String return_non_saleable, String return_saleable, String sold_stock, String sold_amt,
                       String close_bal, String close_amt, String total_gross_amount, String discount, String total_net_amount,
                       String savedServer, String db_id, String FLRCode) {
        this.product_type = product_type;
        this.product_name = product_name;
        this.price = price;
        this.size = size;
        this.opening_stock = opening_stock;
        this.opening_amt = opening_amt;
        this.stock_received = stock_received;
        this.return_non_saleable = return_non_saleable;
        this.return_saleable = return_saleable;
        this.sold_stock = sold_stock;
        this.sold_amt = sold_amt;
        this.close_bal = close_bal;
        this.close_amt = close_amt;
        this.total_gross_amount = total_gross_amount;
        this.discount = discount;
        this.total_net_amount = total_net_amount;
        this.savedServer = savedServer;
        this.db_id = db_id;
        this.FLRCode = FLRCode;

    }

    public ReportModel() {

    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDb_id() {
        return db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public String getEancode() {
        return eancode;
    }

    public void setEancode(String eancode) {
        this.eancode = eancode;
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

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getOpening_stock() {
        return opening_stock;
    }

    public void setOpening_stock(String opening_stock) {
        this.opening_stock = opening_stock;
    }

    public String getOpening_amt() {
        return opening_amt;
    }

    public void setOpening_amt(String opening_amt) {
        this.opening_amt = opening_amt;
    }

    public String getStock_received() {
        return stock_received;
    }

    public void setStock_received(String stock_received) {
        this.stock_received = stock_received;
    }

    public String getStock_in_hand() {
        return stock_in_hand;
    }

    public void setStock_in_hand(String stock_in_hand) {
        this.stock_in_hand = stock_in_hand;
    }

    public String getClose_bal() {
        return close_bal;
    }

    public void setClose_bal(String close_bal) {
        this.close_bal = close_bal;
    }

    public String getClose_amt() {
        return close_amt;
    }

    public void setClose_amt(String close_amt) {
        this.close_amt = close_amt;
    }

    public String getReturn_saleable() {
        return return_saleable;
    }

    public void setReturn_saleable(String return_saleable) {
        this.return_saleable = return_saleable;
    }

    public String getReturn_non_saleable() {
        return return_non_saleable;
    }

    public void setReturn_non_saleable(String return_non_saleable) {
        this.return_non_saleable = return_non_saleable;
    }

    public String getSold_stock() {
        return sold_stock;
    }

    public void setSold_stock(String sold_stock) {
        this.sold_stock = sold_stock;
    }

    public String getSold_amt() {
        return sold_amt;
    }

    public void setSold_amt(String sold_amt) {
        this.sold_amt = sold_amt;
    }

    public String getTotal_gross_amount() {
        return total_gross_amount;
    }

    public void setTotal_gross_amount(String total_gross_amount) {
        this.total_gross_amount = total_gross_amount;
    }

    public String getTotal_net_amount() {
        return total_net_amount;
    }

    public void setTotal_net_amount(String total_net_amount) {
        this.total_net_amount = total_net_amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSavedServer() {
        return savedServer;
    }

    public void setSavedServer(String savedServer) {
        this.savedServer = savedServer;
    }

    public String getInsert_date() {
        return insert_date;
    }

    public void setInsert_date(String insert_date) {
        this.insert_date = insert_date;
    }

    public String getFLRCode() {
        return FLRCode;
    }

    public void setFLRCode(String FLRCode) {
        this.FLRCode = FLRCode;
    }
}
