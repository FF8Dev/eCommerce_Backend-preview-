/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 *
 * @author facundoferreyra
 */
public class RentMyStuff {

    static boolean DEBUG = true;
    static boolean DEBUGdb = false;
    static boolean DEBUGwin = false;
    static Customer customer = new Customer();
    static Order order = new Order();
    static int totalDays = 1;
    static List<Product> products = new ArrayList<>();
    static List<Product> productsInOrder = new ArrayList<>();

    private JTextField dateTextField;
    private JButton toggleCalendarButton;

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
    
    static public void closeStatement(PreparedStatement stmt) throws SQLException {
        try {
            stmt.close();
        } finally {
            stmt = null;
            if (DEBUGdb) {
                System.out.println("Statement connection exit");
            }
        }
    }

    static public void closeResultSet(ResultSet rs) throws SQLException {
        try {
            rs.close();
        } finally {
            rs = null;
            if (DEBUGdb) {
                System.out.println("ResultSet connection exit");
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
