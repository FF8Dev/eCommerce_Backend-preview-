/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

/**
 *
 * @author Alocoifindo
 */
public class Product {
    private int id;
    private String brand;
    private String modelName;
    private byte image;
    private double pricePerDay;
    private int discountPerDay;

    public Product(int id, String brand, String modelName, byte image, double pricePerDay, int discountPerDay) {
        this.id = id;
        this.brand = brand;
        this.modelName = modelName;
        this.image = image;
        this.pricePerDay = pricePerDay;
        this.discountPerDay = discountPerDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public byte getImage() {
        return image;
    }

    public void setImage(byte image) {
        this.image = image;
    }
    
    public String getProductName() {
        return modelName;
    }

    public void setProductName(String productName) {
        this.modelName = productName;
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