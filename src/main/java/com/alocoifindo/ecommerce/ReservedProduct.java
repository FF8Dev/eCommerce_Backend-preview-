/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.time.LocalDate;

/**
 *
 * @author facundoferreyra
 */
public class ReservedProduct {
    String productId;
    LocalDate startDate;
    LocalDate endDate;

    public ReservedProduct() {
    }

    public ReservedProduct(String productId) {
        this.productId = productId;
    }

    public ReservedProduct(String productId, LocalDate startDate, LocalDate endDate) {
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    
    
}
