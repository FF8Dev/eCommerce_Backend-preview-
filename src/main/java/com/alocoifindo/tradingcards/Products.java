package com.alocoifindo.tradingcards;

/**
 *
 * @author Alocoifindo
 */
public class Products {
    private int id;
    private String productName;
    private int cod_prov;
    private double price;
    
    private Products(int id, String productName, int cod_prov, double price){
        this.id = id;
        this.productName = productName;
        this.cod_prov = cod_prov;
        this.price = price;
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

    public int getCod_prov() {
        return cod_prov;
    }

    public void setCod_prov(int cod_prov) {
        this.cod_prov = cod_prov;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    
    
}