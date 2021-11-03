package com.alocoifindo.tradingcards;

/**
 *
 * @author Alocoifindo
 */
public class Users {
    private int id;
    private String firstName;
    private String surname;
    private String telephone;
    private String mail;
    private String username;
    private String pass;
    private String typeUser;

    public Users(int id, String firstName, String surname, String telephone, String mail, String username, String pass, String typeUser) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.telephone = telephone;
        this.mail = mail;
        this.username = username;
        this.pass = pass;
        this.typeUser = typeUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    
}