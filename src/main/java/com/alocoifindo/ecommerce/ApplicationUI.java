/*
 * GNU General Public License v3.0
 */
package com.alocoifindo.ecommerce;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
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
public class ApplicationUI extends javax.swing.JFrame {
   
    static Order order = new Order();
    static MyChecklistTableModel tableModel = new MyChecklistTableModel();
    static DefaultListModel listModel = new DefaultListModel();
    static ApplicationUI appUI = new ApplicationUI();
    
    /**
     * Creates new form ApplicationUI
     */
    public ApplicationUI() {
        initComponents();
        setLocationRelativeTo(null);
        productsTableView();
        // Table Display Model
        productsTable.setRowHeight(50);
        TableColumnModel columnModel = productsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(4);
        columnModel.getColumn(1).setPreferredWidth(20);
        columnModel.getColumn(2).setPreferredWidth(160);
        columnModel.getColumn(3).setPreferredWidth(40);
        columnModel.getColumn(4).setPreferredWidth(110);
        columnModel.getColumn(5).setPreferredWidth(30);
        columnModel.getColumn(6).setPreferredWidth(40);
    }

    // Checklist Table !!! Image BLOB implent needed !!!
    public static class MyChecklistTableModel extends DefaultTableModel implements TableModelListener{
        public MyChecklistTableModel() {
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

        // Listener of checkbox !!! to change after image BLOB (column 1 to 2)
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();

            TableModel model = (TableModel)e.getSource();
            Object data = model.getValueAt(row, 0);

            if (data.equals(true)) {
                listModel.addElement(model.getValueAt(row, 2)); 
                itemsField.setText(String.valueOf(listModel.getSize()));
            } else if (data.equals(false)) {
                listModel.removeElement(model.getValueAt(row, 2));
                itemsField.setText(String.valueOf(listModel.getSize()));
            }
        }
    }
    
    private static void productsTableView() {
        try {
            Connection con = ApplicationMain.startConnection();
        
            String selectProductsSQL = "SELECT Image, CONCAT(brand, \" \", model_name) AS Product, Category, Keywords, price_per_day, discount_per_day FROM Products";
            PreparedStatement stmtProducts = con.prepareStatement(selectProductsSQL);
            ResultSet rsProducts = stmtProducts.executeQuery();
            
            while (rsProducts.next()) {
                // Preparing Icon and get from ResultSet
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
                // Get rest of ResultSet
                String product = rsProducts.getString("Product");
                String category = rsProducts.getString("Category");
                String keywords = rsProducts.getString("Keywords");
                int pricePerDay = rsProducts.getInt("price_per_day");
                String pricePerDayDisplay = pricePerDay + " €";
                int discountPerDay = rsProducts.getInt("discount_per_day");
                String discountPerDayDisplay = discountPerDay + " %";
                Object[] ProductRow = {false, icon, product, category, keywords, pricePerDayDisplay, discountPerDayDisplay};
                tableModel.addRow(ProductRow);
            }
        } catch (SQLException ex) {
            System.out.println("Problem in SQL Represent");
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

        tableScrollPane = new javax.swing.JScrollPane();
        productsTable = new javax.swing.JTable();
        createOrderButton = new javax.swing.JButton();
        daysField = new javax.swing.JTextField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        productsTable.setAutoCreateRowSorter(true);
        productsTable.setModel(tableModel);
        tableScrollPane.setViewportView(productsTable);

        createOrderButton.setText("Create Order");

        daysField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel1.setText("Days");

        totalPriceField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setText("Total Price");

        logoLabel.setFont(new java.awt.Font("Birthstone Bounce", 0, 30)); // NOI18N
        logoLabel.setText("Rent Your Stuff");

        customerSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        customerSelect.setSelectedItem(getName());
        customerSelect.setName("Customer"); // NOI18N

        jLabel4.setText("to:");

        itemsList.setModel(listModel);
        itemsScrollPane.setViewportView(itemsList);

        itemsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        discountField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setText("Discount");

        jLabel6.setText("Items");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tableScrollPane))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(daysField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(totalPriceField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(createOrderButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                            .addComponent(itemsScrollPane))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(logoLabel)
                        .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(itemsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(itemsField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(discountField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(createOrderButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(daysField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(totalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
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
    private javax.swing.JTextField daysField;
    private javax.swing.JTextField discountField;
    private static javax.swing.JTextField itemsField;
    private javax.swing.JList<String> itemsList;
    private javax.swing.JScrollPane itemsScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel logoLabel;
    public static javax.swing.JTable productsTable;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTextField totalPriceField;
    // End of variables declaration//GEN-END:variables
}
