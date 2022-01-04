/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author facundoferreyra
 */
public class ShipmentUI extends javax.swing.JFrame implements WindowListener {

    static ShipmentTableModel shipmentTableModel = new ShipmentTableModel();
    static ShipmentUI shipmentUI = new ShipmentUI();

    String[] optionsCombo = {"Waiting", "In Rent", "Ended", "Cancelled"};
    
    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
    
    static int orderIdTemp = 0;
    
    /**
     * Creates new form ShipmentUI
     */
    public ShipmentUI() {
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);

        shipmentTable.setRowHeight(25);
        TableColumnModel columnModel = shipmentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // Username
        columnModel.getColumn(1).setPreferredWidth(1);  // Order ID
        columnModel.getColumn(2).setPreferredWidth(30); // Start Date
        columnModel.getColumn(3).setPreferredWidth(30); // End Date
        columnModel.getColumn(4).setPreferredWidth(40); // Shipment Status
        columnModel.getColumn(5).setPreferredWidth(100); // Last Update

        // Cells center render
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        shipmentTable.setDefaultRenderer(String.class, centerRenderer);

        // Header center render
        JTableHeader header = shipmentTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(shipmentTable));
        
        // Add ComboBox to table
        if (LoginUI.privileges) {
            shipmentTable.setDefaultRenderer(JComboBox.class, new StatusCellRenderer());
            shipmentTable.setDefaultEditor(JComboBox.class, new StatusComboBoxEditor());
        }
        
        // dispose by ESCAPE_KEY
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        ActionMap amTable = shipmentTable.getRootPane().getActionMap();
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
            //This will only be seen on standard output.
            System.out.println("ShipmentUI: windowClosing.");
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        shipmentTable.removeEditor();
        if (RentMyStuff.DEBUGwin) {
            //This will only be seen on standard output.
            System.out.println("ShipmentUI: windowClosed.");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ShipmentUI: windowOpened.");
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ShipmentUI: windowIconified.");
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ShipmentUI: windowDeiconified.");
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ShipmentUI: windowActivated.");
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ShipmentUI: windowDeactivated.");
        }
    }
    
    private static class HeaderRenderer implements TableCellRenderer {

        DefaultTableCellRenderer renderer;
        int HEADER_HEIGHT = 18;

        public HeaderRenderer(JTable table) {
            renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            renderer.setPreferredSize(new Dimension(100, HEADER_HEIGHT));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            return renderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
        }
    }
    
     public class StatusCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof String) {
                String status = (String) value;
                setText(status);
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getSelectionForeground());
            }

            return this;
        }

    }
    
    /**
     * Custom class for adding elements in the JComboBox.
     */
    public class StatusComboBoxEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        
        String status;
        int row;
        // Declare a model that is used for adding the elements to the `Combo box`
        private DefaultComboBoxModel model;

        public StatusComboBoxEditor() {
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            orderIdTemp = shipmentTableModel.getRowCount() - row;
            if (value instanceof String) {
                this.status = (String) value;
                this.row = orderIdTemp;
            }

            JComboBox statusComboBox = new JComboBox(optionsCombo);

            statusComboBox.setSelectedItem(status);
            statusComboBox.addActionListener(this);

            return statusComboBox;
        }

        @Override
        public Object getCellEditorValue() {
            return this.status;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox statusComboBox = (JComboBox) e.getSource();
            this.status = (String) statusComboBox.getSelectedItem();
            statusUpdate(status, row);
            System.out.println("Shipment Status Updated");
        }
    }
    
    static void statusUpdate(String status, int orderId) {
        String updateStatusSQL = "UPDATE Orders SET shipment_status=? WHERE id_order=?";
        
        try {
            Connection con = RentMyStuff.startConnection();
            
            PreparedStatement stmtUpdStatus = con.prepareStatement(updateStatusSQL);
            stmtUpdStatus.setString(1, status);
            stmtUpdStatus.setInt(2, orderId);
            stmtUpdStatus.executeUpdate();
            
            RentMyStuff.closeStatement(stmtUpdStatus);
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't update status in Invoice");
        }
        
    }
    
    static void cancelInvoice(int orderId) {
        String cancelStatusSQL = "UPDATE Invoices SET invoice_status='Cancelled' WHERE id_order=?";
        
        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtCnclStatus = con.prepareStatement(cancelStatusSQL);
            stmtCnclStatus.setInt(1, orderId);
            stmtCnclStatus.executeUpdate();

            RentMyStuff.closeStatement(stmtCnclStatus);
            RentMyStuff.stopConnection(con);
            ApplicationUI.setOrderLastUpdate(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't set 'Cancelled' in the shipmanet_status Invoice");
        }
    }
    
    public static class ShipmentTableModel extends DefaultTableModel implements TableModelListener {

        // add tableListener in this & Column Identifiers
        public ShipmentTableModel() {
            super(new String[]{"Username", "Order ID", "Start Date", "End Date", "Shipment Status", "Last Update"}, 0);
            addTableModelListener(this);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Class clazz = String.class;
            switch (columnIndex) {
                case 4:
                    clazz = JComboBox.class;
                    break;
            }
            return clazz;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            switch (column) {
                case 4:
                    return true;
                default:
                    return false;
            }
        }

        // 0= "Username", 1= "Order ID", 2= "Start Date", 3= "End Date", 4= "Shipment Status", 5= "Last Update"
        @Override
        public void setValueAt(Object aValue, int row, int col) {
            orderIdTemp = shipmentTableModel.getRowCount() - row;
            System.out.println("Total Rows: " + shipmentTableModel.getColumnCount());
            System.out.println("orderIdTemp: " + orderIdTemp);
            if (col == 4) {                                                  // Shipment Status
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(4, (String) aValue);
                ApplicationUI.setOrderLastUpdate(orderIdTemp);
                shipmentTableView();
                fireTableCellUpdated(row, col);
                fireTableCellUpdated(row, 5);

            }
        }

        // Listener 
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int col = e.getColumn();
            TableModel tableModel = (TableModel) e.getSource();
            if (col == 4) {
                Object jComboData = tableModel.getValueAt(row, 4);
                if (jComboData == "Cancelled") {
                        cancelInvoice(orderIdTemp);
                    }
            }
        }
    }
    
    public static void shipmentTableView() {
        shipmentTableModel.setRowCount(0);
        try {
            Connection con = RentMyStuff.startConnection();

            String selectShipmentsSQL = "SELECT username, id_order, start_rent_date, end_rent_date, shipment_status, Orders.last_update FROM Orders "
                    + "INNER JOIN Customers ON Orders.id_tocustomer = Customers.id_user\n"
                    + "INNER JOIN Users ON Customers.id_user = Users.id_user\n";
            if (!LoginUI.privileges) {
                selectShipmentsSQL = selectShipmentsSQL + "WHERE Users.id_user=? ORDER BY id_order DESC";
            } else {
                selectShipmentsSQL += "ORDER BY id_order DESC";
            }

            PreparedStatement stmtShipments = con.prepareStatement(selectShipmentsSQL);

            if (!LoginUI.privileges) {
                stmtShipments.setInt(1, RentMyStuff.customer.getId());
            }
            
            ResultSet rsShipments = stmtShipments.executeQuery();

            while (rsShipments.next()) {
                // Get ResultSet of Products of the Order
                String username = rsShipments.getString("username");
                int orderId = rsShipments.getInt("id_order");
                LocalDate startDate = rsShipments.getDate("start_rent_date").toLocalDate();
                String startDateFormat = String.valueOf(startDate.format(dateFormat));
                LocalDate endDate = rsShipments.getDate("end_rent_date").toLocalDate();
                String endDateFormat = String.valueOf(endDate.format(dateFormat));
                String status = rsShipments.getString("shipment_status");
                Timestamp lastUpdate = rsShipments.getTimestamp("last_update");
                String lastUpdateFormat = String.valueOf(lastUpdate.toLocalDateTime().format(dateTimeFormat));
                // "Username", "Order ID", "Status", "Issue Date", "Payment Date", "Amount"
                Object[] invoiceRow = {username, String.format("%06d", orderId), startDateFormat, endDateFormat, status, lastUpdateFormat};
                if (status.equals("Not Finished")) {
                    
                } else {
                    shipmentTableModel.addRow(invoiceRow);
                }

            }
            RentMyStuff.closeResultSet(rsShipments);
            RentMyStuff.closeStatement(stmtShipments);
            RentMyStuff.stopConnection(con);

        } catch (SQLException ex) {
            System.out.println("Problem in SQL Table Represent");
            ex.printStackTrace();
        }

        shipmentUI.setVisible(true);
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
        shipmentTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        shipmentTable.setModel(shipmentTableModel);
        jScrollPane1.setViewportView(shipmentTable);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/truck_70.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addGap(18, 18, 18))
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
            java.util.logging.Logger.getLogger(ShipmentUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ShipmentUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ShipmentUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShipmentUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                shipmentUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable shipmentTable;
    // End of variables declaration//GEN-END:variables
}
