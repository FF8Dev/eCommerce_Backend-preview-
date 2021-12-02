/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

/**
 *
 * @author facundoferreyra
 */
public class Customer extends User{
    String firstname;
    String lastname;
    String addressLine;
    int postalcode;
    String city;
    String email;
    int telephone;
    int discount;
    String creationDate;
    String createdBy;

    public Customer() {
    }

    public Customer(String username, String password, String firstname, String lastname, String addressLine, int postalcode, String city, String email, int telephone, int discount) {
        super(username, password);
        this.firstname = firstname;
        this.lastname = lastname;
        this.addressLine = addressLine;
        this.postalcode = postalcode;
        this.city = city;
        this.email = email;
        this.telephone = telephone;
        this.discount = discount;
    }
 
    public Customer getCustomerByUsername(String username) {
        return this;
    }
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public int getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(int postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    
}
