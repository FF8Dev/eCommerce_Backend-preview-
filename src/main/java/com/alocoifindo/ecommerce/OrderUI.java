/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author facundoferreyra
 */
public class OrderUI extends javax.swing.JFrame {

    static OrderChecklistTableModel orderTableModel = new OrderChecklistTableModel();
    static OrderUI orderUI = new OrderUI();
    static List<Double> finalPricesBfCDList = new ArrayList<>();
    static List<Double> finalPricesList = new ArrayList<>();
    static Map<Integer, Double> finalPricesMap = new HashMap<Integer, Double>();
    static int counter = 0;
    static Map<Integer, Double> finalPricesUncheckMap = new HashMap<Integer, Double>();
    static List<Double> pricePerDayList = new ArrayList<>();
    static List<Integer> discountPerDayList = new ArrayList<>();
    static double finalPriceSum = 0.0;
    
    static int discountCustomer;
    
    /**
     * Creates new form OrderUI
     */
    public OrderUI() {
        initComponents();
        setLocationRelativeTo(null);
        
            orderTable.setRowHeight(25);
            TableColumnModel columnModel = orderTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(4);  // Select
            columnModel.getColumn(1).setPreferredWidth(160); // Product
            columnModel.getColumn(2).setPreferredWidth(30);// Base Price
            columnModel.getColumn(3).setPreferredWidth(30); // Days
            columnModel.getColumn(4).setPreferredWidth(50);// Discount/Day
            columnModel.getColumn(5).setPreferredWidth(50); // Price on Days
            columnModel.getColumn(6).setPreferredWidth(30); // Customer Discount
            columnModel.getColumn(7).setPreferredWidth(50); // Price with Discount
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            
            
            JTableHeader header = orderTable.getTableHeader();
            header.setDefaultRenderer(new HeaderRenderer(orderTable));
            
            
            orderTable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );
            orderTable.getColumnModel().getColumn(4).setCellRenderer( centerRenderer );
            orderTable.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
            orderTable.getColumnModel().getColumn(6).setCellRenderer( centerRenderer );
            orderTable.getColumnModel().getColumn(7).setCellRenderer( centerRenderer );
    }

    private static class HeaderRenderer implements TableCellRenderer {

    DefaultTableCellRenderer renderer;
    int HEADER_HEIGHT = 18;
    
    public HeaderRenderer(JTable table) {
        renderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setPreferredSize(new Dimension(100,HEADER_HEIGHT));
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int col) {
        return renderer.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, col);
    }
}
    
    public static class OrderChecklistTableModel extends DefaultTableModel implements TableModelListener{
        String priceWithSymbol;
        double pricePerProduct; 
        String discountWithSymbol;
        int discountPerProduct;
        double discountComma;
        boolean listedProduct = false;
        
        
        // add tableListener in this & Column Identifiers
        public OrderChecklistTableModel() {
            super(new String[]{"Select", "Product", "Base Price", "Days", "Discount/Day", "Price on Days", "User Disc.", "Final Price"}, 0);
            addTableModelListener(this);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
          Class clazz = String.class;
          switch (columnIndex) {
            case 0:
                clazz = Boolean.class;
                break;
          case 3:
            clazz = Integer.class;
            break;
          }
          return clazz;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            switch (column) {
                case 0:
                    return column == 0;
                case 3:
                    return column == 3;
                default:
                    return false;
          }
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            
            if (aValue instanceof Boolean && column == 0) {                     // Select
                Vector rowData = (Vector)getDataVector().get(row);
                rowData.set(0, (boolean)aValue);
                fireTableCellUpdated(row, column);
                
            }  else if (3 == column) {                                          // Days
                Vector rowData = (Vector)getDataVector().get(row);
                int days = (Integer) aValue;
                rowData.set(3, days);
                System.out.println("Data entry in Days Cell: " + days);
                
                System.out.println("setValueAt Row: " + row);
                pricePerProduct = pricePerDayList.get(row);
                double pricePerMoreDay = (pricePerProduct * ((100.0 - discountPerDayList.get(row)) / 100));
                double pricePerDays = pricePerProduct + ((days -1) * pricePerMoreDay);
                double finalPriceWithCD = pricePerDays * ((100.0 - discountCustomer) / 100);

                finalPricesMap.remove(row);
                finalPricesMap.put(row, finalPriceWithCD);
                finalPricesUncheckMap.put(row, finalPriceWithCD);

                System.out.println("Value of product per Day: " + pricePerProduct);
                System.out.println("Value of product per each MoreDay: " + pricePerMoreDay);
                
                String pricePerProductDisplay = (String.format("%.2f", pricePerProduct)) + " €";
                String pricePerDaysDisplay = (String.format("%.2f", pricePerDays)) + " €";
                String finalPriceWithCDDisplay = (String.format("%.2f", finalPriceWithCD)) + " €";
                String priceZeroDisplay = (String.format("%.2f", 00.00)) + " €";
                
                if (days > 1) {
                    rowData.set(5, pricePerDaysDisplay);
                    rowData.set(7, finalPriceWithCDDisplay);
                } else if (days <= 0) {
                    rowData.set(5, priceZeroDisplay);
                    rowData.set(7, priceZeroDisplay);
                } else {
                    rowData.set(5, pricePerProductDisplay);
                    rowData.set(7, finalPriceWithCDDisplay);
                }
                
                updateFinalPrice();
                updateTaxes();
                fireTableCellUpdated(row, column);
            }                                                                   // Price on days
        }

        // Listener of checkbox // (row, 0) = Select checkmark // (row, 2) = Product // (row, 5) = Price //
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int col = e.getColumn();
            TableModel tableModel = (TableModel) e.getSource();
            if (ApplicationMain.DEBUG) {
                System.out.println("Order Row changed nº" + row);
            }
            if (col >= 0) {
                // checkmark = boolean data
                Object datacheck = tableModel.getValueAt(row, 0);;

                if (col == 3) {

                    int daysRow = (Integer)tableModel.getValueAt(row, 3);
                    System.out.println("Day uploaded to SQL after tableChanged: " + daysRow);

                    int productRow = ApplicationMain.productsInOrder.get(row).getId();
                    System.out.println("productsInOrder: " + ApplicationMain.productsInOrder.get(row).getProductName());

                    try {
                        Connection con = ApplicationMain.startConnection();
                        // UPDATE row 3 "Days" to form daysPerProduct
                        PreparedStatement stmtUpdDays = con.prepareStatement("UPDATE order_line SET days=? WHERE id_product=?");

                            stmtUpdDays.setInt(1, daysRow);
                            stmtUpdDays.setInt(2, productRow);
                            System.out.println("id_product update in order_line: " + productRow);
                            System.out.println("days updated in order_line: " + daysRow);

                            stmtUpdDays.executeUpdate();

                        ApplicationMain.stopConnection(con);
                    } catch (SQLException ex) {
                        System.out.println("order_line INSERT failed");
                        ex.printStackTrace();
                    }
                }

                // if checkmark true
                if (datacheck.equals(true)) {
                    int daysRow = (Integer)tableModel.getValueAt(row, 3);
                    int productRow = ApplicationMain.productsInOrder.get(row).getId();

                    // INSERTO into SQL ´order_line´ Temp
                    try {
    //                    ApplicationMain.totalDays = (Integer)tableModel.getValueAt(row, 3);
                        Connection con = ApplicationMain.startConnection();

                        // !!! id_order_line not match the order num !!!
                        PreparedStatement stmtIns = con.prepareStatement("INSERT IGNORE INTO order_line (id_product, id_order, days) VALUES (?, ?, ?)");
                        System.out.println("Product ID insert ignore into order_line: " + productRow);
                        System.out.println("Days Product insert by checkmark=true: " + daysRow);    

                            stmtIns.setInt(1, productRow);
                            stmtIns.setInt(2, ApplicationMain.order.getId());
                            stmtIns.setInt(3, daysRow);
                            stmtIns.executeUpdate();

                        ApplicationMain.stopConnection(con);
                    } catch (SQLException ex) {
                        System.out.println("order_line INSERT failed");
                        ex.printStackTrace();
                    }
                    
                    if (listedProduct == true) {
                        System.out.println("finalPricesUncheckMap: " + finalPricesUncheckMap.get(row));
                        finalPricesMap.put(row, finalPricesUncheckMap.get(row));
                    }
    
                // if checkmark false edit order_line
                } else if (datacheck.equals(false)) {
                    int productRow = ApplicationMain.productsInOrder.get(row).getId();

                    // DELETE from SQL ´order_line´ Temp
                    try {
                        Connection con = ApplicationMain.startConnection();

                        PreparedStatement stmtDel = con.prepareStatement("DELETE FROM order_line WHERE id_product=?;");
                        System.out.println("Product ID deleted from order_line: " + productRow);

                            stmtDel.setInt(1, productRow);
                            stmtDel.executeUpdate();

                        ApplicationMain.stopConnection(con);
                    } catch (SQLException ex) {
                        System.out.println("order_line DELETE failed");
                        ex.printStackTrace();
                    }
                    if (ApplicationMain.DEBUG) {
                        System.out.println("Value to retrieve from order_line: " + finalPricesMap.get(row) + " (from row): " + row);
                    }
                    finalPricesUncheckMap.put(row, finalPricesMap.get(row));
                    finalPricesMap.remove(row);
                    listedProduct = true; 
                }
            }
            updateFinalPrice();
            updateTaxes();
        }
    }
    
    public static void orderTableView() {
        orderTableModel.setRowCount(0);
        ApplicationMain.productsInOrder.clear();
        pricePerDayList.clear();
        finalPricesMap.clear();
        counter = 0;
        try {
            Connection con = ApplicationMain.startConnection();

            String selectProductsOrderSQL = "SELECT \n" +
                                            "orders.id_tocustomer,\n" +
                                            "order_line.id_order,\n" +
                                            "order_line.id_product,\n" +
                                            "CONCAT(brand, \" \", model_name) AS Product, \n" +
                                            "price_per_day, \n" +
                                            "days, \n" +
                                            "discount_per_day, \n" +
                                            "customers.discount\n" +
                                            "FROM Products \n" +
                                            "INNER JOIN order_line ON Products.id_product = order_line.id_product\n" +
                                            "INNER JOIN Orders ON order_line.id_order = Orders.id_order\n" +
                                            "INNER JOIN Customers ON Orders.id_toCustomer=Customers.id_user\n" +
                                            "INNER JOIN Users ON Customers.id_user = Users.id_user\n" +
                                            "WHERE Orders.id_order=?";
            
            PreparedStatement stmtProductsOrder = con.prepareStatement(selectProductsOrderSQL);
            // implement change User
            stmtProductsOrder.setInt(1, ApplicationMain.order.getId());

            ResultSet rsProductsOrder = stmtProductsOrder.executeQuery();
            
            while (rsProductsOrder.next()) {
                // Get ResultSet of Products of the Order
                int idProduct = rsProductsOrder.getInt("id_product");
                String productName = rsProductsOrder.getString("Product");
                int days = rsProductsOrder.getInt("days");
                System.out.println("Day retrieved from SQL to Table after SELECT: " + days);
                
                // price_per_day
                double pricePerDay = rsProductsOrder.getDouble("price_per_day");
                String pricePerDayDisplay = (String.format("%.2f", pricePerDay)) + " €";
                pricePerDayList.add(pricePerDay);
                
                // discount_per_day
                int discountPerDay = rsProductsOrder.getInt("discount_per_day");
                String discountPerDayDisplay = discountPerDay + " %";
                discountPerDayList.add(discountPerDay);
                
                // discount_per_customer
                discountCustomer = rsProductsOrder.getInt("discount");
                String discountCustomerDisplay = discountCustomer+ " %";
                
                // price_per_days (Before Customer Discount) & final_price_per_product
                double priceBfCD = 00.00;
                double finalPricePerProduct = 00.00;
                if (days > 1) {
                    priceBfCD = pricePerDay + ((pricePerDay * (days - 1)) * ((100.0 - discountPerDay) / 100));
                    finalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                } else {
                    priceBfCD = pricePerDay * days;
                    finalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                }
                String priceBfCDDisplay = String.format("%.2f", priceBfCD) + " €";
                String finalPricePerProductDisplay = String.format("%.2f", finalPricePerProduct) + " €";
                finalPricesMap.put(counter, finalPricePerProduct);
                counter++;
                ApplicationMain.productsInOrder.add(new Product(idProduct, productName, pricePerDay, discountPerDay));
                Object[] orderRow = {true, productName, pricePerDayDisplay, days, discountPerDayDisplay, priceBfCDDisplay, discountCustomerDisplay, finalPricePerProductDisplay};
                orderTableModel.addRow(orderRow);
                
            }
            ApplicationMain.stopConnection(con);
            
        } catch (SQLException ex) {
            System.out.println("Problem in SQL Table Represent");
            ex.printStackTrace();
        }
        
        orderUI.setVisible(true);
        updateFinalPrice();
        updateTaxes();
    }
    
    public static void updateFinalPrice() {
        finalPriceSum = 0.0;
        for (int i = 0; i < orderTableModel.getRowCount(); i++) {
            if (finalPricesMap.get(i) != null) {
                finalPriceSum += finalPricesMap.get(i);
            }
        }
        finalPriceField.setText(String.format("%.2f", finalPriceSum));
    }
    
    public static void updateTaxes() {
        double taxes = finalPriceSum * 0.21;
        taxesField.setText(String.format("%.2f", taxes));
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        customerLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        taxesField = new javax.swing.JTextField();
        finalPriceField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        invoiceButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        orderTable.setModel(orderTableModel);
        orderTable.setToolTipText("");
        jScrollPane1.setViewportView(orderTable);

        jLabel1.setText("Customer:");

        customerLabel.setText(ApplicationUI.tempUsername);

        jLabel2.setText("Taxes:");

        taxesField.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        finalPriceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Final Price:");

        invoiceButton.setText("Create Invoice");
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("€");

        jLabel5.setText("€");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerLabel)
                .addGap(264, 264, 264))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(invoiceButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taxesField, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(finalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(customerLabel))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(taxesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(finalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(invoiceButton)
                    .addComponent(cancelButton))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        System.out.println("Create Invoice ActionPerformed");
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        orderUI.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OrderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                orderUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    public static javax.swing.JLabel customerLabel;
    private static javax.swing.JTextField finalPriceField;
    private javax.swing.JButton invoiceButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable orderTable;
    private static javax.swing.JTextField taxesField;
    // End of variables declaration//GEN-END:variables
}
