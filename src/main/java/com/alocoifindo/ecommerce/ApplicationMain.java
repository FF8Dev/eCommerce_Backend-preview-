/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author facundoferreyra
 */
public class ApplicationMain {

    static boolean DEBUG = true;
    static LoginUI loginUI = new LoginUI();
    
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        loginUI.main(args);
    }
    
}
