package com.example.android.bookstore;

public class Product {

    private String mName;
    private double mPrice;
    private int mQuantity;
    private String mSupplierName;
    private String mSupplierPhoneNumber;

    public Product (String name, double price, int quantity, String supplierName,
                    String supplierPhoneNumber){
        mName = name;
        mPrice = price;
        mQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhoneNumber = supplierPhoneNumber;
    }

    public String getmName() {
        return mName;
    }

    public double getmPrice() {
        return mPrice;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public String getmSupplierName() {
        return mSupplierName;
    }

    public String getmSupplierPhoneNumber() {
        return mSupplierPhoneNumber;
    }
}
