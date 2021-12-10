/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author facundoferreyra
 */
public class ApplicationUI extends javax.swing.JFrame implements WindowListener {
    static int idOrder;
    
    static List<Customer> customers = new ArrayList<Customer>();
    static String tempUsername = "default_customer";
    
    static int counter = 0;
    static Map<Integer, Double> pricePerDayMap = new HashMap<Integer, Double>();
    static Map<Integer, Integer> discountPerDayMap = new HashMap<Integer, Integer>();
    static Map<Integer, Boolean> selectedProduct = new HashMap<Integer,Boolean>();
    
    static DefaultComboBoxModel customersComboBoxModel = new DefaultComboBoxModel();
    static discountByCustomerListener comboListener = new discountByCustomerListener();
    static ProductsChecklistTableModel productsTableModel = new ProductsChecklistTableModel();
    static DefaultListModel listModel = new DefaultListModel();
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 0, 90, 1);
    static ApplicationUI appUI = new ApplicationUI();
    
    /**
     * Creates new form ApplicationUI
     */
    public ApplicationUI() {
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);
        
        int customerId = ApplicationMain.customer.getId();
        System.out.println("!!!: " + customerId);
        setOrderId();
        
        // customerID starts at 2 in admin session
        if (customerId == 2){
            customerLabel.setVisible(false);
            listCustomers();
        } else {
//            listCustomers(
            //retrieve comboBox to jLabel
            customerSelect.setVisible(false);
            customerSelect.getItemAt(customerId);
            
            // !!! retrieve discountField if discount == 0
            if (ApplicationMain.customer.getDiscount() == 0) {
                discountLabel.setVisible(false);
                discountField.setVisible(false);
            }
        }
        
        totalPriceField.setText("0.00");
        productsTableView();
        // Table Display Model
        productsTable.setRowHeight(50);
        TableColumnModel columnModel = productsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(4);  // Select
        columnModel.getColumn(1).setPreferredWidth(17); // Image
        columnModel.getColumn(2).setPreferredWidth(160);// Product
        columnModel.getColumn(3).setPreferredWidth(41); // Category
        columnModel.getColumn(4).setPreferredWidth(102);// Keywords
        columnModel.getColumn(5).setPreferredWidth(30); // Price/Day
        columnModel.getColumn(6).setPreferredWidth(50); // Discount/Day
        
        setCustomerDataUI();
        
        daysSpinner.addChangeListener(new daysListener());
        }
    
    @Override
    public void windowClosing(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("WindowListener method called: windowClosing.");
        }
        String tempStatus;
        
        try {
            Connection con = ApplicationMain.startConnection();
            
            PreparedStatement stmtSelTempOrd = con.prepareStatement("SELECT shipment_status FROM Orders WHERE id_order = ?");
            stmtSelTempOrd.setInt(1, idOrder);
            ResultSet rsTempOrd = stmtSelTempOrd.executeQuery();
            rsTempOrd.next();
            tempStatus = rsTempOrd.getString("shipment_status");
            if (tempStatus.equals("Not Finished")) {
                PreparedStatement stmtDelTempOrdLn = con.prepareStatement("DELETE FROM order_line WHERE id_order = ?");
                stmtDelTempOrdLn.setInt(1, idOrder);
                stmtDelTempOrdLn.executeUpdate();
                
                PreparedStatement stmtDelTempOrd = con.prepareStatement("DELETE FROM Orders WHERE id_order = ?");
                stmtDelTempOrd.setInt(1, idOrder);
                stmtDelTempOrd.executeUpdate();
                
                if (ApplicationMain.DEBUG) {
                    System.out.println("DELETED actual Temporal Order");
                }
            }
            
            ApplicationMain.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("Cannot DELETE Temporary Order");
            ex.printStackTrace();
        }
    }
    
    @Override
    public void windowClosed(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            //This will only be seen on standard output.
            System.out.println("ApplicationUI: windowClosed.");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
           System.out.println("ApplicationUI: windowOpened.");
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("ApplicationUI: windowIconified.");
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("ApplicationUI: windowDeiconified.");
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("ApplicationUI: windowActivated.");
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (ApplicationMain.DEBUGwin) {
            System.out.println("ApplicationUI: windowDeactivated.");
        }
    }

    private void setOrderId() {
        try {
            Connection con = ApplicationMain.startConnection();
            
            PreparedStatement stmtRstOrderLine = con.prepareStatement("DELETE FROM order_line");
            PreparedStatement stmtRstIncrem = con.prepareStatement("ALTER TABLE order_line AUTO_INCREMENT = 1");
            PreparedStatement stmtRstOrderNotFinished = con.prepareStatement("DELETE FROM orders WHERE shipment_status = 'Not Finished'");
            
            
            stmtRstOrderLine.executeUpdate();
            stmtRstIncrem.execute();
            stmtRstOrderNotFinished.executeUpdate();
            
            PreparedStatement stmtOrderId = con.prepareStatement("SELECT MAX(id_order) AS id_order FROM Orders");
            ResultSet rsOrdId = stmtOrderId.executeQuery();
            rsOrdId.next();
            idOrder = rsOrdId.getInt("id_order") +1;
            ApplicationMain.order.setId(idOrder);
            if (ApplicationMain.DEBUG) {
                System.out.println("next order_id: " + idOrder);
            }
            
            PreparedStatement stmtOrdCreate = con.prepareStatement("INSERT INTO orders VALUES (?, NOW(), 1, null, 'Not Finished', NOW(), ?, ?)");
            stmtOrdCreate.setInt(1, idOrder);
            int userOrder;
            // if user cames from SignUp
            if (LoginUI.idUser == 0) {
                userOrder = ApplicationMain.customer.getId();
            } else {
                userOrder = LoginUI.idUser;
            }
            stmtOrdCreate.setInt(2, userOrder);
            stmtOrdCreate.setInt(3, ApplicationMain.customer.getId());
            if (ApplicationMain.DEBUG) {
                System.out.println("idUser (0 if cames from signUp): " + LoginUI.idUser);
                System.out.println("idUser by ApplicationMain#customer: " + ApplicationMain.customer.getId());
            }
            stmtOrdCreate.executeUpdate();
            
            ApplicationMain.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("Cannot DELETE FROM order_line TABLE OR SELECT MAX(id_order) OR INSERT NEW order");
            ex.printStackTrace();
        }
        
        ApplicationMain.order.setId(idOrder);
    }
    
    public static void listCustomers() {
        customerSelect.removeAllItems();
        customerSelect.removeItemListener(comboListener);
        customersComboBoxModel.removeAllElements();
        customers.clear();
        
        try {
            Connection con = ApplicationMain.startConnection();
            String selectCustomersSQL = "SELECT * FROM Users NATURAL JOIN Customers WHERE users.id_user = customers.id_user ORDER BY id_user ASC";
            PreparedStatement stmtCustomers = con.prepareStatement(selectCustomersSQL);
            ResultSet rsCustomers = stmtCustomers.executeQuery();

            while (rsCustomers.next()) {

                String username = rsCustomers.getString("username");
                String password = rsCustomers.getString("password");
                String firstname = rsCustomers.getString("firstname");
                String lastname = rsCustomers.getString("lastname");
                String addressLine = rsCustomers.getString("address_line");
                int postalcode = rsCustomers.getInt("postalcode");
                String city = rsCustomers.getString("city");
                String email = rsCustomers.getString("email");
                int telephone = rsCustomers.getInt("telephone");
                int discount = rsCustomers.getInt("discount");

                Customer tempCustomer = new Customer(username, password, firstname, lastname, addressLine, postalcode, city, email, telephone, discount);
                customers.add(tempCustomer);
            } 
            for (Customer customer : customers) {
                customersComboBoxModel.addElement(customer.getUsername());
            }
            ApplicationMain.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("SQL Error in ComboBox");
            ex.printStackTrace();
        }
    }
    
    public static void setCustomerDataUI() {
        customerLabel.setText(ApplicationMain.customer.getUsername());
//        customersComboBoxModel.setSelectedItem(ApplicationMain.customer.getUsername());
//        customerSelect.setSelectedItem(ApplicationMain.customer.getUsername());
        discountField.setText(String.valueOf(ApplicationMain.customer.getDiscount()));
        if (LoginUI.privileges) {
            customerSelect.setSelectedItem(ApplicationMain.customer.getUsername());
            System.out.println("Selected Item: " + ApplicationMain.customer.getUsername());
        }
        customerSelect.addItemListener(comboListener);
    }
    
    public static class discountByCustomerListener implements ItemListener{
        
        Customer listenerCustomer = new Customer();
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                // add to tempUsername username of list & to ApplicationMain.customer
                tempUsername = item.toString();
                ApplicationMain.customer.setUsername(tempUsername);
                System.out.println("tempUsername: " + tempUsername);
                OrderUI.customerLabel.setText(tempUsername);
                
                try {
                    Connection con = ApplicationMain.startConnection();
                    String selectDiscountsSQL = "SELECT id_user, username, discount FROM Users NATURAL JOIN Customers WHERE users.id_user = customers.id_user AND username=? ORDER BY id_user ASC;";
                    PreparedStatement stmtDiscounts = con.prepareStatement(selectDiscountsSQL);
                    stmtDiscounts.setString(1, tempUsername);
                    ResultSet rsCustSelected = stmtDiscounts.executeQuery();
                    rsCustSelected.next();
                    
                    int tempId = rsCustSelected.getInt("id_user");
                    int tempDiscount = rsCustSelected.getInt("discount");
                    
                    
                    if (ApplicationMain.DEBUG) {
                        System.out.println("TempId customer selected: " + tempId);
                        System.out.println("Discount customer selected: " + tempDiscount);
                    }
                    
                    String updateOrderSQL = "UPDATE Orders SET id_tocustomer=? WHERE id_order=?";
                    PreparedStatement stmtUpdOrd = con.prepareStatement(updateOrderSQL);
                    stmtUpdOrd.setInt(1, tempId);
                    stmtUpdOrd.setInt(2, idOrder);
                    stmtUpdOrd.executeUpdate();
                    
                    // Update ApplicationMain.customer && set discount in fieldUI
                    ApplicationMain.customer.setId(tempId);
                    ApplicationMain.customer.setDiscount(tempDiscount);
                    discountField.setText(String.valueOf(tempDiscount));
                    
                    ApplicationMain.stopConnection(con);
                } catch (SQLException ex) {
                      System.out.println("Error while setting Discount percentage OR Update id_toCustomer");
                      ex.printStackTrace(); 
                }
                updateTotalPrice(ApplicationMain.totalDays);
           }
        }       
    }
    
    // Checklist Table
    public static class ProductsChecklistTableModel extends DefaultTableModel implements TableModelListener{
        boolean listedProduct = false;
        String priceWithSymbol;
        double pricePerProduct; 
        String discountWithSymbol;
        int discountPerProduct;
        double discountComma;
        
        // add tableListener in this & Column Identifiers
        public ProductsChecklistTableModel() {
            super(new String[]{"Select", "Image", "Product", "Category", "Keywords", "Price/day", "Discount/day"}, 0);
            addTableModelListener(this);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
          Class clazz = String.class;
          switch (columnIndex) {
            case 0:
                clazz = Boolean.class;
                break;
            case 1:
                clazz = ImageIcon.class;
                break;
           
          }
          return clazz;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
          return column == 0;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
          if (aValue instanceof Boolean && column == 0) {
            Vector rowData = (Vector)getDataVector().get(row);
            rowData.set(0, (boolean)aValue);
            fireTableCellUpdated(row, column);
          }
        }

        // Listener of checkbox // (row, 0) = Select checkmark // (row, 2) = Product // (row, 5) = Price //
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            if (ApplicationMain.DEBUG) {
                System.out.println("Row ProductTable changed nº" + row);
            }
            
            TableModel model = (TableModel)e.getSource();
            // data = checkmark
            Object data = model.getValueAt(row, 0);

            // if checkmark true
            if (data.equals(true)) {
                // product list add
                listModel.addElement(model.getValueAt(row, 2)); 
                itemsField.setText(String.valueOf(listModel.getSize()));
                
                // total pricePerProduct add
                priceWithSymbol = model.getValueAt(row, 5).toString();
                pricePerProduct = Double.parseDouble(priceWithSymbol.replaceAll("[^0-9.]", ""));
                
                // total discountPerProduct add
                discountWithSymbol = model.getValueAt(row, 6).toString();
                discountPerProduct = Integer.parseInt(discountWithSymbol.replaceAll("[^0-9]", ""));
                discountComma = (100.0 - discountPerProduct) / 100.0;
                
                // INSERTO into SQL ´order_line´ Temp
                try {
                    Connection con = ApplicationMain.startConnection();
                    
                    // !!! id_order_line not match the order num !!!
                    PreparedStatement stmtIns = con.prepareStatement("INSERT INTO order_line (id_product, id_order, days) VALUES (?, ?, ?);");
                    System.out.println("id_product insert into order_line: " + ApplicationMain.products.get(row).getId());
                    
                        stmtIns.setInt(1, ApplicationMain.products.get(row).getId());
                        stmtIns.setInt(2, ApplicationMain.order.getId());
                        stmtIns.setInt(3, ApplicationMain.totalDays);
                        stmtIns.executeUpdate();
                    
                    ApplicationMain.stopConnection(con);
                } catch (SQLException ex) {
                    System.out.println("order_line INSERT failed");
                    ex.printStackTrace();
                }
                
                // Final Price set
                System.out.println("setValueAt Row: " + row);
                selectedProduct.put(row, true);
                updateTotalPrice(ApplicationMain.totalDays);
                
                // activate remove item values
                listedProduct = true;
                
            // if checkmark false
            } else if (data.equals(false)) {
                // product list remove
                listModel.removeElement(model.getValueAt(row, 2));
                itemsField.setText(String.valueOf(listModel.getSize()));
                
                // if already placed as item
                if (listedProduct == true) {
                    // total pricePerProduct remove
                    priceWithSymbol = model.getValueAt(row, 5).toString();
                    pricePerProduct = Double.parseDouble(priceWithSymbol.replaceAll("[^0-9.]", ""));
                    
                    // total discountPerProduct remove
                    discountWithSymbol = model.getValueAt(row, 6).toString();
                    discountPerProduct = Integer.parseInt(discountWithSymbol.replaceAll("[^0-9]", ""));
                    discountComma = (100.0 - discountPerProduct) / 100.0;
                    
                    // DELETE from SQL ´order_line´ Temp
                    try {
                        Connection con = ApplicationMain.startConnection();

                        PreparedStatement stmtDel = con.prepareStatement("DELETE FROM order_line WHERE id_product=?;");
                        System.out.println("Product ID deleted from order_line: " + ApplicationMain.products.get(row).getId());
                        stmtDel.setInt(1, ApplicationMain.products.get(row).getId());
                        stmtDel.executeUpdate();

                        ApplicationMain.stopConnection(con);
                    } catch (SQLException ex) {
                        System.out.println("order_line DELETE failed");
                        ex.printStackTrace();
                    }
                    
                    // Final Price set
                    selectedProduct.put(row, false);
                    updateTotalPrice(ApplicationMain.totalDays);
                }
            }
        }
    }
    
    private static void productsTableView() {
        try {
            Connection con = ApplicationMain.startConnection();
        
            String selectProductsSQL = "SELECT Image, CONCAT(brand, \" \", model_name) AS Product, Category, Keywords, price_per_day, discount_per_day, id_product FROM Products";
            PreparedStatement stmtProducts = con.prepareStatement(selectProductsSQL);
            ResultSet rsProducts = stmtProducts.executeQuery();
            
            while (rsProducts.next()) {
                // Preparing Icon and get from ResultSetof Products
                ImageIcon icon = null;
                InputStream is = rsProducts.getBinaryStream("Image"); 
                // Decode the inputstream as BufferedImage
                try {
                    BufferedImage bufImg = null;
                    bufImg = ImageIO.read(is);
                    Image image = bufImg;
                    icon =new ImageIcon(image);
                } catch (IOException ioe) {
                    System.out.println("Error catching image");
                    ioe.printStackTrace();
                }
                // Get rest of ResultSet of Products
                String productName = rsProducts.getString("Product");
                String category = rsProducts.getString("Category");
                String keywords = rsProducts.getString("Keywords");
                double pricePerDay = rsProducts.getDouble("price_per_day");
                // format price_per_day
                String pricePerDayDisplay = (String.format("%.2f", pricePerDay)) + " €";
                int discountPerDay = rsProducts.getInt("discount_per_day");
                // format discount_per_day
                String discountPerDayDisplay = discountPerDay + " %";
                int idProduct = rsProducts.getInt("id_product");
                
                pricePerDayMap.put(counter, pricePerDay);
                discountPerDayMap.put(counter, discountPerDay);
                counter++;
                
                ApplicationMain.products.add(new Product(idProduct, productName, pricePerDay, discountPerDay));
                
                Object[] productRow = {false, icon, productName, category, keywords, pricePerDayDisplay, discountPerDayDisplay};
                productsTableModel.addRow(productRow);
            }
            ApplicationMain.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("Problem in SQL Table Represent");
            ex.printStackTrace();
        }
    }
        
    public class daysListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSpinner spinner = (JSpinner) e.getSource();
            ApplicationMain.totalDays = (int)spinner.getValue();
            
            try {
                Connection con = ApplicationMain.startConnection();

                String updDaysOrderSQL = "UPDATE Orders SET total_days=? WHERE id_order=?";
                String updDaysOrderLineSQL = "UPDATE order_line SET days=? WHERE id_order=?";
                PreparedStatement stmtUpdDay = con.prepareStatement(updDaysOrderSQL);
                stmtUpdDay.setInt(1, ApplicationMain.totalDays);
                stmtUpdDay.setInt(2, idOrder);
                stmtUpdDay.executeUpdate();
                System.out.println("totalDays in Order: " + ApplicationMain.totalDays);
                PreparedStatement stmtUpdDayLine = con.prepareStatement(updDaysOrderLineSQL);
                stmtUpdDayLine.setInt(1, ApplicationMain.totalDays);
                stmtUpdDayLine.setInt(2, idOrder);
                stmtUpdDayLine.executeUpdate();
            
            } catch (SQLException ex) {
                System.out.println("Cannot UPDATE total_days in Order");
                ex.printStackTrace();
            }
            updateTotalPrice(ApplicationMain.totalDays);
        }
    }
    
    public static void updateTotalPrice(int days) {
        double finalPriceSum = 0.0;
        for (int i = 0; i < productsTableModel.getRowCount(); i++) {
            if (selectedProduct.get(i) != null && selectedProduct.get(i) != false) {
                double pricePerProduct = pricePerDayMap.get(i);
                double pricePerMoreDay = (pricePerProduct * ((100.0 - discountPerDayMap.get(i)) / 100));
                double pricePerDays;
                if (days == 0) {
                    pricePerDays = 0.0;
                } else {
                    pricePerDays = pricePerProduct + ((days -1) * pricePerMoreDay);
                }
                double finalPriceWithCD = pricePerDays * ((100.0 - ApplicationMain.customer.getDiscount()) / 100);
                finalPriceSum += finalPriceWithCD;
            }
        }
        totalPriceField.setText(String.format("%.2f", finalPriceSum));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        tableScrollPane = new javax.swing.JScrollPane();
        productsTable = new javax.swing.JTable();
        createOrderButton = new javax.swing.JButton();
        daysLabel = new javax.swing.JLabel();
        totalPriceField = new javax.swing.JTextField();
        totalPriceLabel = new javax.swing.JLabel();
        logoLabel = new javax.swing.JLabel();
        customerSelect = new javax.swing.JComboBox<>();
        toLabel = new javax.swing.JLabel();
        itemsScrollPane = new javax.swing.JScrollPane();
        itemsList = new javax.swing.JList<>();
        itemsField = new javax.swing.JTextField();
        discountField = new javax.swing.JTextField();
        discountLabel = new javax.swing.JLabel();
        itemsLabel = new javax.swing.JLabel();
        userButton = new javax.swing.JButton();
        invoiceButton = new javax.swing.JButton();
        shippingButton = new javax.swing.JButton();
        productAddButton = new javax.swing.JButton();
        daysSpinner = new javax.swing.JSpinner();
        customerLabel = new javax.swing.JLabel();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RentMyStuff");

        productsTable.setAutoCreateRowSorter(true);
        productsTable.setModel(productsTableModel);
        tableScrollPane.setViewportView(productsTable);

        createOrderButton.setText("Create Order");
        createOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createOrderButtonActionPerformed(evt);
            }
        });

        daysLabel.setText("Days");

        totalPriceField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        totalPriceLabel.setText("Total Price");

        logoLabel.setFont(new java.awt.Font("Birthstone Bounce", 0, 30)); // NOI18N
        logoLabel.setText("Rent My Stuff");

        customerSelect.setModel(customersComboBoxModel);
        customerSelect.setName("Customer"); // NOI18N

        toLabel.setFont(new java.awt.Font("Birthstone Bounce", 0, 18)); // NOI18N
        toLabel.setText("to:");

        itemsScrollPane.setPreferredSize(new java.awt.Dimension(275, 147));

        itemsList.setModel(listModel);
        itemsScrollPane.setViewportView(itemsList);

        itemsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        discountField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        discountLabel.setText("Discount");

        itemsLabel.setText("Items");

        userButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/customer_20.png"))); // NOI18N
        userButton.setBorder(null);
        userButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userButtonActionPerformed(evt);
            }
        });

        invoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/invoice_20-04.png"))); // NOI18N
        invoiceButton.setBorder(null);
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });

        shippingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/truck_20-03.png"))); // NOI18N
        shippingButton.setBorder(null);
        shippingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shippingButtonActionPerformed(evt);
            }
        });

        productAddButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add-to-collection_20-02.png"))); // NOI18N
        productAddButton.setBorder(null);
        productAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productAddButtonActionPerformed(evt);
            }
        });

        daysSpinner.setModel(spinnerModel);
        daysSpinner.setValue(1);

        customerLabel.setFont(new java.awt.Font("Birthstone Bounce", 0, 26)); // NOI18N
        customerLabel.setText(ApplicationMain.customer.getUsername() + "    ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(toLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(userButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(invoiceButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(shippingButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(productAddButton)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(customerLabel)
                            .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(discountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(discountField))
                                .addGap(58, 58, 58)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(itemsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(itemsField))
                                .addGap(34, 34, 34))
                            .addComponent(itemsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(daysLabel))
                                    .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(totalPriceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(totalPriceField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(createOrderButton))
                            .addComponent(tableScrollPane))))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logoLabel)
                            .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toLabel)
                            .addComponent(customerLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userButton)
                            .addComponent(invoiceButton)
                            .addComponent(shippingButton)
                            .addComponent(productAddButton))
                        .addGap(58, 58, 58))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(itemsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemsLabel)
                    .addComponent(discountLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(itemsField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(discountField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(daysLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(createOrderButton)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        UserUI.emailExistLabel.setVisible(false);
        UserUI.userUI.setUpButtons();
        UserUI.userUI.setVisible(true);        
    }//GEN-LAST:event_userButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void shippingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shippingButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_shippingButtonActionPerformed

    private void productAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productAddButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productAddButtonActionPerformed

    private void createOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createOrderButtonActionPerformed
        OrderUI.orderTableView();
    }//GEN-LAST:event_createOrderButtonActionPerformed

    
    
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
            java.util.logging.Logger.getLogger(ApplicationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ApplicationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ApplicationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ApplicationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                appUI.setVisible(true);    
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createOrderButton;
    public static javax.swing.JLabel customerLabel;
    public static javax.swing.JComboBox<String> customerSelect;
    private javax.swing.JLabel daysLabel;
    private static javax.swing.JSpinner daysSpinner;
    private static javax.swing.JTextField discountField;
    private javax.swing.JLabel discountLabel;
    private javax.swing.JButton invoiceButton;
    public static javax.swing.JTextField itemsField;
    private javax.swing.JLabel itemsLabel;
    private javax.swing.JList<String> itemsList;
    private javax.swing.JScrollPane itemsScrollPane;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton productAddButton;
    public static javax.swing.JTable productsTable;
    private javax.swing.JButton shippingButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JLabel toLabel;
    private static javax.swing.JTextField totalPriceField;
    private javax.swing.JLabel totalPriceLabel;
    private javax.swing.JButton userButton;
    // End of variables declaration//GEN-END:variables
}
