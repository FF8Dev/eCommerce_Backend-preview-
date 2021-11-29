/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

/**
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
 * https://alocosite.w3spaces.com
 */
public class Product {
    private int id;
    private String productName;
    private byte image;
    private double pricePerDay;
    private int discountPerDay;

    public Product(int id, String productName, double pricePerDay, int discountPerDay) {
        this.id = id;
        this.productName = productName;
        this.pricePerDay = pricePerDay;
        this.discountPerDay = discountPerDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return pricePerDay;
    }

    public void setPrice(double price) {
        this.pricePerDay = price;
    }
    
    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getDiscountPerDay() {
        return discountPerDay;
    }

    public void setDiscountPerDay(int discountPerDay) {
        this.discountPerDay = discountPerDay;
    }
    
}