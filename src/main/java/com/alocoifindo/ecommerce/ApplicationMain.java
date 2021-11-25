/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author facundoferreyra
 */
public class ApplicationMain {

    static boolean DEBUG = true;
    static Customer customer = new Customer();
    static Order order = new Order();
    
    static public Connection startConnection() throws SQLException {
        Connection con = null;
        try {
            // MySQL Driver dependency driver for Maven
            Class.forName("com.mysql.cj.jdbc.Driver");
            String urlDB = "jdbc:mysql://localhost:3306/rentyourstuff";
            String user = "root";
            String pass = "pa88#word";
            con = DriverManager.getConnection(urlDB, user, pass);
            if (DEBUG) {
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
            if (DEBUG) {
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
