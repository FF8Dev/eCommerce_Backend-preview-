/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

/**
 *
 * @author facundoferreyra
 */
public class Order {
    int id;
    int orderLine;
    int days;
    double amount;
    String creationDate;
    String shipmentStatus;
    String lastUpdate;
    String createdBy;

    public Order() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(int orderLine) {
        this.orderLine = orderLine;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    
}
