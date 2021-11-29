/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
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
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
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
    
    static double totalProductPricePerDay = 0;
    static double totalPrice;
    
    static DefaultComboBoxModel customersComboBoxModel = new DefaultComboBoxModel();
    static ProductsChecklistTableModel productsTableModel = new ProductsChecklistTableModel();
    static DefaultListModel listModel = new DefaultListModel();
    static ApplicationUI appUI = new ApplicationUI();
    
    /**
     * Creates new form ApplicationUI
     */
    public ApplicationUI() {
        initComponents();
        setLocationRelativeTo(null);
        
        ApplicationMain.customer.setId(2);
        setOrderId();
        
        
        listCustomers();
        customerSelect.addItemListener(new discountByCustomerListener());
        
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
        
        discountField.setText("0");
        totalPriceField.setText("0.00");
        
        daysSpinner.addChangeListener(new daysListener());
        
        addWindowListener(this);
        }
    
    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("WindowListener method called: windowClosing.");
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
        //This will only be seen on standard output.
        System.out.println("WindowListener method called: windowClosed.");
    }

    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("WindowListener method called: windowOpened.");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("WindowListener method called: windowIconified.");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("WindowListener method called: windowDeiconified.");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        System.out.println("WindowListener method called: windowActivated.");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("WindowListener method called: windowDeactivated.");
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
            stmtOrdCreate.setInt(2, LoginUI.idUser);
            stmtOrdCreate.setInt(3, ApplicationMain.customer.getId());
            if (ApplicationMain.DEBUG) {
                System.out.println("idUser: " + LoginUI.idUser);
            }
            stmtOrdCreate.executeUpdate();
            
            ApplicationMain.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("Cannot DELETE FROM order_line TABLE OR SELECT MAX(id_order) OR INSERT NEW order");
            ex.printStackTrace();
        }
        
        ApplicationMain.order.setId(idOrder);
    }

    public class daysListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSpinner spinner = (JSpinner) e.getSource();
            ApplicationMain.totalDays = (int)spinner.getValue();
            
            try {
            Connection con = ApplicationMain.startConnection();
            
            String updateDaysSQL = "UPDATE Orders SET total_days=? WHERE id_order=?";
            PreparedStatement stmtUpdDay = con.prepareStatement(updateDaysSQL);
            stmtUpdDay.setInt(1, ApplicationMain.totalDays);
            stmtUpdDay.setInt(2, idOrder);
            stmtUpdDay.executeUpdate();
            
            } catch (SQLException ex) {
                System.out.println("Cannot UPDATE total_days in Order");
                ex.printStackTrace();
            }
        }
    }
    
    public static void listCustomers() {
        
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
    
    public class discountByCustomerListener implements ItemListener{
        
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
                    System.out.println("Product ID insert into order_line: " + ApplicationMain.products.get(row).getId());
                    
                        stmtIns.setInt(1, ApplicationMain.products.get(row).getId());
                        stmtIns.setInt(2, ApplicationMain.order.getId());
                        stmtIns.setInt(3, ApplicationMain.totalDays);
                        stmtIns.executeUpdate();
                    
                    ApplicationMain.stopConnection(con);
                } catch (SQLException ex) {
                    System.out.println("order_line INSERT failed");
                    ex.printStackTrace();
                }
                
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
        jLabel1 = new javax.swing.JLabel();
        totalPriceField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        logoLabel = new javax.swing.JLabel();
        customerSelect = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        itemsScrollPane = new javax.swing.JScrollPane();
        itemsList = new javax.swing.JList<>();
        itemsField = new javax.swing.JTextField();
        discountField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        userButton = new javax.swing.JButton();
        invoiceButton = new javax.swing.JButton();
        shippingButton = new javax.swing.JButton();
        productAddButton = new javax.swing.JButton();
        daysSpinner = new javax.swing.JSpinner();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RentYourStuff");

        productsTable.setAutoCreateRowSorter(true);
        productsTable.setModel(productsTableModel);
        tableScrollPane.setViewportView(productsTable);

        createOrderButton.setText("Create Order");
        createOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createOrderButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Days");

        totalPriceField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setText("Total Price");

        logoLabel.setFont(new java.awt.Font("Birthstone Bounce", 0, 30)); // NOI18N
        logoLabel.setText("Rent Your Stuff");

        customerSelect.setModel(customersComboBoxModel);
        customerSelect.setName("Customer"); // NOI18N

        jLabel4.setText("to:");

        itemsList.setModel(listModel);
        itemsScrollPane.setViewportView(itemsList);

        itemsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        discountField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setText("Discount");

        jLabel6.setText("Items");

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

        daysSpinner.setValue(1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(userButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(invoiceButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(shippingButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(productAddButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(discountField))
                                .addGap(58, 58, 58)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(itemsField))
                                .addGap(34, 34, 34))
                            .addComponent(itemsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(jLabel1))
                                    .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userButton)
                            .addComponent(invoiceButton)
                            .addComponent(shippingButton)
                            .addComponent(productAddButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(itemsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(itemsField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(discountField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(createOrderButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        UserUI.callCustomerData();
        
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
//        OrderUI.updateFinalPrice();
        OrderUI.orderUI.setVisible(true);
        
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
    private javax.swing.JComboBox<String> customerSelect;
    private static javax.swing.JSpinner daysSpinner;
    private static javax.swing.JTextField discountField;
    private javax.swing.JButton invoiceButton;
    public static javax.swing.JTextField itemsField;
    private javax.swing.JList<String> itemsList;
    private javax.swing.JScrollPane itemsScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton productAddButton;
    public static javax.swing.JTable productsTable;
    private javax.swing.JButton shippingButton;
    private javax.swing.JScrollPane tableScrollPane;
    private static javax.swing.JTextField totalPriceField;
    private javax.swing.JButton userButton;
    // End of variables declaration//GEN-END:variables
}
