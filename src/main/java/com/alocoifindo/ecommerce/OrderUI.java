/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author facundoferreyra
 */
public class OrderUI extends javax.swing.JFrame {

    static OrderChecklistTableModel orderTableModel = new OrderChecklistTableModel();
    static OrderUI orderUI = new OrderUI();
    static List<Double> finalPrice = new ArrayList<>();
    static double finalPriceSum = 0.0;
    
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
        
        
    }

    public static class OrderChecklistTableModel extends DefaultTableModel implements TableModelListener{
        boolean listedProduct = false;
        String priceWithSymbol;
        double pricePerProduct; 
        String discountWithSymbol;
        int discountPerProduct;
        double discountComma;
        
        // add tableListener in this & Column Identifiers
        public OrderChecklistTableModel() {
            super(new String[]{"Select", "Product", "Base Price", "Days", "Discount/Day", "Price on Days", "Customer Discount", "Price with Discount"}, 0);
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
//            double priceBfCD = 1.0;
            
            if (aValue instanceof Boolean && column == 0) {
                Vector rowData = (Vector)getDataVector().get(row);
                rowData.set(0, (boolean)aValue);
                fireTableCellUpdated(row, column);
            }  else if(3 == column) {
                Vector rowData = (Vector)getDataVector().get(row);
                rowData.set(3, (Integer) aValue);
                fireTableCellUpdated(row, column);
                
//                try {
//                    Connection con = ApplicationMain.startConnection();
//                    String updateDaysSQL = "UPDATE IGNORE INTO order_line SET days=? WHERE id_product=?";
//                    PreparedStatement stmtUpdDay = con.prepareStatement(updateDaysSQL);
//                    stmtUpdDay.setInt(1, (Integer)aValue);
//                    stmtUpdDay.setInt(2, row);
//                } catch (SQLException ex) {
//                    System.out.println("Cannot Update days in order_line");
//                    ex.printStackTrace();
//                }
            }
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
            
            TableModel model = (TableModel)e.getSource();
            // data = checkmark
            Object datacheck = false;
            

            try {
                if (row >= 0 && col >= 0) {
                    datacheck = tableModel.getValueAt(row, col);

                    boolean ishotKeyColEmpty = false;
                    if (tableModel.getValueAt(row, 0) != null) {
                        ishotKeyColEmpty = tableModel.getValueAt(row, 0).toString().isEmpty();
                    }
                    if (datacheck != null && datacheck.toString().equals("") && !ishotKeyColEmpty) {
                        tableModel.setValueAt("", row, 0);
                    }
                }
                if (row >=0 && col == 3) {
                    try {
                    Connection con = ApplicationMain.startConnection();

                    // !!! UPDATE row 3 "Days" (daysPerProduct) to form daysPerProduct
                    PreparedStatement stmtUpdDays = con.prepareStatement("UPDATE order_line SET days=? WHERE id_product=?");
                        System.out.println("Product ID insert into order_line: " + ApplicationMain.products.get(row).getId());
                        System.out.println("days (tableModel.getValueAt(row, 3): )" + tableModel.getValueAt(row, 3));
                        System.out.println("order_line id_product ApplicationMain.products.get(row).getId(): )" + ApplicationMain.products.get(row).getId());
                        
                        stmtUpdDays.setInt(1, (Integer)tableModel.getValueAt(row, 3));
                        stmtUpdDays.setInt(2, ApplicationMain.products.get(row).getId());
                        stmtUpdDays.executeUpdate();

                    ApplicationMain.stopConnection(con);
                    } catch (SQLException ex) {
                        System.out.println("order_line INSERT failed");
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
                    
            // if checkmark true
            if (datacheck.equals(true)) {
//                // product list add
//                ApplicationUI.listModel.addElement(model.getValueAt(row, 2)); 
//                ApplicationUI.itemsField.setText(String.valueOf(ApplicationUI.listModel.getSize()));
                
//                // total pricePerProduct add
//                priceWithSymbol = model.getValueAt(row, 5).toString();
//                pricePerProduct = Double.parseDouble(priceWithSymbol.replaceAll("[^0-9.]", ""));
//                
//                // total discountPerProduct add
//                discountWithSymbol = model.getValueAt(row, 6).toString();
//                discountPerProduct = Integer.parseInt(discountWithSymbol.replaceAll("[^0-9]", ""));
//                discountComma = (100.0 - discountPerProduct) / 100.0;
                
                // INSERTO into SQL ´order_line´ Temp
                try {
                    Connection con = ApplicationMain.startConnection();
                    
                    // !!! id_order_line not match the order num !!!
                    PreparedStatement stmtIns = con.prepareStatement("INSERT IGNORE INTO order_line (id_product, id_order, days) VALUES (?, ?, ?)");
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


                
//             // add item in pricePerProduct by totalDays for totalPriceField
//                if (totalDays == 1) {
//                    finalPrice += pricePerProduct;
//                    totalPriceField.setText(String.format("%.2f", finalPrice));
//                } else {
//                    finalPrice = pricePerProduct + (((pricePerProduct * (totalDays-1)) * discountComma));
//                    totalPriceField.setText(String.format("%.2f", finalPrice));
//                }
                


                // activate remove item values
                listedProduct = true;
            
                
            // if checkmark false !!! edit order_line
            } else if (datacheck.equals(false)) {
//                // product list remove
//                ApplicationUI.listModel.removeElement(model.getValueAt(row, 2));
//                ApplicationUI.itemsField.setText(String.valueOf(ApplicationUI.listModel.getSize()));
                
                // if already placed as item
                if (listedProduct == true) {
//                    // total pricePerProduct remove
//                    priceWithSymbol = model.getValueAt(row, 5).toString();
//                    pricePerProduct = Double.parseDouble(priceWithSymbol.replaceAll("[^0-9.]", ""));
//                    
//                    // total discountPerProduct remove
//                    discountWithSymbol = model.getValueAt(row, 6).toString();
//                    discountPerProduct = Integer.parseInt(discountWithSymbol.replaceAll("[^0-9]", ""));
//                    discountComma = (100.0 - discountPerProduct) / 100.0;
                    
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
                    
//                 // remove item in pricePerProduct by totalDays for totalPriceField
//                    if (totalDays == 1) {
//                        finalPrice -= pricePerProduct;
//                        totalPriceField.setText(String.format("%.2f", finalPrice));
//                    } else {
//                        finalPrice = pricePerProduct - (((pricePerProduct * (totalDays-1)) * discountComma));
//                        totalPriceField.setText(String.format("%.2f", finalPrice));
//                    }
                }
            }
        }
    }
    
    public static void orderTableView() {
        orderTableModel.setRowCount(0);
        try {
            Connection con = ApplicationMain.startConnection();

            String selectProductsOrderSQL = "SELECT \n" +
                                            "orders.id_tocustomer,\n" +
                                            "order_line.id_order,\n" +
                                            "CONCAT(brand, \" \", model_name) AS Product, \n" +
                                            "price_per_day, \n" +
                                            "total_days, \n" +
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
                String productName = rsProductsOrder.getString("Product");
                int days = rsProductsOrder.getInt("total_days");
                
                // price_per_day
                double pricePerDay = rsProductsOrder.getDouble("price_per_day");
                String pricePerDayDisplay = (String.format("%.2f", pricePerDay)) + " €";
                
                // discount_per_day
                int discountPerDay = rsProductsOrder.getInt("discount_per_day");
                String discountPerDayDisplay = discountPerDay + " %";
                
                // discount_per_customer
                int discountCustomer = rsProductsOrder.getInt("discount");
                String discountCustomerDisplay = discountCustomer+ " %";
                
                // price_per_days (Before Customer Discount) & final_price_per_product
                double priceBfCD;
                double finalPricePerProduct;
                if (days > 1) {
                    priceBfCD = pricePerDay + ((pricePerDay * (days - 1)) * ((100.0 - discountPerDay) / 100));
                    finalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                } else {
                    priceBfCD = pricePerDay * days;
                    finalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                }
                String priceBfCDDisplay = String.format("%.2f", priceBfCD) + " €";
                String finalPricePerProductDisplay = String.format("%.2f", finalPricePerProduct) + " €";
                
                finalPrice.add(finalPricePerProduct);
                Object[] orderRow = {true, productName, pricePerDayDisplay, days, discountPerDayDisplay, priceBfCDDisplay, discountCustomerDisplay, finalPricePerProductDisplay};
                orderTableModel.addRow(orderRow);
                
            }
            ApplicationMain.stopConnection(con);
            
        } catch (SQLException ex) {
            System.out.println("Problem in SQL Table Represent");
            ex.printStackTrace();
        }
        
    }
    
    public static void updateFinalPrice() {
        for (int i=0; i < finalPrice.size(); i++) {
                finalPriceSum += finalPrice.get(i);
            }
        
        finalPriceField.setText(String.format("%.2f", finalPriceSum));
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
    private javax.swing.JTextField taxesField;
    // End of variables declaration//GEN-END:variables
}
