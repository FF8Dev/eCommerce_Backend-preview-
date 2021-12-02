/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author facundoferreyra
 */
public class ApplicationMain {

    static boolean DEBUG = true;
    static boolean DEBUGdb = false;
    static boolean DEBUGwin = false;
    static Customer customer = new Customer();
    static Order order = new Order();
    static int totalDays = 1;
    static List<Product> products = new ArrayList<>();
    static List<Product> productsInOrder = new ArrayList<>();
    
    static public Connection startConnection() throws SQLException {
        Connection con = null;
        try {
            // MySQL Driver dependency driver for Maven
            Class.forName("com.mysql.cj.jdbc.Driver");
            String urlDB = "jdbc:mysql://localhost:3306/rentyourstuff";
            String user = "root";
            String pass = "pa88#word";
            con = DriverManager.getConnection(urlDB, user, pass);
            if (DEBUGdb) {
                System.out.println("Connected to Database");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            }
        return con;
    }
    
    static public void stopConnection(Connection con) throws SQLException {
        try {
                con.close();
        } finally {
            con = null;
            if (DEBUGdb) {
                System.out.println("Database connection exit");
            }
        }   
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LoginUI.loginUI.main(args);
    }
    
}
