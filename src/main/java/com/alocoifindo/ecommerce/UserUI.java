/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author facundoferreyra
 */
public class UserUI extends javax.swing.JFrame implements WindowListener {

    int id_user_new;
    int id_userCreator;
    static UserUI userUI = new UserUI();
    static boolean permission = false;
    static boolean validPassword = false;
    
    static String firstname;
    static String lastname;
    static String addressLine;
    static String city;
    static int postalcode;
    static int telephone;
    static String email;
    static int discount;
    
    static boolean updateOK = false;
    
    /**
     * Creates new form UserUI
     */
    public UserUI() {
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);
        usernameExistLabel.setVisible(false);
        emailExistLabel.setVisible(false);
        errorFormatLabel.setVisible(false);
        
        // dispose by ESCAPE_KEY
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

    @Override
    public void windowClosing(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("UserUI: windowClosing.");
        }
        
    }
    
    @Override
    public void windowClosed(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            //This will only be seen on standard output.
            System.out.println("UserUI: windowClosed.");
            emailExistLabel.setVisible(false);
            errorFormatLabel.setVisible(false);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
           System.out.println("UserUI: windowOpened.");
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("UserUI: windowIconified.");
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("UserUI: windowDeiconified.");
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("UserUI: windowActivated.");
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("UserUI: windowDeactivated.");
        }
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
            passwordLabel.setVisible(true);
            passwordField.setVisible(true);
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
                firstname = rsSelCust.getString("firstname");
                lastname = rsSelCust.getString("lastname");
                addressLine = rsSelCust.getString("address_line");
                city = rsSelCust.getString("city");
                postalcode = rsSelCust.getInt("postalcode");
                telephone = rsSelCust.getInt("telephone");
                email = rsSelCust.getString("email");
                discount = rsSelCust.getInt("discount");
                
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
        if (postalcode == 00000) {
            postalcodeField.setText("");
        } else {
            postalcodeField.setText(String.format("%05d", ApplicationMain.customer.getPostalcode()));
        }
        discountField.setText(String.valueOf(ApplicationMain.customer.getDiscount()));
    }
    
    static class UsernameExistException extends Exception {
      public UsernameExistException() {
        usernameExistLabel.setVisible(true);
        updateOK = false;
      }
    }
    
    // Class for user-defined InvalidPasswordException
    static class InvalidPasswordException extends Exception {
        int passwordConditionViolated = 0;

        public InvalidPasswordException(int conditionViolated) {
            super("Invalid Password");
            passwordConditionViolated = conditionViolated;
        }
  
        public void showMessage() {
            switch (passwordConditionViolated) {
            // Password length should be between 8 to 15 characters
            case 1:
                JOptionPane.showMessageDialog(null, "Password length should be between 8 to 15 characters", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                break;
            // Password should not contain any space
            case 2:
                JOptionPane.showMessageDialog(null, "Password should not contain any space", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                break;
            // Password should contain// at least one digit(0-9)
            case 3:
                JOptionPane.showMessageDialog(null, "Password should contain at least one digit(0-9)", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                break;
//            // Password should contain at least one special character ( @, #, %, &, !, $ )
//            case 4:
//                JOptionPane.showMessageDialog(null, "Password should contain at least one special character", "Invalid Password", JOptionPane.ERROR_MESSAGE);
//                break;
            // Password should contain at least one uppercase letter(A-Z)
            case 5:
                JOptionPane.showMessageDialog(null, "Password should contain at least one uppercase letter(A-Z)", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                break;
            // Password should contain at least one lowercase letter(a-z)
            case 6:
                JOptionPane.showMessageDialog(null, "Password should contain at least one lowercase letter(a-z)", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }
    
    static class EmailExistException extends Exception {
        public EmailExistException() {
            emailExistLabel.setVisible(true);
        }
    }
    
    public static boolean usernameCheck(String username) {
        usernameExistLabel.setVisible(false);
        try {
            Connection con = ApplicationMain.startConnection();

            PreparedStatement stmtChckUsr = con.prepareStatement("SELECT username FROM Users WHERE username=?");
            stmtChckUsr.setString(1, username);
            
            ResultSet rsChckUsr = stmtChckUsr.executeQuery();
            if (rsChckUsr.next()) {
                throw new UsernameExistException();
            }
            
            ApplicationMain.stopConnection(con);
            return false;
            
        } catch (UsernameExistException inte) {
            System.out.println("User exist");
            updateOK = false;
            return true;
        } catch (SQLException e) {
            System.out.println("Error in SQL Statement");
            return true;
        }
    }
    
    // A utility function to check whether a password is valid or not
    public static boolean passwordValidator(char[] password) throws InvalidPasswordException {
        String passwordWrap = String.copyValueOf(password);
        System.out.println("passwordWrap: " + passwordWrap);
        // for checking if password length is between 8 and 15
        if (!((passwordWrap.length() >= 8)
              && (passwordWrap.length() <= 15))) {
            throw new InvalidPasswordException(1);
        }
  
        // to check space
        if (passwordWrap.contains(" ")) {
            throw new InvalidPasswordException(2);
        }
        if (true) {
            int count = 0;
  
            // check digits from 0 to 9
            for (int i = 0; i <= 9; i++) {
  
                // to convert int to string
                String str1 = Integer.toString(i);
  
                if (passwordWrap.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                throw new InvalidPasswordException(3);
            }
        }
  
//        // // At least one capital letter one special characters
//        if (!(passwordWrap.contains("@") || passwordWrap.contains("#")
//              || passwordWrap.contains("!") || passwordWrap.contains("~")
//              || passwordWrap.contains("$") || passwordWrap.contains("%")
//              || passwordWrap.contains("^") || passwordWrap.contains("&")
//              || passwordWrap.contains("*") || passwordWrap.contains("(")
//              || passwordWrap.contains(")") || passwordWrap.contains("-")
//              || passwordWrap.contains("+") || passwordWrap.contains("/")
//              || passwordWrap.contains(":") || passwordWrap.contains(".")
//              || passwordWrap.contains(", ") || passwordWrap.contains("<")
//              || passwordWrap.contains(">") || passwordWrap.contains("?")
//              || passwordWrap.contains("|"))) {
//            throw new InvalidPasswordException(4);
//        }
        // At least one capital letter
        if (true) {
            int count = 0;
  
            // checking capital letters
            for (int i = 65; i <= 90; i++) {
  
                // type casting
                char c = (char)i;
  
                String str1 = Character.toString(c);
                if (passwordWrap.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                throw new InvalidPasswordException(5);
            }
        }
        // At least one small letter
        if (true) {
            int count = 0;
  
            // checking small letters
            for (int i = 90; i <= 122; i++) {
  
                // type casting
                char c = (char)i;
                String str1 = Character.toString(c);
  
                if (passwordWrap.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                throw new InvalidPasswordException(6);
            }
        }
  
        // The password is valid
        validPassword = true;
        return validPassword;
    }
    
    public static boolean emailCheck(String email) {
//        emailExistLabel.setVisible(false);
        try {
            Connection con = ApplicationMain.startConnection();

            PreparedStatement stmtChckUsr = con.prepareStatement("SELECT email FROM Customers WHERE email=?");
            stmtChckUsr.setString(1, email);
            
            ResultSet rsChckUsr = stmtChckUsr.executeQuery();
            if (rsChckUsr.next()) {
                throw new EmailExistException();
            }
            
            ApplicationMain.stopConnection(con);
            return false;
            
        } catch (EmailExistException inte) {
            System.out.println("Email exist");
            updateOK = false;
            return true;
        } catch (SQLException e) {
            System.out.println("Error in SQL Statement");
            return true;
        }
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
        emailExistLabel = new javax.swing.JLabel();
        updatePasswordButton = new javax.swing.JButton();
        usernameExistLabel = new javax.swing.JLabel();
        errorFormatLabel = new javax.swing.JLabel();

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

        emailExistLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        emailExistLabel.setForeground(new java.awt.Color(255, 0, 51));
        emailExistLabel.setText("email already registered");
        getContentPane().add(emailExistLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, -1, -1));

        updatePasswordButton.setText("Update Password");
        updatePasswordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePasswordButtonActionPerformed(evt);
            }
        });
        getContentPane().add(updatePasswordButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(369, 218, -1, -1));

        usernameExistLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        usernameExistLabel.setForeground(new java.awt.Color(255, 0, 51));
        usernameExistLabel.setText("username exists");
        getContentPane().add(usernameExistLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 50, -1, -1));

        errorFormatLabel.setForeground(new java.awt.Color(153, 153, 153));
        errorFormatLabel.setText("error in a field format");
        getContentPane().add(errorFormatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 200, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        String usernameSet = usernameField.getText();
        String emailSet = emailField.getText();
        
        try {
            Connection con = ApplicationMain.startConnection();
            System.out.println("getUsername: " + ApplicationMain.customer.getUsername() );
            // Username change
            if (!usernameField.getText().equals(ApplicationMain.customer.getUsername())) {
                if (!usernameCheck(usernameSet)) {
                    String newUsername = usernameSet;
                    String changeUsernameSQL = "UPDATE Users SET users.username=? WHERE id_user=?";
                    try {
                        PreparedStatement stmtChgUsrnm = con.prepareStatement(changeUsernameSQL);
                        stmtChgUsrnm.setString(1, newUsername);
                        stmtChgUsrnm.setInt(2, ApplicationMain.customer.getId());
                        stmtChgUsrnm.executeUpdate();
                        updateOK = true;
                        if (!emailSet.equals(email)) {
                            emailCheck(emailSet);
                        }
                        ApplicationMain.customer.setUsername(newUsername);
                        System.out.println("New username: " + newUsername);
                    } catch (SQLException ex) {
                        System.out.println("Cannot update username in Users");
                    }
                }
            } else if (usernameSet.equals(ApplicationMain.customer.getUsername())) {
                updateOK = true;
            }
            
            if (!emailSet.equals(email)) {
                if (!emailCheck(emailField.getText())) {
                    try {
                        PreparedStatement stmtUpdEmail = con.prepareStatement("UPDATE Customers SET email=? WHERE id_user=?");
                        stmtUpdEmail.setString(1, emailField.getText());
                        stmtUpdEmail.setInt(2, ApplicationMain.customer.getId());

                        stmtUpdEmail.executeUpdate();
                        System.out.println("Email updated");
                        updateOK = true;
                        if (!usernameSet.equals(ApplicationMain.customer.getUsername())) {
                            usernameCheck(usernameSet);
                        }
                    } catch (SQLIntegrityConstraintViolationException ice) {
                        System.out.println("Email exists - FATAL ERROR");
                        emailExistLabel.setVisible(true);
                    } catch (SQLException ex) {
                        System.out.println("Email not uploaded");
                        ex.printStackTrace();
                    }
                }
            } 
            
            if (!firstnameField.getText().equals(firstname) || !lastnameField.getText().equals(lastname)
                || !addressField.getText().equals(addressLine) || !cityField.getText().equals(city)
                || !postalcodeField.getText().equals(String.format("%05d", postalcode)) || !telephoneField.getText().equals(String.valueOf(telephone))
                || !discountField.getText().equals(String.valueOf(discount))) {
                // UPDATE Customers
                String updateCustomerSQL = "UPDATE Customers SET firstname=?, lastname=?, address_line=?, city=?, postalcode=?, telephone=?, discount=?, last_update=NOW() WHERE id_user=?";
                try {
                    PreparedStatement stmtUpdCustomer = con.prepareStatement(updateCustomerSQL);
                    stmtUpdCustomer.setString(1, firstnameField.getText());
                    stmtUpdCustomer.setString(2, lastnameField.getText());
                    stmtUpdCustomer.setString(3, addressField.getText());
                    stmtUpdCustomer.setString(4, cityField.getText());
                    if (postalcodeField.getText().equals("")) {
                        stmtUpdCustomer.setInt(5, 0);
                    } else {
                        stmtUpdCustomer.setInt(5, Integer.parseInt(postalcodeField.getText()));
                    }
                    if (postalcodeField.getText().equals("")) {
                        stmtUpdCustomer.setInt(6, 0);
                    } else {
                        stmtUpdCustomer.setInt(6, Integer.parseInt(telephoneField.getText()));
                    }
                    if (LoginUI.privileges == true) {
                        int newDiscount = Integer.parseInt(discountField.getText());
                        stmtUpdCustomer.setInt(7, newDiscount);
                        ApplicationMain.customer.setDiscount(newDiscount);
                    } else {
                        stmtUpdCustomer.setInt(7, ApplicationMain.customer.getDiscount());
                    }

                    // WHERE id_user is Customer selected
                    stmtUpdCustomer.setInt(8, ApplicationMain.customer.getId());

                    stmtUpdCustomer.executeUpdate();
                    System.out.println("Data updated");

                    ApplicationMain.stopConnection(con);
                    updateOK = true;
                    if (!usernameSet.equals(ApplicationMain.customer.getUsername()) || !emailSet.equals(email)) {
                        usernameCheck(usernameField.getText());
                        emailCheck(emailField.getText());
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Cannot update customer data");
                    errorFormatLabel.setVisible(true);
                } catch (SQLIntegrityConstraintViolationException ice) {
                    System.out.println("Email exists - FATAL ERROR");
                    emailExistLabel.setVisible(true);
                } catch (SQLException ex) {
                    System.out.println("Data not uploaded");
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Data not uploaded");
            ex.printStackTrace();
        }
        
        if (updateOK) {
            // Update appUI Data
            ApplicationUI.listCustomers();
            ApplicationUI.setCustomerDataUI();
            setVisible(false);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void createUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUserButtonActionPerformed
        String usernameSet = usernameField.getText();
        String firstnameSet = firstnameField.getText();
        String lastnameSet = lastnameField.getText();
        String addressLineSet= addressField.getText();
        String citySet = cityField.getText();
        String postalcodeString = postalcodeField.getText();
        String telephoneString = telephoneField.getText();
        int postalcodeSet = 0;
        int telephoneSet = 0;
        String emailSet = emailField.getText().toLowerCase();
        String discountString = discountField.getText();
        int discountSet = 0;
        
        if (postalcodeString.matches("[0-9]+")) {
            postalcodeSet = Integer.parseInt(postalcodeString);
        }
        
        if (telephoneString.matches("[0-9]+")) {
            telephoneSet = Integer.parseInt(telephoneString);
        }
        
        if (discountString.matches("[0-9]+")) {
            discountSet = Integer.parseInt(discountString);
        }
        
        try {
            boolean usernameCheck = usernameCheck(usernameSet);
            boolean emailCheck = emailCheck(emailSet);
            passwordValidator(passwordField.getPassword());
            
            if (!usernameCheck && !emailCheck) {
                try {
                    Connection con = ApplicationMain.startConnection();
                    con.setAutoCommit(false);

                    // if comes fromLoginUI = Find last ID for INSERT NEW USER
                    if (LoginUI.fromLogin) {
                        PreparedStatement lastId_stmt = con.prepareStatement("SELECT max(id_user) FROM Users");
                        ResultSet rsMaxId = lastId_stmt.executeQuery();
                        rsMaxId.next();
                        id_user_new = rsMaxId.getInt(1) + 1;
//                        id_userCreator = id_user_new;

                    // Find ID from CURRENT USER CREATOR
                    } else {
                        PreparedStatement idCheck = con.prepareStatement("SELECT id_user FROM Users WHERE username=?");
                        idCheck.setString(1, LoginUI.username);
                        ResultSet rsId = idCheck.executeQuery();
                        rsId.next();
                        id_userCreator = rsId.getInt(1);
                        
                        PreparedStatement lastId_stmt = con.prepareStatement("SELECT max(id_user) FROM Users");
                        ResultSet rsMaxId = lastId_stmt.executeQuery();
                        rsMaxId.next();
                        id_user_new = rsMaxId.getInt(1) + 1;
                    }

                    // UPDATE 
                    PreparedStatement stmtInsertUsr = con.prepareStatement("INSERT INTO Users SET id_user=?, username=?, password=?");
                                                                // 4 values in UPDATE
                    PreparedStatement stmtInsertCust = con.prepareStatement("INSERT INTO Customers (`id_user`, `firstname`, `lastname`, `address_line`, `postalcode`, `city`, `email`, `telephone`, `discount`, `creation_date`, `last_update`, `id_ByUser`)"
                                                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), ?)");
                    stmtInsertUsr.setInt(1, id_user_new);
                    stmtInsertUsr.setString(2, usernameSet);
                    stmtInsertUsr.setString(3, String.copyValueOf(passwordField.getPassword()));
                    stmtInsertCust.setInt(1, id_user_new);
                    stmtInsertCust.setString(2, firstnameSet);
                    stmtInsertCust.setString(3, lastnameSet);
                    stmtInsertCust.setString(4, addressLineSet);
                    stmtInsertCust.setInt(5, postalcodeSet);
                    stmtInsertCust.setString(6, citySet);
                    stmtInsertCust.setString(7, emailSet);          // email set into SQL Statement
                    stmtInsertCust.setInt(8, telephoneSet);
                    if (LoginUI.privileges == true) {
                        stmtInsertCust.setInt(9, discountSet);
                    } else {
                        stmtInsertCust.setInt(9, 0);
                    }
                    stmtInsertCust.setInt(10, id_userCreator);

                    stmtInsertUsr.executeUpdate();
                    stmtInsertCust.executeUpdate();
                    con.commit();
                    System.out.println("Data uploaded");

                    // ApplicationMain#Customer Set
                    ApplicationMain.customer.setId(id_user_new);
                    if (discountField.getText().equals("0")) {
                        ApplicationMain.customer.setDiscount(0);
                    } else {
                        ApplicationMain.customer.setDiscount(discountSet);
                    }
                    ApplicationMain.customer.setUsername(usernameSet);
                    ApplicationMain.customer.setFirstname(firstnameSet);
                    ApplicationMain.customer.setLastname(lastnameSet);
                    ApplicationMain.customer.setAddressLine(addressLineSet);
                    ApplicationMain.customer.setCity(citySet);
                    ApplicationMain.customer.setPostalcode(postalcodeSet);
                    ApplicationMain.customer.setEmail(emailSet);
                    ApplicationMain.customer.setTelephone(telephoneSet);
                    
                    ApplicationUI.listCustomers();
                    ApplicationUI.setCustomerDataUI();
                    setVisible(false);

                    // loginUI#Visible(false) if exist
                    LoginUI.fromLogin = false;
                    LoginUI.loginUI.setVisible(false);
                    if (permission == true) {
                        setVisible(false);
                    }
                    ApplicationUI.appUI.setVisible(true);
                    // !!! usernameExistLabel check from userUI button
    //                    usernameExistLabel.setVisible(true);

                ApplicationMain.stopConnection(con);

                } catch (SQLIntegrityConstraintViolationException inte) {
                    inte.printStackTrace();
                    System.out.println("Unique info exist");
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                    System.out.println("Data set error");
                    errorFormatLabel.setVisible(true);
                } catch (SQLException ex) {
                   ex.printStackTrace();
                   System.out.println("Data not uploaded");
                } 
            }
        } catch (InvalidPasswordException ipe) {
            System.out.println("Invalid password: " + passwordField.getPassword());
            ipe.showMessage();
        }
    }//GEN-LAST:event_createUserButtonActionPerformed

    private void updatePasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePasswordButtonActionPerformed
        PasswordUI.passUI.removeMessages();
        PasswordUI.passUI.setVisible(true);
        PasswordUI.showOldPassword();
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
    public static javax.swing.JLabel emailExistLabel;
    private static javax.swing.JTextField emailField;
    public static javax.swing.JLabel errorFormatLabel;
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
