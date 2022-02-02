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

    static String firstnameSQL;
    static String lastnameSQL;
    static String addressLineSQL;
    static String citySQL;
    static int postalcodeSQL;
    static int telephoneSQL;
    static String emailSQL;
    static int discountSQL;

    String usernameSet;
    String firstnameSet;
    String lastnameSet;
    String addressLineSet;
    String citySet;
    String postalcodeString;
    String telephoneString;
    int postalcodeSet;
    int telephoneSet;
    String emailSet;
    String discountString;
    int discountSet;
    // postalcodeSet, telephoneSet & discountSet getText in setUpNumericalSets()

    static boolean updateOK = false;
    static boolean justUpdatedUsername = false;
    static boolean justUpdatedEmail = false;

    /**
     * Creates new form UserUI
     */
    public UserUI() {
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);
        removeMessages();

        // postalcodeSet, telephoneSet & discountSet getText in setUpNumericalSets()
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
        if (RentMyStuff.DEBUGwin) {
            System.out.println("UserUI: windowClosing.");
        }

    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            //This will only be seen on standard output.
            System.out.println("UserUI: windowClosed.");
            emailExistLabel.setVisible(false);
            emailEmptyLabel.setVisible(false);
            errorFormatLabel.setVisible(false);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("UserUI: windowOpened.");
        }
        UserUI.removeMessages();
        UserUI.userUI.setUpButtons();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("UserUI: windowIconified.");
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("UserUI: windowDeiconified.");
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("UserUI: windowActivated.");
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
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
            // discountSQL#Visible if admin
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
        int customerId = RentMyStuff.customer.getId();

        try {
            Connection con = RentMyStuff.startConnection();
            if (RentMyStuff.DEBUG) {
                System.out.println("id_customer to callCustomerData: " + customerId);
            }
            PreparedStatement stmtSelCust = con.prepareStatement("SELECT * FROM Customers WHERE id_user=?");
            stmtSelCust.setInt(1, customerId);
            ResultSet rsSelCust = stmtSelCust.executeQuery();

            while (rsSelCust.next()) {
                firstnameSQL = rsSelCust.getString("firstname");
                lastnameSQL = rsSelCust.getString("lastname");
                addressLineSQL = rsSelCust.getString("address_line");
                citySQL = rsSelCust.getString("city");
                postalcodeSQL = rsSelCust.getInt("postalcode");
                telephoneSQL = rsSelCust.getInt("telephone");
                emailSQL = rsSelCust.getString("email");
                discountSQL = rsSelCust.getInt("discount");

                RentMyStuff.customer.setFirstname(firstnameSQL);
                RentMyStuff.customer.setLastname(lastnameSQL);
                RentMyStuff.customer.setAddressLine(addressLineSQL);
                RentMyStuff.customer.setCity(citySQL);
                RentMyStuff.customer.setPostalcode(postalcodeSQL);
                RentMyStuff.customer.setTelephone(telephoneSQL);
                RentMyStuff.customer.setEmail(emailSQL);
                RentMyStuff.customer.setDiscount(discountSQL);
            }
            permission = true;
            RentMyStuff.closeResultSet(rsSelCust);
            RentMyStuff.closeStatement(stmtSelCust);
            RentMyStuff.stopConnection(con);

        } catch (SQLIntegrityConstraintViolationException intex) {
            System.out.println("User exist");
            intex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Cannot select data from Customers WHERE customerId");
        }

        usernameField.setText(RentMyStuff.customer.getUsername());
        passwordField.setText(RentMyStuff.customer.getPassword());
        firstnameField.setText(RentMyStuff.customer.getFirstname());
        lastnameField.setText(RentMyStuff.customer.getLastname());
        if (RentMyStuff.customer.getTelephone() == 0) {
            telephoneField.setText("");
        } else {
            telephoneField.setText(String.valueOf(RentMyStuff.customer.getTelephone()));
        }
        emailField.setText(RentMyStuff.customer.getEmail());
        addressField.setText(RentMyStuff.customer.getAddressLine());
        cityField.setText(RentMyStuff.customer.getCity());
        if (postalcodeSQL == 00000) {
            postalcodeField.setText("");
        } else {
            postalcodeField.setText(String.format("%05d", RentMyStuff.customer.getPostalcode()));
        }
        discountField.setText(String.valueOf(RentMyStuff.customer.getDiscount()));
    }

    public static boolean usernameExist(String username) {
//        usernameExistLabel.setVisible(false);
        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtChckUsr = con.prepareStatement("SELECT username FROM Users WHERE username=?");
            stmtChckUsr.setString(1, username);

            ResultSet rsChckUsr = stmtChckUsr.executeQuery();
            if (rsChckUsr.next() && !rsChckUsr.getString(1).equals(RentMyStuff.customer.getUsername())) {
                throw new UsernameExistException();
            }

            RentMyStuff.closeResultSet(rsChckUsr);
            RentMyStuff.closeStatement(stmtChckUsr);
            RentMyStuff.stopConnection(con);
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

    public static boolean emailExist(String email) {
//        emailExistLabel.setVisible(false);
        if (!email.equals("")) {
            try {
                Connection con = RentMyStuff.startConnection();

                PreparedStatement stmtChckUsr = con.prepareStatement("SELECT email FROM Customers WHERE email=?");
                stmtChckUsr.setString(1, email);

                ResultSet rsChckUsr = stmtChckUsr.executeQuery();
                if (rsChckUsr.next() && !rsChckUsr.getString(1).equals(RentMyStuff.customer.getEmail())) {
                    throw new EmailExistException();
                }

                RentMyStuff.closeResultSet(rsChckUsr);
                RentMyStuff.closeStatement(stmtChckUsr);
                RentMyStuff.stopConnection(con);
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
        return true;
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
                char c = (char) i;

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
                char c = (char) i;
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

    public static void removeMessages() {
        usernameExistLabel.setVisible(false);
        emailErrorPanel.setVisible(false);
        emailExistLabel.setVisible(false);
        emailEmptyLabel.setVisible(false);
        errorFormatLabel.setVisible(false);
    }

    private void setUpSets() {
        usernameSet = usernameField.getText();
        firstnameSet = firstnameField.getText();
        lastnameSet = lastnameField.getText();
        addressLineSet = addressField.getText();
        citySet = cityField.getText();
        postalcodeString = postalcodeField.getText();
        telephoneString = telephoneField.getText();
        emailSet = emailField.getText().toLowerCase();
        discountString = discountField.getText();

        if (postalcodeString.equals("")) {
            postalcodeSet = 0;
        } else if (postalcodeString.matches("[0-9]+")) {
            postalcodeSet = Integer.parseInt(postalcodeString);
        } else {
            errorFormatLabel.setVisible(true);
            throw new NumberFormatException();
        }

        if (telephoneString.equals("")) {
            telephoneSet = 0;
        } else if (telephoneString.matches("[0-9]+")) {
            telephoneSet = Integer.parseInt(telephoneString);
        } else {
            errorFormatLabel.setVisible(true);
            throw new NumberFormatException();
        }

        if (discountString.equals("")) {
            discountSet = 0;
        } else if (discountString.matches("[0-9]+")) {
            discountSet = Integer.parseInt(discountString);
        } else {
            errorFormatLabel.setVisible(true);
            throw new NumberFormatException();
        }
    }

    static class UsernameExistException extends Exception {

        public UsernameExistException() {
            usernameExistLabel.setVisible(true);
            updateOK = false;
        }
    }

    static class EmailExistException extends Exception {

        public EmailExistException() {
            emailErrorPanel.setVisible(true);
            emailExistLabel.setVisible(true);
        }
    }

    static class EmailEmptyException extends Exception {

        public EmailEmptyException() {
            emailErrorPanel.setVisible(true);
            emailEmptyLabel.setVisible(true);
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
        updatePasswordButton = new javax.swing.JButton();
        usernameExistLabel = new javax.swing.JLabel();
        errorFormatLabel = new javax.swing.JLabel();
        emailErrorPanel = new javax.swing.JPanel();
        emailExistLabel = new javax.swing.JLabel();
        emailEmptyLabel = new javax.swing.JLabel();

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

        emailErrorPanel.setLayout(new java.awt.CardLayout());

        emailExistLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        emailExistLabel.setForeground(new java.awt.Color(255, 0, 51));
        emailExistLabel.setText("email already registered");
        emailErrorPanel.add(emailExistLabel, "card2");

        emailEmptyLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        emailEmptyLabel.setForeground(new java.awt.Color(255, 0, 51));
        emailEmptyLabel.setText("email cannot be empty");
        emailErrorPanel.add(emailEmptyLabel, "card3");

        getContentPane().add(emailErrorPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, 170, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        removeMessages();
        setUpSets();
        //default icon, custom title
        int n = JOptionPane.showConfirmDialog(userUI, "Would you like to update " + RentMyStuff.customer.getUsername() + "?", "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (RentMyStuff.DEBUG) {
            System.out.println("option decided ('0' means yes): " + n);
        }
        if (n == 0) {

            try {
                Connection con = RentMyStuff.startConnection();
                if (RentMyStuff.DEBUG) {
                    System.out.println("getUsername: " + RentMyStuff.customer.getUsername());
                }
                // Username change
                if (!usernameSet.equals(RentMyStuff.customer.getUsername())) {
                    updateOK = false;
                    if (!usernameExist(usernameSet)) {
                        String changeUsernameSQL = "UPDATE Users SET users.username=? WHERE id_user=?";
                        try {
                            PreparedStatement stmtChgUsrnm = con.prepareStatement(changeUsernameSQL);
                            stmtChgUsrnm.setString(1, usernameSet);
                            stmtChgUsrnm.setInt(2, RentMyStuff.customer.getId());

                            stmtChgUsrnm.executeUpdate();
                            RentMyStuff.closeStatement(stmtChgUsrnm);

                            RentMyStuff.customer.setUsername(usernameSet);
                            if (RentMyStuff.DEBUG) {
                                System.out.println("New username: " + usernameSet);
                            }
                            updateOK = true;
                            justUpdatedUsername = true;
                            if (!justUpdatedEmail) {
                                if (!emailSet.equals(emailSQL)) {
                                    emailExist(emailSet);
                                }
                            }
                        } catch (SQLException ex) {
                            System.out.println("Cannot update username in Users");
                        }
                    }
                } else if (usernameSet.equals(RentMyStuff.customer.getUsername())) {
                    updateOK = true;
                }

                if (!emailSet.equals(emailSQL) && !emailSet.equals("")) {
                    updateOK = false;
                    if (!emailExist(emailSet)) {
                        try {
                            PreparedStatement stmtUpdEmail = con.prepareStatement("UPDATE Customers SET email=? WHERE id_user=?");
                            stmtUpdEmail.setString(1, emailSet);
                            RentMyStuff.customer.setEmail(emailSet);
                            stmtUpdEmail.setInt(2, RentMyStuff.customer.getId());

                            stmtUpdEmail.executeUpdate();
                            RentMyStuff.closeStatement(stmtUpdEmail);
                            System.out.println("Email updated");
                            updateOK = true;
                            justUpdatedEmail = true;
                            if (!justUpdatedUsername) {
                                if (!usernameSet.equals(RentMyStuff.customer.getUsername())) {
                                    usernameExist(usernameSet);
                                }
                            }
                        } catch (SQLIntegrityConstraintViolationException ice) {
                            System.out.println("Email exists - FATAL ERROR");
                            emailErrorPanel.setVisible(true);
                            emailEmptyLabel.setVisible(true);
                        } catch (SQLException ex) {
                            System.out.println("Email not uploaded");
                            ex.printStackTrace();
                        }
                    }
                }

                if (!firstnameSet.equals(firstnameSQL) || !lastnameSet.equals(lastnameSQL)
                        || !addressLineSet.equals(addressLineSQL) || !citySet.equals(citySQL)
                        || postalcodeSet != postalcodeSQL || !telephoneField.getText().equals(String.valueOf(telephoneSQL))
                        || !discountField.getText().equals(String.valueOf(discountSQL))) {
                    updateOK = false;
                    // UPDATE Customers
                    String updateCustomerSQL = "UPDATE Customers SET firstname=?, lastname=?, address_line=?, city=?, postalcode=?, telephone=?, discount=?, last_update=NOW() WHERE id_user=?";
                    try {
                        PreparedStatement stmtUpdCustomer = con.prepareStatement(updateCustomerSQL);
                        stmtUpdCustomer.setString(1, firstnameSet);
                        stmtUpdCustomer.setString(2, lastnameSet);
                        stmtUpdCustomer.setString(3, addressLineSet);
                        stmtUpdCustomer.setString(4, citySet);
                        stmtUpdCustomer.setInt(5, postalcodeSet);
                        stmtUpdCustomer.setInt(6, telephoneSet);
                        RentMyStuff.customer.setFirstname(firstnameSet);
                        RentMyStuff.customer.setLastname(lastnameSet);
                        RentMyStuff.customer.setAddressLine(addressLineSet);
                        RentMyStuff.customer.setCity(citySet);
                        RentMyStuff.customer.setPostalcode(postalcodeSet);
                        RentMyStuff.customer.setTelephone(telephoneSet);

                        if (LoginUI.privileges == true) {
                            stmtUpdCustomer.setInt(7, discountSet);
                            RentMyStuff.customer.setDiscount(discountSet);
                        } else {
                            stmtUpdCustomer.setInt(7, RentMyStuff.customer.getDiscount());
                        }

                        // WHERE id_user is Customer selected
                        stmtUpdCustomer.setInt(8, RentMyStuff.customer.getId());

                        stmtUpdCustomer.executeUpdate();

                        RentMyStuff.closeStatement(stmtUpdCustomer);

                        updateOK = true;
                        if (!justUpdatedUsername || !justUpdatedEmail) {
                            if (!usernameSet.equals(RentMyStuff.customer.getUsername()) || !emailSet.equals(emailSQL)) {
                                usernameExist(usernameField.getText());
                                emailExist(emailField.getText());
                            }
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Cannot update customer data");
                        errorFormatLabel.setVisible(true);
                    } catch (SQLIntegrityConstraintViolationException ice) {
                        System.out.println("Email exists - FATAL ERROR");
                        emailErrorPanel.setVisible(true);
                        emailEmptyLabel.setVisible(true);
                    } catch (SQLException ex) {
                        System.out.println("Data not uploaded");
                        ex.printStackTrace();
                    }
                }

                RentMyStuff.stopConnection(con);
            } catch (SQLException ex) {
                System.out.println("Data not uploaded");
                ex.printStackTrace();
            }

            if (updateOK) {
                // Update appUI Data
                ApplicationUI.listCustomers();
                ApplicationUI.setCustomerDataUI();
                ApplicationUI.setDiscountInTable();
                setVisible(false);
                
                if (RentMyStuff.DEBUG) {
                    System.out.println("Data updated");
                }
            }
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void createUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUserButtonActionPerformed
        removeMessages();
        setUpSets();

        try {
            boolean usernameCheck = usernameExist(usernameSet);
            boolean emailCheck;
            if (!emailSet.equals("")) {
                emailCheck = emailExist(emailSet);
            } else {
                emailCheck = false;
            }
            passwordValidator(passwordField.getPassword());

            if (!usernameCheck && !emailCheck && !emailSet.equals("")) {
                try {
                    Connection con = RentMyStuff.startConnection();
                    con.setAutoCommit(false);

                    // if comes from SignUp = Find last ID for INSERT NEW USER
                    if (LoginUI.fromLogin) {
                        PreparedStatement lastId_stmt = con.prepareStatement("SELECT max(id_user) FROM Users");
                        ResultSet rsMaxId = lastId_stmt.executeQuery();
                        rsMaxId.next();
                        id_user_new = rsMaxId.getInt(1) + 1;
                        id_userCreator = id_user_new;

                        RentMyStuff.closeResultSet(rsMaxId);
                        RentMyStuff.closeStatement(lastId_stmt);
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

                        RentMyStuff.closeResultSet(rsId);
                        RentMyStuff.closeResultSet(rsMaxId);
                        RentMyStuff.closeStatement(idCheck);
                        RentMyStuff.closeStatement(lastId_stmt);
                    }

                    // UPDATE 
                    PreparedStatement stmtInsertUsr = con.prepareStatement("INSERT INTO Users SET id_user=?, username=?, password=MD5(?)");
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
                    stmtInsertCust.setString(7, emailSet);          // emailSQL set into SQL Statement
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

                    // RentMyStuff#Customer Set
                    RentMyStuff.customer.setId(id_user_new);
                    if (discountField.getText().equals("0")) {
                        RentMyStuff.customer.setDiscount(0);
                    } else {
                        RentMyStuff.customer.setDiscount(discountSet);
                    }
                    RentMyStuff.customer.setUsername(usernameSet);
                    RentMyStuff.customer.setFirstname(firstnameSet);
                    RentMyStuff.customer.setLastname(lastnameSet);
                    RentMyStuff.customer.setAddressLine(addressLineSet);
                    RentMyStuff.customer.setCity(citySet);
                    RentMyStuff.customer.setPostalcode(postalcodeSet);
                    RentMyStuff.customer.setEmail(emailSet);
                    RentMyStuff.customer.setTelephone(telephoneSet);

                    ApplicationUI.listCustomers();
                    ApplicationUI.setCustomerDataUI();
                    ApplicationUI.setDiscountInTable();
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

                    RentMyStuff.closeStatement(stmtInsertUsr);
                    RentMyStuff.closeStatement(stmtInsertCust);
                    RentMyStuff.stopConnection(con);

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
            } else if (emailSet.equals("")) {
                emailErrorPanel.setVisible(true);
                emailEmptyLabel.setVisible(true);
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
    public static javax.swing.JLabel emailEmptyLabel;
    private static javax.swing.JPanel emailErrorPanel;
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
