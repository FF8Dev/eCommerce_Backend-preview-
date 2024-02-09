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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class InvoicesUI extends javax.swing.JFrame implements WindowListener {

    static InvoicesTableModel invoicesTableModel = new InvoicesTableModel();
    static InvoicesUI invoicesUI = new InvoicesUI();

    List<String> optionsCombo = new ArrayList<>();

    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    static int orderIdTemp = 0;

    /**
     * Creates new form InvoicesUI
     */
    public InvoicesUI() {
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);

        if (LoginUI.privileges) {
            optionsCombo.add("Issued");
            optionsCombo.add("Paid");
            optionsCombo.add("Cancelled");
        } else {
            optionsCombo.add("Issued");
            optionsCombo.add("Cancelled");
        }
        
        invoicesTable.setRowHeight(25);
        TableColumnModel columnModel = invoicesTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // Username
        columnModel.getColumn(1).setPreferredWidth(5);  // Order ID
        columnModel.getColumn(2).setPreferredWidth(30); // Status
        columnModel.getColumn(3).setPreferredWidth(20); // Issue Date
        columnModel.getColumn(4).setPreferredWidth(20); // Payment Date
        columnModel.getColumn(5).setPreferredWidth(25); // Amount

        // Cells center render
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        invoicesTable.setDefaultRenderer(String.class, centerRenderer);

        // Header center render
        JTableHeader header = invoicesTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(invoicesTable));

        // Add ComboBox to table
        invoicesTable.setDefaultRenderer(JComboBox.class, new StatusCellRenderer());
        invoicesTable.setDefaultEditor(JComboBox.class, new StatusComboBoxEditor());

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
            //This will only be seen on standard output.
            System.out.println("InvoicesUI: windowClosing.");
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        invoicesTable.removeEditor();
        if (RentMyStuff.DEBUGwin) {
            //This will only be seen on standard output.
            System.out.println("InvoicesUI: windowClosed.");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("InvoicesUI: windowOpened.");
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("InvoicesUI: windowIconified.");
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("InvoicesUI: windowDeiconified.");
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("InvoicesUI: windowActivated.");
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("InvoicesUI: windowDeactivated.");
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

    static void statusUpdate(String status, int orderId) {
        String updateStatusSQL = "UPDATE [rentmystuff].Invoices SET invoice_status=? WHERE id_order=?";

        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtUpdStatus = con.prepareStatement(updateStatusSQL);
            stmtUpdStatus.setString(1, status);
            stmtUpdStatus.setInt(2, orderId);
            stmtUpdStatus.executeUpdate();

            RentMyStuff.closeStatement(stmtUpdStatus);
            RentMyStuff.stopConnection(con);
            ApplicationUI.setOrderLastUpdate(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't update status in Invoice");
        }

    }

    static void updatePaymentDate(String date, int orderId) {
        LocalDate dateSQL = LocalDate.parse(date, dateFormat);
        String updateStatusSQL = "UPDATE [rentmystuff].Invoices SET payment_date=? WHERE id_order=?";

        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtUpdStatus = con.prepareStatement(updateStatusSQL);
            stmtUpdStatus.setDate(1, java.sql.Date.valueOf(dateSQL));
            stmtUpdStatus.setInt(2, orderId);
            stmtUpdStatus.executeUpdate();

            RentMyStuff.closeStatement(stmtUpdStatus);
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't update payment_date in Invoice");
        }
    }

    static void deletePaymentDate(int orderId) {
        String updateStatusSQL = "UPDATE [rentmystuff].Invoices SET payment_date=null WHERE id_order=?";

        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtNullStatus = con.prepareStatement(updateStatusSQL);
            stmtNullStatus.setInt(1, orderId);
            stmtNullStatus.executeUpdate();

            RentMyStuff.closeStatement(stmtNullStatus);
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't set null in the payment_date Invoice");
        }
    }

    static void cancelShipment(int orderId) {
        String cancelStatusSQL = "UPDATE [rentmystuff].Orders SET shipment_status='Cancelled' WHERE id_order=?";

        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtCnclStatus = con.prepareStatement(cancelStatusSQL);
            stmtCnclStatus.setInt(1, orderId);
            stmtCnclStatus.executeUpdate();

            RentMyStuff.closeStatement(stmtCnclStatus);
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't set 'Cancelled' in the shipmanet_status Invoice");
        }
    }

    static void cancelOrder(int orderId) {
        String xmlFile = String.format("%06d", orderId) + ".xml";
        File xmlManipulSrc = new File(OrderUI.xmlFolder + "/" + xmlFile);
        File xmlManipulTrg = new File(OrderUI.xmlCancelledFolder + "/" + xmlFile);
        
        System.out.println("XML Manipul SRC: " + xmlManipulSrc);

        if (Files.exists(xmlManipulSrc.toPath())) {
            System.out.println("Source moving: " + xmlManipulSrc.toPath());
            try {
                Files.move(xmlManipulSrc.toPath(), xmlManipulTrg.toPath(), REPLACE_EXISTING);
                System.out.println("Target: " + xmlManipulTrg.toPath() + "\n");
                System.out.println("Cancelled Order\n");
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Cannot move Cancelled XML");
            }
            ApplicationUI.rentReserved.clear();
        } else {
            System.out.println("File XML not cancelled\n");
        }
    }

    static void readmitOrder(int orderId) {
        String xmlFile = String.format("%06d", orderId) + ".xml";
        File xmlManipulSrc = new File(OrderUI.xmlCancelledFolder + "/" + xmlFile);
        File xmlManipulTrg = new File(OrderUI.xmlFolder + "/" + xmlFile);

        if (Files.exists(xmlManipulSrc.toPath())) {
            System.out.println("Moving: " + xmlManipulSrc.toPath());
            try {
                Files.move(xmlManipulSrc.toPath(), xmlManipulTrg.toPath(), REPLACE_EXISTING);
                System.out.println("Target: " + xmlManipulTrg.toPath() + "\n");
                System.out.println("Readmited Order\n");
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Cannot readmit XML");
            }
            ApplicationUI.rentReserved.clear();
        } else {
            System.out.println("File XML not readmited\n");
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
            orderIdTemp = invoicesTableModel.getRowCount() - row;
            if (value instanceof String) {
                this.status = (String) value;
                this.row = orderIdTemp;
            }

            JComboBox statusComboBox = new JComboBox(optionsCombo.toArray());

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
            System.out.println("Invoice Status Updated");
        }
    }

    public static class InvoicesTableModel extends DefaultTableModel implements TableModelListener {

        // add tableListener in this & Column Identifiers
        public InvoicesTableModel() {
            super(new String[]{"Username", "Order ID", "Status", "Issue Date", "Payment Date", "Amount"}, 0);
            addTableModelListener(this);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Class clazz = String.class;
            switch (columnIndex) {
                case 2:
                    clazz = JComboBox.class;
                    break;
            }
            return clazz;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            switch (column) {
                case 2:
                    return true;
                case 4:
                    return true;
                default:
                    return false;
            }
        }

        // 0= "Username", 1= "Order ID", 2= "Status", 3= "Issue Date", 4= "Payment Date", 5= "Amount"
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            orderIdTemp = Integer.parseInt((String)invoicesTableModel.getValueAt(row, 1));
            if (column == 2) {                                                  // Status
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(2, (String) aValue);
                ApplicationUI.setOrderLastUpdate(orderIdTemp);
                fireTableCellUpdated(row, column);

            } else if (4 == column) {                                           // Payment Date
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(4, (String) aValue);
                if (aValue == null) {
                    deletePaymentDate(orderIdTemp);
                } else {
                    updatePaymentDate(String.valueOf(aValue), orderIdTemp);
                }
                ApplicationUI.setOrderLastUpdate(orderIdTemp);
                fireTableCellUpdated(row, column);
            }
        }

        // Listener 
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int col = e.getColumn();
            TableModel tableModel = (TableModel) e.getSource();
            if (col == 2) {
                // jComboData = JComboBox data
                Object jComboData = tableModel.getValueAt(row, 2);
                if (jComboData == "Paid") {
                    tableModel.setValueAt(LocalDate.now().format(dateFormat), row, 4);
                    readmitOrder(orderIdTemp);
                } else if (jComboData == "Cancelled") {
                    tableModel.setValueAt(null, row, 4);
                    cancelShipment(orderIdTemp);
                    cancelOrder(orderIdTemp);
                } else if (jComboData == "Issued") {
                    tableModel.setValueAt(null, row, 4);
                    readmitOrder(orderIdTemp);
                }
                ApplicationUI.readXML();
                ApplicationUI.productsTable.repaint();
            }
        }
    }

    public static void invoiceTableView() {
        invoicesTableModel.setRowCount(0);
        try {
            Connection con = RentMyStuff.startConnection();

            String selectInvoicesSQL = "SELECT username, Orders.id_order, invoice_status, issue_date, payment_date, amount FROM [rentmystuff].Invoices "
                    + "INNER JOIN [rentmystuff].Orders ON Invoices.id_order = Orders.id_order\n"
                    + "INNER JOIN [rentmystuff].Customers ON Orders.id_tocustomer = Customers.id_user\n"
                    + "INNER JOIN [rentmystuff].Users ON Customers.id_user = Users.id_user\n";
            if (!LoginUI.privileges) {
                selectInvoicesSQL += "WHERE Users.id_user=? ORDER BY id_order DESC";
            } else {
                selectInvoicesSQL += "ORDER BY id_order DESC";
            }

            PreparedStatement stmtInvoices = con.prepareStatement(selectInvoicesSQL);
            if (!LoginUI.privileges) {
                stmtInvoices.setInt(1, RentMyStuff.customer.getId());
            }

            ResultSet rsInvoices = stmtInvoices.executeQuery();

            while (rsInvoices.next()) {
                // Get ResultSet of Products of the Order
                String username = rsInvoices.getString("username");
                int orderId = rsInvoices.getInt("id_order");
                String status = rsInvoices.getString("invoice_status");
                LocalDate issueDate = rsInvoices.getDate("issue_date").toLocalDate();
                String issueDateFormat = String.valueOf(issueDate.format(dateFormat));
                Date paymentDate = rsInvoices.getDate("payment_date");
                String paymentDateFormat = "";
                if (rsInvoices.wasNull()) {
                    paymentDateFormat = "";
                } else {
                    LocalDate paymentDateTrue = Instant.ofEpochMilli(paymentDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    paymentDateFormat = String.valueOf(paymentDateTrue.format(dateFormat));
                }
                double amount = rsInvoices.getDouble("amount");
                String amountFormat = String.format("%.2f", amount) + " €";
                // Username", "Order ID", "Status", "Issue Date", "Payment Date", "Amount
                Object[] invoiceRow = {username, String.format("%06d", orderId), status, issueDateFormat, paymentDateFormat, amountFormat};
                invoicesTableModel.addRow(invoiceRow);

            }
            RentMyStuff.closeResultSet(rsInvoices);
            RentMyStuff.closeStatement(stmtInvoices);
            RentMyStuff.stopConnection(con);

        } catch (SQLException ex) {
            System.out.println("Problem in SQL Table Represent");
            ex.printStackTrace();
        }

        invoicesUI.setVisible(true);
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
        invoicesTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        invoicesTable.setModel(invoicesTableModel);
        jScrollPane1.setViewportView(invoicesTable);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/invoice_70.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            java.util.logging.Logger.getLogger(InvoicesUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InvoicesUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InvoicesUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InvoicesUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                invoicesUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable invoicesTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
