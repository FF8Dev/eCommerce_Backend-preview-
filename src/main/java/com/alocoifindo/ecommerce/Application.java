/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author facundoferreyra
 */
public class Application {

    static boolean DEBUG = true;
    static String username;
    static String pass;
    static String telephone;
    static String email;
    static String firstname;
    static String lastname;

    public Application() {
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Application.username = username;
    }

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        Application.pass = pass;
    }

    public static String getTelephone() {
        return telephone;
    }

    public static void setTelephone(String telephone) {
        Application.telephone = telephone;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Application.email = email;
    }

    public static void main(String[] args) throws SQLException {
        MainMenu();
    }

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

    static void MainMenu() throws SQLException {
        int option = 0;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            do {
                do {
                    System.out.println("**E-Commerce application***\n[enter '0' to exit when there's no option]\n\n");
                    System.out.println("What you want to do?\n");
                    System.out.println("1. Login");
                    System.out.println("2. Register");
                    System.out.println("3. Exit\n");
                    if (keyboard.hasNextInt()) {
                        option = keyboard.nextInt();
                        valid = true;
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid option\n");
                    }
                } while (option < 1 || option >= 4);
                switch (option) {
                    case 1:
                        System.out.println("\nLogin\n");
                        login();
                        break;
                    case 2:
                        System.out.println("\nRegister\n");
                        addUser();
                        break;
                    case 3:
                        System.out.println("\nThanks for your visit");
                }
            } while (option == 2);
        } while (!valid);
    }

    private static void login() throws SQLException {
        boolean valid = false;
        int type = 0;
        Scanner keyboard = new Scanner(System.in);
        do {
            System.out.println("Enter your user: ");
            username = keyboard.next();
            if (username.equals("0")) {
                break;
            }
            setUsername(username);
            System.out.println("Enter your password: ");
            pass = keyboard.next();
            if (pass.equals("0")) {
                break;
            }

            Connection con = startConnection();

            PreparedStatement sel = con.prepareStatement("SELECT user_type FROM Users WHERE username=? and pass=?");
            sel.setString(1, username);
            sel.setString(2, pass);
            ResultSet rs = sel.executeQuery();

            if (rs.next()) {
                type = rs.getInt(1);
                valid = true;
                if (DEBUG) {
                    System.out.println("The user type is: " + type);
                }
            } else {
                System.out.println("The user does not exist.\n");
            }

            try {
                con.close();
            } finally {
                con = null;
            }
        } while (!valid);

        if (valid) {
            if (type == 1) {
                MenuAdmin();
            } else if (type == 2) {
                MenuUser();
            }
        } else {
            MainMenu();
        }
    }

    private static void MenuAdmin() throws SQLException {
        int option = 0;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            do {
                do {
                    System.out.println("\n1. Add new Products");
                    System.out.println("2. Add new Admin");
                    System.out.println("3. Modify Credentials");
                    System.out.println("4. Exit\n");
                    if (keyboard.hasNextInt()) {
                        option = keyboard.nextInt();
                        valid = true;
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid option\n");
                    }
                } while (option < 0 || option > 4);
                switch (option) {
                    case 1:
                        addProduct();
                        break;
                    case 2:
                        addAdmin();
                        break;
                    case 3:
                        // implement modifyUserMenuAdmin()
                        modifyUserMenu();
                        break;
                    case 4:
                        System.out.println("\nThanks for your visit");
                        break;
                }
            } while (option != 4);
        } while (!valid);
    }

    private static void MenuUser() throws SQLException {
        int option = 0;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            do {
                do {
                    System.out.println("\n1. Show Products");
                    System.out.println("2. Modify Credentials");
                    System.out.println("3. Exit\n");
                    if (keyboard.hasNextInt()) {
                        option = keyboard.nextInt();
                        valid = true;
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid option\n");
                    }
                } while (option < 1 || option > 3);
                switch (option) {
                    case 1:
                        showProductsMenu();
                        break;
                    case 2:
                        modifyUserMenu();
                        break;
                    case 3:
                        System.out.println("\nThanks for your visit");
                        break;
                }
            } while (option != 3);
        } while (!valid);
    }

    private static void addUser() throws SQLException {

        int id = 1;

        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);

        do {
            System.out.println("\nInsert your name: ");
            firstname = keyboard.next();
            if (firstname.equals("0")) {
                break;
            }
            System.out.println("Insert your surname: ");
            lastname = keyboard.next();
            if (lastname.equals("0")) {
                break;
            }
            System.out.println("Insert your telephone: ");
            telephone = keyboard.next();
            if (telephone.equals("0")) {
                break;
            }
            System.out.println("Insert your email: ");
            email = keyboard.next();
            if (email.equals("0")) {
                break;
            }
            System.out.println("Insert your username: ");
            username = keyboard.next();
            if (username.equals("0")) {
                break;
            }
            System.out.println("Insert your password: ");
            pass = keyboard.next();
            if (pass.equals("0")) {
                break;
            }
            if (DEBUG) {
                System.out.println("\nUser: " + firstname + " " + lastname + " "
                        + telephone + " " + email + " " + username + " " + pass);
            }

            Connection c = startConnection();

            PreparedStatement sel = c.prepareStatement("SELECT * FROM Users WHERE username=? OR mail=? OR telephone=?");
            sel.setString(1, username);
            sel.setString(2, email);
            sel.setString(3, telephone);
            ResultSet r = sel.executeQuery();
            if (r.next()) {
                System.out.println("User exists");
            } else {
                valid = true;
            }
            try {
                c.close();
            } finally {
                c = null;
            }
        } while (!valid);

        if (valid) {
            Connection c = startConnection();

            PreparedStatement MaxId = c.prepareStatement("SELECT max(id) FROM Users");
            ResultSet r = MaxId.executeQuery();
            if (r.next()) {
                id = r.getInt(1) + 1;
            } else {
                id = 1;
            }
            if (DEBUG) {
                System.out.println("User ID: " + id);
            }

            PreparedStatement Insert = c.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?,?,?)");
            Insert.setInt(1, id);
            Insert.setString(2, firstname);
            Insert.setString(3, lastname);
            Insert.setString(4, telephone);
            Insert.setString(5, email);
            Insert.setString(6, username);
            Insert.setString(7, pass);
            Insert.setInt(8, 2);
            Insert.executeUpdate();

            try {
                c.close();
            } finally {
                c = null;
            }
        }
        System.out.println("done!");
    }

    private static void addProduct() throws SQLException {
        boolean exit;
        boolean valid = false;
        int id = 0;
        String prodName;
        int cod_prov = 0;
        int maxProvNum = 0;
        boolean validProv = false;
        double price = 0.00;
        int option = 0;
        Scanner keyboard = new Scanner(System.in);
        Connection c = startConnection();

        PreparedStatement MaxProv = c.prepareStatement("SELECT max(cod_prov) FROM Products");
        ResultSet rProv = MaxProv.executeQuery();
        if (rProv.next()) {
            maxProvNum = rProv.getInt(1);
        }

        PreparedStatement sel = c.prepareStatement("SELECT * FROM products WHERE productname=? AND cod_prov=?");

        do {
            do {
                exit = false;
                System.out.println("\nInsert the name: ");
                prodName = keyboard.next();
                if (prodName.equals("0")) {
                    exit = true;
                    break;
                }
                do {
                    System.out.println("Insert the provider code (1=SJ; 2=PB; 3=AI): ");
                    if (keyboard.hasNextInt()) {
                        cod_prov = keyboard.nextInt();
                        if (cod_prov == 0) {
                            exit = true;
                            break;
                        } else if (cod_prov > 0 && cod_prov <= maxProvNum) {
                            validProv = true;
                        } else {
                            keyboard.nextLine();
                            System.out.println("Invalid provider code\n");
                        }
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid character code\n");
                    }
                } while (!validProv);

                if (!exit) {
                    sel.setString(1, prodName);
                    sel.setInt(2, cod_prov);
                    ResultSet rProd = sel.executeQuery();
                    if (rProd.next()) {
                        System.out.println("Item exists.");
                    } else {
                        System.out.println("Insert the price: ");
                        price = keyboard.nextDouble();
                        if (price == 0.0) {
                            exit = true;
                            break;
                        }
                        valid = true;
                        exit = true;
                    }
                }
            } while (!exit);

            if (DEBUG) {
                System.out.println("Product: " + prodName + " Provider: " + cod_prov + " Price: " + price);
            }

            if (valid) {
                PreparedStatement MaxId = c.prepareStatement("SELECT max(id) FROM Products");
                ResultSet r = MaxId.executeQuery();
                if (r.next()) {
                    id = r.getInt(1) + 1;
                } else {
                    id = 1;
                }
                if (DEBUG) {
                    System.out.println("Product ID: " + id);
                }

                PreparedStatement insertProd = c.prepareStatement("INSERT INTO Products VALUES (?,?,?,?)");
                insertProd.setInt(1, id);
                insertProd.setString(2, prodName);
                insertProd.setInt(3, cod_prov);
                insertProd.setDouble(4, price);
                insertProd.executeUpdate();

                try {
                    c.close();
                } finally {
                    c = null;
                }

                System.out.println("Add successfull!\nYou want to add another product? 1.Yes 2.No");
                if (keyboard.hasNextInt()) {
                    option = keyboard.nextInt();
                    valid = true;
                } else {
                    keyboard.nextLine();
                    System.out.println("Invalid option\n");
                }
                keyboard.nextLine();
            }
        } while (option == 1);
    }

    private static void showProductsByCategory(String category) throws SQLException {
        Connection c = startConnection();
        PreparedStatement products;
        if (category == "All") {
            products = c.prepareStatement("SELECT products.*, providers.providername "
                    + "FROM products LEFT JOIN providers ON products.COD_PROV = providers.ID");
        } else {
            products = c.prepareStatement("SELECT products.*, providers.providername "
                    + "FROM products LEFT JOIN providers ON products.COD_PROV = providers.ID WHERE category = '" + category + "'");
        }
        ResultSet r = products.executeQuery();

        NumberFormat formatter = new DecimalFormat("#0.00");

        while (r.next()) {
            System.out.println("Code: " + r.getInt("ID"));
            System.out.println("Name: " + r.getString("PRODUCTNAME"));
            System.out.println("Price: " + formatter.format(r.getDouble("PRICE")) + "€");
            System.out.println("Code Provider: " + r.getInt("COD_PROV"));
            System.out.println("Provider Name: " + r.getString("PROVIDERNAME"));
            System.out.println("Category: " + r.getString("CATEGORY"));
            System.out.println("-----------------------");
        }
        try {
            c.close();
        } finally {
            c = null;
        }
    }

    private static void addAdmin() throws SQLException {
        int id = 1;
        String firstName;
        String lastName = "";

        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        int option = 0;
        do {
            do {
                System.out.println("\nInsert admin name: ");
                firstName = keyboard.next();
                if (firstName.equals("0")) {
                    break;
                }
                System.out.println("Insert admin surname: ");
                lastName = keyboard.next();
                if (lastName.equals("0")) {
                    break;
                }
                System.out.println("Insert admin telephone: ");
                telephone = keyboard.next();
                if (telephone.equals("0")) {
                    break;
                }
                System.out.println("Insert admin email: ");
                email = keyboard.next();
                if (email.equals("0")) {
                    break;
                }
                System.out.println("Insert admin username: ");
                username = keyboard.next();
                if (username.equals("0")) {
                    break;
                }
                System.out.println("Insert admin password: ");
                pass = keyboard.next();
                if (pass.equals("0")) {
                    break;
                }
                if (DEBUG) {
                    System.out.println("UserAdmin: " + firstName + " " + lastName + " "
                            + telephone + " " + email + " " + username + " " + pass);
                }

                Connection c = startConnection();

                PreparedStatement sel = c.prepareStatement("SELECT * FROM Users WHERE username=? OR mail=? OR telephone=?");
                sel.setString(1, username);
                sel.setString(2, email);
                sel.setString(3, telephone);
                ResultSet r = sel.executeQuery();
                if (r.next()) {
                    System.out.println("User exists");
                } else {
                    valid = true;
                }

                try {
                    c.close();
                } finally {
                    c = null;
                }
            } while (!valid);

            if (valid) {
                Connection c = startConnection();

                PreparedStatement MaxId = c.prepareStatement("SELECT max(id) FROM Users");
                ResultSet r = MaxId.executeQuery();
                if (r.next()) {
                    id = r.getInt(1) + 1;
                } else {
                    id = 1;
                }
                if (DEBUG) {
                    System.out.println("User ID:" + id);
                }

                PreparedStatement Insert = c.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?,?,?)");
                Insert.setInt(1, id);
                Insert.setString(2, firstName);
                Insert.setString(3, lastName);
                Insert.setString(4, telephone);
                Insert.setString(5, email);
                Insert.setString(6, username);
                Insert.setString(7, pass);
                Insert.setInt(8, 1);
                Insert.executeUpdate();

                try {
                    c.close();
                } finally {
                    c = null;
                }

                System.out.println("You want to add another admin? 1.Yes 2.No");
                if (keyboard.hasNextInt()) {
                    option = keyboard.nextInt();
                    valid = true;
                } else {
                    keyboard.nextLine();
                    System.out.println("Invalid option\n");
                }
            }
        } while (option == 1);

    }

    private static void modifyUserMenu() throws SQLException {
        int id = 1;
        int option = 0;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);

        while (!valid) {
            do {
                do {
                    System.out.println("\n1. Change Username");
                    System.out.println("2. Change Password");
                    System.out.println("3. Change Telephone");
                    System.out.println("4. Change Email");
                    System.out.println("5. Exit\n");
                    if (keyboard.hasNextInt()) {
                        option = keyboard.nextInt();
                        valid = true;
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid option\n");
                    }
                } while (option < 0 || option > 5);
                switch (option) {
                    case 1:
                        changeUsername();
                        break;
                    case 2:
                        changePassword();
                        break;
                    case 3:
                        changeTelephone();
                        break;
                    case 4:
                        changeEmail();
                        break;
                }
            } while (option != 5);
        }
    }

    // NOT IMPLEMENTED
    private static void modifyUserMenuAdmin() throws SQLException {
        int option = 0;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            do {
                do {
                    System.out.println("\n1. Change Username");
                    System.out.println("2. Change Password");
                    System.out.println("3. Change Telephone");
                    System.out.println("4. Change Email");
                    System.out.println("5. Change Product Info");
                    System.out.println("6. Exit\n");
                    if (keyboard.hasNextInt()) {
                        option = keyboard.nextInt();
                        valid = true;
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid option\n");
                    }
                } while (option < 0 || option > 6);
                switch (option) {
                    case 1:
                        changeUsername();
                        break;
                    case 2:
                        changePassword();
                        break;
                    case 3:
                        changeTelephone();
                        break;
                    case 4:
                        changeEmail();
                        break;
                    case 5:
                        changeProduct();
                        break;
                }
            } while (option != 6);
        } while (!valid);
    }

    private static void changeUsername() throws SQLException {
        String newUsername;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            System.out.println("\nYour username is: " + getUsername());
            System.out.println("Insert new username: ");
            newUsername = keyboard.next();
            if (newUsername.equals("0")) {
                break;
            }

            Connection c = startConnection();

            PreparedStatement sel = c.prepareStatement("SELECT * FROM Users WHERE username=?");
            sel.setString(1, newUsername);
            ResultSet r = sel.executeQuery();

            if (r.next()) {
                System.out.println("User exists");
            } else {
                valid = true;
                PreparedStatement upd = c.prepareStatement("UPDATE Users SET username = '" + newUsername + "'"
                        + "WHERE username = '" + getUsername() + "'");
                upd.executeUpdate();
                setUsername(newUsername);
            }
            try {
                c.close();
            } finally {
                c = null;
            }
        } while (!valid);

        if (valid) {
            System.out.println("done!\n");
        }
    }

    private static void changePassword() throws SQLException {
        String actualPass = "";
        String newPass;
        String confirmationPass;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);

        do {
            System.out.println("\nInsert your password: ");
            actualPass = keyboard.next();
            if (actualPass.equals("0")) {
                break;
            }

            if (actualPass.equals(getPass())) {
                Connection c = startConnection();
                PreparedStatement sel = c.prepareStatement("SELECT * FROM Users "
                        + "WHERE username='" + getUsername() + "' AND password='" + actualPass + "'");
                ResultSet r = sel.executeQuery();
                if (r.next()) {
                    valid = true;
                    System.out.println("Insert NEW password: ");
                    newPass = keyboard.next();
                    if (newPass.equals("0")) {
                        valid = false;
                        break;
                    }
                    System.out.println("Confirm NEW password: ");
                    confirmationPass = keyboard.next();
                    if (confirmationPass.equals("0")) {
                        valid = false;
                        break;
                    }
                    if (newPass.equals(confirmationPass)) {
                        setPass(newPass);
                        PreparedStatement upd = c.prepareStatement("UPDATE Users SET password = '" + newPass + "' WHERE username = '" + getUsername() + "'");
                        upd.executeUpdate();
                    }
                } else {
                    System.out.println("Wrong password.");
                }
                try {
                    c.close();
                } finally {
                    c = null;
                }
            } else {
                System.out.println("Actual password doesn't match");
            }
        } while (!valid);

        if (valid) {
            System.out.println("done!\n");
        }
    }

    private static void changeTelephone() throws SQLException {
        String newTelephone;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            Connection c = startConnection();

            PreparedStatement selMail = c.prepareStatement("SELECT telephone FROM Users WHERE username=?");
            selMail.setString(1, getUsername());
            ResultSet rm = selMail.executeQuery();

            while (rm.next()) {
                telephone = rm.getString("telephone");
            }
            rm.close();
            selMail.close();

            System.out.println("\nYour actual telephone is: " + getTelephone());
            System.out.println("Insert new telephone: ");
            newTelephone = keyboard.next();
            if (newTelephone.equals("0")) {
                break;
            }

            PreparedStatement sel = c.prepareStatement("SELECT * FROM Users WHERE telephone=?");
            sel.setString(1, newTelephone);
            ResultSet r = sel.executeQuery();

            valid = true;
            PreparedStatement upd = c.prepareStatement("UPDATE Users SET telephone = '" + newTelephone + "'"
                    + "WHERE telephone = '" + getTelephone() + "'");
            upd.executeUpdate();
            setEmail(newTelephone);

            try {
                c.close();
            } finally {
                c = null;
            }
        } while (!valid);

        if (valid) {
            System.out.println("done!\n");
        }
    }

    private static void changeEmail() throws SQLException {
        String newEmail;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);
        do {
            Connection c = startConnection();

            PreparedStatement selMail = c.prepareStatement("SELECT mail FROM Users WHERE username=?");
            selMail.setString(1, getUsername());
            ResultSet rm = selMail.executeQuery();

            while (rm.next()) {
                email = rm.getString("mail");
            }
            rm.close();
            selMail.close();

            System.out.println("\nYour actual email is: " + getEmail());
            System.out.println("Insert new email: ");
            newEmail = keyboard.next();
            if (newEmail.equals("0")) {
                break;
            }

            PreparedStatement sel = c.prepareStatement("SELECT * FROM Users WHERE mail=?");
            sel.setString(1, newEmail);
            ResultSet r = sel.executeQuery();

            if (r.next()) {
                System.out.println("Email in use for another user");
            } else {
                valid = true;
                PreparedStatement upd = c.prepareStatement("UPDATE Users SET mail = '" + newEmail + "'"
                        + "WHERE mail = '" + getEmail() + "'");
                upd.executeUpdate();
                setEmail(newEmail);
            }
            try {
                c.close();
            } finally {
                c = null;
            }
        } while (!valid);

        if (valid) {
            System.out.println("done!\n");
        }
    }

    private static void changeProduct() throws SQLException {

        Connection c = startConnection();

        PreparedStatement products = c.prepareStatement("SELECT products.*, providers.providername "
                + "FROM products LEFT JOIN providers ON products.COD_PROV = providers.ID");
        ResultSet r = products.executeQuery();

        NumberFormat formatter = new DecimalFormat("#0.00");

        while (r.next()) {
            System.out.println("Code: " + r.getInt("ID"));
            System.out.println("Name: " + r.getString("PRODUCTNAME"));
            System.out.println("Category: " + r.getString("CATEGORY"));
            System.out.println("Price: " + formatter.format(r.getDouble("PRICE")) + "€");
            System.out.println("Code Provider: " + r.getInt("COD_PROV"));
            System.out.println("Provider Name: " + r.getString("PROVIDERNAME"));
            System.out.println("-----------------------");
        }
        try {
            c.close();
        } finally {
            c = null;
        }

//        do {
//            System.out.println("\nInsert your password: ");
//            actualPass = keyboard.next();
//
//            if (actualPass.equals(getPass())){
//                Connection con = startConnection();
//                PreparedStatement sel = con.prepareStatement("SELECT * FROM Users "
//                        + "WHERE username='" + getUsername() + "' AND password='" + actualPass + "'");
//                ResultSet r = sel.executeQuery();
//                if (r.next()) {
//                    valid = true;
//                    System.out.println("Insert NEW password: ");
//                    newPass = keyboard.next();    
//                    System.out.println("Confirm NEW password: ");
//                    confirmationPass = keyboard.next();
//                    if (newPass.equals(confirmationPass)) {
//                        setPass(newPass);
//                        PreparedStatement upd = con.prepareStatement("UPDATE Users SET password = '" + newPass + "' WHERE username = '" + getUsername() + "'");
//                        upd.executeUpdate();
//                    }
//                } 
//                else {
//                    System.out.println("Wrong password.");
//                }
//                try {
//                    con.close();
//                } finally {
//                    con = null;
//                }
//            } else {
//                System.out.println("Actual password doesn't match");
//            }
//        } while (!valid);
//        System.out.println
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void showProductsMenu() throws SQLException {
        int option = 0;
        boolean valid = false;
        Scanner keyboard = new Scanner(System.in);

        do {
            do {
                do {
                    System.out.println("\nWhich category of product want to see?");
                    System.out.println("1. Meat & Fish");
                    System.out.println("2. Spices & Specialities");
                    System.out.println("3. Prepared Food");
                    System.out.println("4. All");
                    System.out.println("5. Exit\n");

                    if (keyboard.hasNextInt()) {
                        option = keyboard.nextInt();
                        valid = true;
                    } else {
                        keyboard.nextLine();
                        System.out.println("Invalid option");
                    }
                } while (option < 1 || option > 5);
                switch (option) {
                    case 1:
                        showProductsByCategory("Meat & Fish");
                        break;
                    case 2:
                        showProductsByCategory("Spices & Specialities");
                        break;
                    case 3:
                        showProductsByCategory("Prepared Food");
                        break;
                    case 4:
                        showProductsByCategory("All");
                        break;
                }
            } while (option != 5);
        } while (!valid);
    }

}
