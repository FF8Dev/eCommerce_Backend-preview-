/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author facundoferreyra
 */
public class UserUI extends javax.swing.JFrame {


    int id_user_new;
    int discount;
    int id_userCreator;
    static UserUI userUI = new UserUI();
    static boolean permission = false;
    
    /**
     * Creates new form UserUI
     */
    public UserUI() {
        initComponents();
        setLocationRelativeTo(null);
        usernameExistLabel.setVisible(false);
        
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        am.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    public void setUpButtons() {
        // DiscountField will not show
        discountLabel.setVisible(false);
        discountField.setVisible(false);
        discountField.setEditable(false);
        
        // if came from ApplicationUI
        if (LoginUI.fromLogin == false) {
            getRootPane().setDefaultButton(updateButton);
            passwordLabel.setVisible(false);
            passwordField.setVisible(false);
            
        // if came from Login SignUp
        } else if (LoginUI.fromLogin == true && LoginUI.privileges == false) {
            getRootPane().setDefaultButton(createUserButton);
            createUserButton.setVisible(true);
            // updateButton#Visible(false) if cames from Login SignUp
            updateButton.setVisible(false);
            updatePasswordButton.setVisible(false);
            
        } 
        if (LoginUI.privileges == true) {
            // createUserButton#Visibile(true) if Admin
            createUserButton.setVisible(true);
            updatePasswordButton.setVisible(false);
            // discount#Visible if admin
            // DiscountField will show only to Admin
            discountLabel.setVisible(true);
            discountField.setVisible(true);
            discountField.setEditable(true);
            callCustomerData();
        }
        
        // if Customer && cames from ApplicationUI
        if (LoginUI.fromLogin == false && LoginUI.privileges == false) {
            getRootPane().setDefaultButton(updateButton);
            createUserButton.setVisible(false);
            updateButton.setVisible(true);
            updatePasswordButton.setVisible(true);
            callCustomerData();
        } 
        
        
    }
    
    public static void callCustomerData() {
        int customerId = ApplicationMain.customer.getId();
        
        try {
            Connection con = ApplicationMain.startConnection();
            System.out.println("id_customer to callCustomerData: " + customerId);
            PreparedStatement stmtSelCust = con.prepareStatement("SELECT * FROM Customers WHERE id_user=?");
            stmtSelCust.setInt(1, customerId);
            ResultSet rsSelCust = stmtSelCust.executeQuery();
            
            while (rsSelCust.next()) {
                String firstname = rsSelCust.getString("firstname");
                String lastname = rsSelCust.getString("lastname");
                String addressLine = rsSelCust.getString("address_line");
                String city = rsSelCust.getString("city");
                int postalcode = rsSelCust.getInt("postalcode");
                int telephone = rsSelCust.getInt("telephone");
                String email = rsSelCust.getString("email");
                int discount = rsSelCust.getInt("discount");
//                if (discount > 0) {
//                    discountLabel.setVisible(true);
//                    discountField.setVisible(true);
//                }
                
                ApplicationMain.customer.setFirstname(firstname);
                ApplicationMain.customer.setLastname(lastname);
                ApplicationMain.customer.setAddressLine(addressLine);
                ApplicationMain.customer.setCity(city);
                ApplicationMain.customer.setPostalcode(postalcode);
                ApplicationMain.customer.setTelephone(telephone);
                ApplicationMain.customer.setEmail(email);
                ApplicationMain.customer.setDiscount(discount);
            }
            permission = true;
            ApplicationMain.stopConnection(con);
            
        } catch (SQLIntegrityConstraintViolationException intex) {
            System.out.println("User exist");
            intex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Cannot select data from Customers WHERE customerId");
        } 

        usernameField.setText(ApplicationMain.customer.getUsername());
        passwordField.setText(ApplicationMain.customer.getPassword());
        firstnameField.setText(ApplicationMain.customer.getFirstname());
        lastnameField.setText(ApplicationMain.customer.getLastname());
        if (ApplicationMain.customer.getTelephone() == 0) {
            telephoneField.setText("");
        } else {
            telephoneField.setText(String.valueOf(ApplicationMain.customer.getTelephone()));
        }
        emailField.setText(ApplicationMain.customer.getEmail());
        addressField.setText(ApplicationMain.customer.getAddressLine());
        cityField.setText(ApplicationMain.customer.getCity());
        postalcodeField.setText(String.format("%05d", ApplicationMain.customer.getPostalcode()));
        discountField.setText(String.valueOf(ApplicationMain.customer.getDiscount()));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        firstnameField = new javax.swing.JTextField();
        lastnameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        telephoneField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        discountLabel = new javax.swing.JLabel();
        discountField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cityField = new javax.swing.JTextField();
        postalcodeField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        createUserButton = new javax.swing.JButton();
        usernameExistLabel = new javax.swing.JLabel();
        updatePasswordButton = new javax.swing.JButton();

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Customer Data");
        setMinimumSize(new java.awt.Dimension(626, 259));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/customer_70.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, -1, -1));

        jLabel2.setText("First Name:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 82, -1, -1));
        getContentPane().add(firstnameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 77, 120, -1));
        getContentPane().add(lastnameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 109, 120, -1));

        jLabel3.setText("Last Name:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(102, 114, -1, -1));
        getContentPane().add(usernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 23, 120, -1));

        jLabel4.setText("Username:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 28, -1, -1));

        passwordLabel.setText("Password:");
        getContentPane().add(passwordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 28, -1, -1));

        passwordField.setToolTipText("");
        getContentPane().add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(367, 23, 120, -1));
        getContentPane().add(telephoneField, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 141, 112, -1));

        jLabel6.setText("Email:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 178, -1, -1));
        getContentPane().add(emailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 173, 171, -1));

        jLabel7.setText("Telephone:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 146, -1, -1));

        discountLabel.setText("Discount:");
        getContentPane().add(discountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(493, 28, -1, -1));
        getContentPane().add(discountField, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 23, 54, -1));
        getContentPane().add(addressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(355, 94, 210, -1));

        jLabel9.setText("Address:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(355, 77, -1, -1));

        jLabel10.setText("City:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 163, -1, -1));
        getContentPane().add(cityField, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 158, 149, -1));
        getContentPane().add(postalcodeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 126, 100, -1));

        jLabel11.setText("Postal Code:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 131, -1, -1));

        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(526, 218, -1, -1));

        createUserButton.setText("Create User");
        createUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createUserButtonActionPerformed(evt);
            }
        });
        getContentPane().add(createUserButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 218, -1, -1));

        usernameExistLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        usernameExistLabel.setForeground(new java.awt.Color(255, 0, 51));
        usernameExistLabel.setText("username exists");
        getContentPane().add(usernameExistLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 50, -1, -1));

        updatePasswordButton.setText("Update Password");
        updatePasswordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePasswordButtonActionPerformed(evt);
            }
        });
        getContentPane().add(updatePasswordButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(369, 218, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try {
            Connection con = ApplicationMain.startConnection();
            
            // Username change
            if (usernameField.getText() != ApplicationMain.customer.getUsername()) {
                String newUsername = usernameField.getText();
                String changeUsernameSQL = "UPDATE Users SET users.username=? WHERE id_user=?";
                PreparedStatement stmtChgUsrnm = con.prepareStatement(changeUsernameSQL);
                stmtChgUsrnm.setString(1, newUsername);
                stmtChgUsrnm.setInt(2, ApplicationMain.customer.getId());
                stmtChgUsrnm.executeUpdate();
                ApplicationMain.customer.setUsername(newUsername);
            }
            
            // UPDATE Customers
            String updateCustomerSQL = "UPDATE Customers SET firstname=?, lastname=?, address_line=?, city=?, postalcode=?, telephone=?, email=?, discount=?, last_update=NOW() WHERE id_user=?";
            PreparedStatement stmtUpdCustomer = con.prepareStatement(updateCustomerSQL);
            stmtUpdCustomer.setString(1, firstnameField.getText());
            stmtUpdCustomer.setString(2, lastnameField.getText());
            stmtUpdCustomer.setString(3, addressField.getText());
            stmtUpdCustomer.setString(4, cityField.getText());
            stmtUpdCustomer.setInt(5, Integer.parseInt(postalcodeField.getText()));
            stmtUpdCustomer.setInt(6, Integer.parseInt(telephoneField.getText()));
            stmtUpdCustomer.setString(7, emailField.getText());
            if (LoginUI.privileges == true) {
                int newDiscount = Integer.parseInt(discountField.getText());
                stmtUpdCustomer.setInt(8, newDiscount);
                ApplicationMain.customer.setDiscount(newDiscount);
            } else {
                stmtUpdCustomer.setInt(8, ApplicationMain.customer.getDiscount());
            }
            
            // WHERE id_user is Customer selected
            stmtUpdCustomer.setInt(9, ApplicationMain.customer.getId());
            
            stmtUpdCustomer.executeUpdate();
            System.out.println("Data updated");
            ApplicationMain.stopConnection(con);
            
            // Update appUI Data
            ApplicationUI.setCustomerDataUI();
            setVisible(false);
        } catch (SQLException ex) {
            System.out.println("Data not uploaded");
            ex.printStackTrace();
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void createUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUserButtonActionPerformed
        try {
            Connection con = ApplicationMain.startConnection();
            
            // if comes fromLoginUI = Find last ID for INSERT NEW USER
            if (LoginUI.fromLogin) {
                PreparedStatement lastId_stmt = con.prepareStatement("SELECT max(id_user) FROM Users");
                ResultSet rsMaxId = lastId_stmt.executeQuery();
                rsMaxId.next();
                id_user_new = rsMaxId.getInt(1) + 1;
                id_userCreator = id_user_new;
                
            // Find ID from CURRENT USER
            } else {
                PreparedStatement idCheck = con.prepareStatement("SELECT id_user FROM Users WHERE username=?");
                idCheck.setString(1, LoginUI.username);
                ResultSet rsId = idCheck.executeQuery();
                id_userCreator = rsId.getInt(1);
            }
            
            // UPDATE 
            PreparedStatement stmtInsertUsr = con.prepareStatement("INSERT INTO Users SET id_user=?, username=?, password=?");
                                                        // 4 values in UPDATE
            PreparedStatement stmtInsertCust = con.prepareStatement("INSERT INTO Customers (`id_user`, `firstname`, `lastname`, `address_line`, `postalcode`, `city`, `email`, `telephone`, `discount`, `creation_date`, `id_ByUser`)"
                                                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), ?)");
            stmtInsertUsr.setInt(1, id_user_new);
            stmtInsertUsr.setString(2, usernameField.getText());
            stmtInsertUsr.setString(3, String.copyValueOf(passwordField.getPassword()));
            stmtInsertCust.setInt(1, id_user_new);
            stmtInsertCust.setString(2, firstnameField.getText());
            stmtInsertCust.setString(3, lastnameField.getText());
            stmtInsertCust.setString(4, addressField.getText());
            stmtInsertCust.setInt(5, Integer.parseInt(postalcodeField.getText()));
            stmtInsertCust.setString(6, cityField.getText());
            stmtInsertCust.setString(7, emailField.getText());
            stmtInsertCust.setInt(8, Integer.parseInt(telephoneField.getText()));
            if (LoginUI.privileges == true) {
                stmtInsertCust.setInt(9, Integer.parseInt(discountField.getText()));
            } else {
                stmtInsertCust.setInt(9, 0);
            }
            // NEXT CHANGE: "id_user_new" for "id_userCreator"
            stmtInsertCust.setInt(10, id_userCreator);
            try {
                stmtInsertUsr.executeUpdate();
                stmtInsertCust.executeUpdate();
                System.out.println("Data uploaded");
                setVisible(false);

            } catch (SQLIntegrityConstraintViolationException intex) {
                System.out.println("User exist");
                intex.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Data not uploaded");
                e.printStackTrace();
                
            } 
            ApplicationMain.stopConnection(con);
            
        } catch (SQLException ex) {
           ex.printStackTrace();
        }
        
        // ApplicationMain#Customer Set
        ApplicationMain.customer.setId(id_user_new);
        if (discountField.getText().equals("0")) {
            ApplicationMain.customer.setDiscount(Integer.parseInt(discountField.getText()));
        } else {
            ApplicationMain.customer.setDiscount(0);
        }
        ApplicationMain.customer.setUsername(usernameField.getText());
        ApplicationMain.customer.setFirstname(firstnameField.getText());
        ApplicationMain.customer.setLastname(lastnameField.getText());
        ApplicationMain.customer.setAddressLine(addressField.getText());
        ApplicationMain.customer.setCity(cityField.getText());
        ApplicationMain.customer.setPostalcode(Integer.parseInt(postalcodeField.getText()));
        ApplicationMain.customer.setEmail(emailField.getText());
        ApplicationMain.customer.setTelephone(Integer.parseInt(telephoneField.getText()));
        
        // loginUI#Visible(false) if exist
        LoginUI.fromLogin = false;
        LoginUI.loginUI.setVisible(false);
        if (permission == true) {
            setVisible(false);
            ApplicationUI.appUI.setVisible(true);
        }
        ApplicationUI.appUI.setVisible(true);
        usernameExistLabel.setVisible(true);
    }//GEN-LAST:event_createUserButtonActionPerformed

    private void updatePasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePasswordButtonActionPerformed
        PasswordUI.passUI.setVisible(true);
    }//GEN-LAST:event_updatePasswordButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the System look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("System".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                userUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextField addressField;
    private static javax.swing.JTextField cityField;
    private static javax.swing.JButton createUserButton;
    private static javax.swing.JTextField discountField;
    private static javax.swing.JLabel discountLabel;
    private static javax.swing.JTextField emailField;
    private static javax.swing.JTextField firstnameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JToggleButton jToggleButton1;
    private static javax.swing.JTextField lastnameField;
    public static javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private static javax.swing.JTextField postalcodeField;
    private static javax.swing.JTextField telephoneField;
    private static javax.swing.JButton updateButton;
    private javax.swing.JButton updatePasswordButton;
    private static javax.swing.JLabel usernameExistLabel;
    public static javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
