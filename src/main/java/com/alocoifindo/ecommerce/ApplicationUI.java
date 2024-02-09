/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author facundoferreyra
 */
public class ApplicationUI extends javax.swing.JFrame implements WindowListener {

    static int idOrder;

    static List<Customer> customers = new ArrayList<Customer>();
    static String tempUsername = "default_customer";

    static boolean updatedDiscount;
    static int counter = 0;
    static Map<Integer, Double> pricePerDayMap = new HashMap<Integer, Double>();
    static Map<Integer, Integer> discountPerDayMap = new HashMap<Integer, Integer>();
    static Map<Integer, Boolean> selectedProduct = new HashMap<Integer, Boolean>();

    static HashSet<ReservedProduct> rentReserved;
    static Map<Integer, ReservedProduct> rentOrder = new HashMap<Integer, ReservedProduct>();

    static DefaultComboBoxModel customersComboBoxModel = new DefaultComboBoxModel();
    static customerListener comboListener = new customerListener();
    static ProductsChecklistTableModel productsTableModel = new ProductsChecklistTableModel();
    static GrayableCellRenderer grayableRenderer = new GrayableCellRenderer();
    static GrayableCheckboxCellRenderer grayableCheckboxRenderer = new GrayableCheckboxCellRenderer();
//    GrayableImageIconCellRenderer grayableImageIconRenderer = new GrayableImageIconCellRenderer();
    static DefaultListModel listModel = new DefaultListModel();

    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 0, 90, 1);

    static ApplicationUI appUI = new ApplicationUI();

    static DatePickerSettings dateSettingsStart;
    static DatePickerSettings dateSettingsEnd;

    static DatePicker datePickerStart;
    static DatePicker datePickerEnd;

    DateListenerStart dateListenerStart = new DateListenerStart();
    DateListenerEnd dateListenerEnd = new DateListenerEnd();
    JButton datePickerButtonStart;
    JButton datePickerButtonEnd;
    ImageIcon calendarIcon = new ImageIcon(getClass().getResource("/calendar-20.png"));

    /**
     * Creates new form ApplicationUI
     */
    public ApplicationUI() {
//        rangeDatePickerMap = new HashMap<Integer, RangeDatePicker>();
//        rangeDatePickerSettingsMap = new HashMap<>();
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);

        if (!LoginUI.privileges) {
            jSeparator1.setVisible(false);
            productAddButton.setVisible(false);
        }

        int customerId = RentMyStuff.customer.getId();
        if (RentMyStuff.DEBUG) {
            System.out.println("Customer ID: " + customerId);
        }
        RentMyStuff.order.setCreationDate(LocalDate.now());
        RentMyStuff.order.setStartDate(LocalDate.now());
        RentMyStuff.order.setEndDate(LocalDate.now());
        RentMyStuff.order.setAmount(0.0);
        setOrderId();

        // customerID starts at 2 in admin session
        if (customerId == 2) {
            customerLabel.setVisible(false);
            listCustomers();
        } else {
            //switch comboBox to jLabel
            customerSelect.setVisible(false);
            customerSelect.getItemAt(customerId);

            // !!! retrieve discountField if discountSQL == 0
            if (RentMyStuff.customer.getDiscount() == 0) {
                discountLabel.setVisible(false);
                discountField.setVisible(false);
            }
        }

        totalPriceField.setText("0.00");

        productsTableView();
        setCustomerDataUI();
        productsTable.setDefaultRenderer(String.class, grayableRenderer);
        productsTable.setDefaultRenderer(Boolean.class, grayableCheckboxRenderer);    // desplace JCheckBox when clicked
//        productsTable.setDefaultRenderer(JCheckBox.class, grayableCheckboxRenderer);  // doesn't accept paitings
//        productsTable.setDefaultEditor(Boolean.class, new GrayableCellEditor());
//        productsTable.setDefaultRenderer(ImageIcon.class, grayableImageIconRenderer);

        daysSpinner.setVisible(false);
//        daysSpinner.addChangeListener(new daysListener());

        dateSettingsStart = new DatePickerSettings();
        dateSettingsEnd = new DatePickerSettings();

        datePickerStart = new DatePicker(dateSettingsStart);
        datePickerEnd = new DatePicker(dateSettingsEnd);

        dateSettingsStart.setFormatForDatesCommonEra("d MMM yyyy");
        dateSettingsStart.setFormatForDatesBeforeCommonEra("d MMM uuuu");
        dateSettingsEnd.setFormatForDatesCommonEra("d MMM yyyy");
        dateSettingsEnd.setFormatForDatesBeforeCommonEra("d MMM uuuu");

//        dateSettingsStart.setVisibleDateTextField(false);
//        dateSettingsStart.setGapBeforeButtonPixels(0);
        DateVetoPolicy vetoPolicyStart = new DateVetoPolicyStart();
        DateVetoPolicy vetoPolicyEnd = new DateVetoPolicyEnd();

        dateSettingsStart.setVetoPolicy(vetoPolicyStart);
        dateSettingsEnd.setVetoPolicy(vetoPolicyEnd);

        datePickerStart.setDateToToday();
        datePickerEnd.setDateToToday();

        datePickerButtonStart = datePickerStart.getComponentToggleCalendarButton();
        datePickerButtonEnd = datePickerEnd.getComponentToggleCalendarButton();

        datePickerButtonStart.setText("");
        datePickerButtonStart.setIcon(calendarIcon);
        datePickerButtonEnd.setText("");
        datePickerButtonEnd.setIcon(calendarIcon);

        datePanel.add(datePickerStart);
        datePanel.add(datePickerEnd);

        datePickerStart.addDateChangeListener(dateListenerStart);
        datePickerEnd.addDateChangeListener(dateListenerEnd);

        rentReserved = new HashSet<ReservedProduct>();

        readXML();
        if (RentMyStuff.DEBUG) {
            System.out.println("\nRentMyStuff Folder: " + OrderUI.rmsFolder + "\n");
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("WindowListener method called: windowClosing.");
        }
        String tempStatus;

        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtSelTempOrd = con.prepareStatement("SELECT shipment_status FROM [rentmystuff].Orders WHERE id_order = ?");
            stmtSelTempOrd.setInt(1, idOrder);
            ResultSet rsTempOrd = stmtSelTempOrd.executeQuery();
            rsTempOrd.next();
            tempStatus = rsTempOrd.getString("shipment_status");
            if (tempStatus.equals("Not Finished")) {
                PreparedStatement stmtDelTempOrdLn = con.prepareStatement("DELETE FROM [rentmystuff].order_line WHERE id_order = ?");
                stmtDelTempOrdLn.setInt(1, idOrder);
                stmtDelTempOrdLn.executeUpdate();

                PreparedStatement stmtDelTempOrd = con.prepareStatement("DELETE FROM [rentmystuff].Orders WHERE id_order = ?");
                stmtDelTempOrd.setInt(1, idOrder);
                stmtDelTempOrd.executeUpdate();

                if (RentMyStuff.DEBUG) {
                    System.out.println("DELETED actual Temporal Order");
                }
                RentMyStuff.closeStatement(stmtDelTempOrdLn);
                RentMyStuff.closeStatement(stmtDelTempOrd);
            }

            RentMyStuff.closeResultSet(rsTempOrd);
            RentMyStuff.closeStatement(stmtSelTempOrd);
            RentMyStuff.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("Cannot DELETE Temporary Order");
            ex.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            //This will only be seen on standard output.
            System.out.println("ApplicationUI: windowClosed.");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ApplicationUI: windowOpened.");
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ApplicationUI: windowIconified.");
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ApplicationUI: windowDeiconified.");
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ApplicationUI: windowActivated.");
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (RentMyStuff.DEBUGwin) {
            System.out.println("ApplicationUI: windowDeactivated.");
        }
    }

    public static void setDiscountInTable() {
        //update product prices on table by customer
        for (int i = 0; i < productsTable.getRowCount(); i++) {
            double pricePerProduct = pricePerDayMap.get(i);
            double priceWDiscount = pricePerProduct * ((100.0 - RentMyStuff.customer.getDiscount()) / 100);

            String priceWDiscountSymbol = (String.format("%.2f", priceWDiscount)) + " €";
            if (RentMyStuff.DEBUG) {
                System.out.println("PriceWDiscountSymbol(" + i + "): " + priceWDiscountSymbol);
            }
            // to not update order_line if discountSQL modified
            updatedDiscount = true;
            // check 5 corresponds with priceWDiscountSymbol or 6 --> after add idProductNamed
            productsTableModel.setValueAt(priceWDiscountSymbol, i, 6);
        }
        productsTable.repaint();
    }

    public static void setOrderLastUpdate(int idOrder) {
        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtUpdOrd = con.prepareStatement("UPDATE [rentmystuff].Orders SET last_update=GETDATE() WHERE id_order=?");
            stmtUpdOrd.setInt(1, idOrder);
            stmtUpdOrd.executeUpdate();

            RentMyStuff.closeStatement(stmtUpdOrd);
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't update NOW() in last_update Order");
        }
    }

    public static void setOrderId() {
        try {
            Connection con = RentMyStuff.startConnection();

            PreparedStatement stmtRstOrderLine = con.prepareStatement("DELETE FROM [rentmystuff].order_line");
            PreparedStatement stmtRstIncrem = con.prepareStatement(
                            "ALTER TABLE [rentmystuff].order_line DROP COLUMN id_order_line; "
                            + "ALTER TABLE [rentmystuff].order_line ADD id_order_line INT NOT NULL IDENTITY(1,1)"
            );
            PreparedStatement stmtRstOrderNotFinished = con.prepareStatement("DELETE FROM [rentmystuff].orders WHERE shipment_status = 'Not Finished'");

            stmtRstOrderLine.executeUpdate();
            stmtRstIncrem.execute();
            stmtRstOrderNotFinished.executeUpdate();

            PreparedStatement stmtOrderId = con.prepareStatement("SELECT MAX(id_order) AS id_order FROM [rentmystuff].Orders");
            ResultSet rsOrdId = stmtOrderId.executeQuery();
            rsOrdId.next();
            idOrder = rsOrdId.getInt("id_order") + 1;
            RentMyStuff.order.setId(idOrder);
            if (RentMyStuff.DEBUG) {
                System.out.println("next order_id: " + idOrder);
            }

            PreparedStatement stmtOrdCreate = con.prepareStatement("INSERT INTO [rentmystuff].orders VALUES (?, GETDATE(), 1, ?, ?, ?, 'Not Finished', GETDATE(), ?, ?)");
            stmtOrdCreate.setInt(1, idOrder);
            int userOrder;
            // if user cames from SignUp
            if (LoginUI.idUser == 0) {
                userOrder = RentMyStuff.customer.getId();
            } else {
                userOrder = LoginUI.idUser;
            }
            stmtOrdCreate.setDate(2, Date.valueOf(RentMyStuff.order.getStartDate()));
            stmtOrdCreate.setDate(3, Date.valueOf(RentMyStuff.order.getEndDate()));
            stmtOrdCreate.setDouble(4, RentMyStuff.order.getAmount());
            stmtOrdCreate.setInt(5, userOrder);
            stmtOrdCreate.setInt(6, RentMyStuff.customer.getId());
            if (RentMyStuff.DEBUG) {
                System.out.println("idUser (0 if cames from signUp): " + LoginUI.idUser);
                System.out.println("idUser by ApplicationMain#customer: " + RentMyStuff.customer.getId());
            }
            stmtOrdCreate.executeUpdate();

            RentMyStuff.closeResultSet(rsOrdId);
            RentMyStuff.closeStatement(stmtOrderId);
            RentMyStuff.closeStatement(stmtRstOrderLine);
            RentMyStuff.closeStatement(stmtRstIncrem);
            RentMyStuff.closeStatement(stmtRstOrderNotFinished);
            RentMyStuff.closeStatement(stmtOrdCreate);
            RentMyStuff.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("Cannot DELETE FROM [rentmystuff].order_line TABLE OR SELECT MAX(id_order) OR INSERT NEW order");
            ex.printStackTrace();
        }
    }

    public static void listCustomers() {
        customerSelect.removeAllItems();
        customerSelect.removeItemListener(comboListener);
        customersComboBoxModel.removeAllElements();
        customers.clear();

        try {
            Connection con = RentMyStuff.startConnection();
            String selectCustomersSQL = "SELECT * FROM [rentmystuff].Users JOIN [rentmystuff].Customers ON [rentmystuff].users.id_user = [rentmystuff].customers.id_user ORDER BY [rentmystuff].users.id_user ASC";
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

            RentMyStuff.closeResultSet(rsCustomers);
            RentMyStuff.closeStatement(stmtCustomers);
            RentMyStuff.stopConnection(con);
        } catch (SQLException ex) {
            System.out.println("SQL Error in ComboBox");
            ex.printStackTrace();
        }
    }

    public static void setCustomerDataUI() {
        customerLabel.setText(RentMyStuff.customer.getUsername());
        discountField.setText(String.valueOf(RentMyStuff.customer.getDiscount()));
        if (LoginUI.privileges) {
            customerSelect.setSelectedItem(RentMyStuff.customer.getUsername());
            if (RentMyStuff.DEBUG) {
                System.out.println("Selected Customer Item: " + RentMyStuff.customer.getUsername());
            }
        }
        customerSelect.addItemListener(comboListener);
    }

    public static class customerListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                // add to tempUsername username of list & to RentMyStuff.customer
                tempUsername = item.toString();
                RentMyStuff.customer.setUsername(tempUsername);
                if (RentMyStuff.DEBUG) {
                    System.out.println("tempUsername: " + tempUsername);
                }
                OrderUI.customerLabel.setText(tempUsername);

                try {
                    Connection con = RentMyStuff.startConnection();
                    String selectDiscountsSQL = "SELECT customers.id_user, username, discount, firstname, lastname, address_line, city, postalcode, telephone, email"
                            + " FROM [rentmystuff].Users JOIN [rentmystuff].Customers ON users.id_user = customers.id_user AND username=? ORDER BY [rentmystuff].users.id_user ASC;";
                    PreparedStatement stmtDiscounts = con.prepareStatement(selectDiscountsSQL);
                    stmtDiscounts.setString(1, tempUsername);
                    ResultSet rsCustSelected = stmtDiscounts.executeQuery();
                    rsCustSelected.next();

                    RentMyStuff.customer.setId(rsCustSelected.getInt("id_user"));
                    RentMyStuff.customer.setDiscount(rsCustSelected.getInt("discount"));
                    RentMyStuff.customer.setFirstname(rsCustSelected.getString("firstname"));
                    RentMyStuff.customer.setLastname(rsCustSelected.getString("lastname"));
                    RentMyStuff.customer.setAddressLine(rsCustSelected.getString("address_line"));
                    RentMyStuff.customer.setCity(rsCustSelected.getString("city"));
                    RentMyStuff.customer.setPostalcode(rsCustSelected.getInt("postalcode"));
                    RentMyStuff.customer.setTelephone(rsCustSelected.getInt("telephone"));
                    RentMyStuff.customer.setEmail(rsCustSelected.getString("email"));

                    if (RentMyStuff.DEBUG) {
                        System.out.println("TempId customer selected: " + RentMyStuff.customer.getId());
                        System.out.println("Discount customer selected: " + RentMyStuff.customer.getDiscount());
                    }

                    String updateOrderSQL = "UPDATE [rentmystuff].Orders SET id_tocustomer=? WHERE id_order=?";
                    PreparedStatement stmtUpdOrd = con.prepareStatement(updateOrderSQL);
                    stmtUpdOrd.setInt(1, RentMyStuff.customer.getId());
                    stmtUpdOrd.setInt(2, idOrder);
                    stmtUpdOrd.executeUpdate();

                    // set discountSQL in fieldUI
                    discountField.setText(String.valueOf(RentMyStuff.customer.getDiscount()));

                    //update product prices on table by customer
                    setDiscountInTable();

                    RentMyStuff.closeResultSet(rsCustSelected);
                    RentMyStuff.closeStatement(stmtUpdOrd);
                    RentMyStuff.closeStatement(stmtDiscounts);
                    RentMyStuff.stopConnection(con);

                } catch (SQLException ex) {
                    System.out.println("Error while setting Discount percentage OR Update id_toCustomer");
                    ex.printStackTrace();
                }
                setOrderLastUpdate(RentMyStuff.order.getId());
                updateTotalPrice(RentMyStuff.totalDays);
            }
        }
    }

    /**
     * SampleDateVetoPolicy, A veto policy is a way to disallow certain dates
     * from being selected in calendar. A vetoed date cannot be selected by
     * using the keyboard or the mouse.
     */
    private static class DateVetoPolicyStart implements DateVetoPolicy {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(6);

        /**
         * isDateAllowed, Return true if a date should be allowed, or false if a
         * date should be vetoed.
         */
        @Override
        public boolean isDateAllowed(LocalDate date) {
            // Disallow days beforeToday and after4Months
            if ((date.isBefore(today)) || (date.isAfter(endDate))) {
                return false;
            }
            // Allow all other days.
            return true;
        }
    }

    private static class DateVetoPolicyEnd implements DateVetoPolicy {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(6);

        /**
         * isDateAllowed, Return true if a date should be allowed, or false if a
         * date should be vetoed.
         */
        @Override
        public boolean isDateAllowed(LocalDate date) {
            // Disallow days beforeToday and after4Months
            if ((date.isBefore(today)) || (date.isAfter(endDate))) {
                return false;
            } else if (date.isBefore(RentMyStuff.order.getStartDate())) {
                return false;
            }
            // Allow all other days.
            return true;
        }
    }

    public static class DateListenerStart implements DateChangeListener {

        @Override
        public void dateChanged(DateChangeEvent dce) {
            LocalDate newDate = dce.getNewDate();
            RentMyStuff.order.setStartDate(newDate);
            int adjustedDays = (Math.abs(RentMyStuff.order.getEndDate().compareTo(newDate))) + 1;

            if (RentMyStuff.order.getEndDate().compareTo(newDate) == 0) {
                daysField.setText("1");
            } else if (RentMyStuff.order.getEndDate().compareTo(newDate) <= 0) {
                daysField.setText("0");
                adjustedDays = 0;

            } else {
                daysField.setText(String.valueOf(adjustedDays));
            }
            RentMyStuff.totalDays = adjustedDays;
            updateTotalPrice(adjustedDays);
            productsTable.repaint();

            Connection con;
            try {
                con = RentMyStuff.startConnection();
                String updateDateStartSQL = "UPDATE [rentmystuff].Orders SET start_rent_date=?, total_days=? WHERE id_order=?";

                PreparedStatement stmtDateStart = con.prepareStatement(updateDateStartSQL);
                stmtDateStart.setDate(1, Date.valueOf(dce.getNewDate()));
                stmtDateStart.setInt(2, adjustedDays);
                stmtDateStart.setInt(3, RentMyStuff.order.getId());
                stmtDateStart.executeUpdate();

                RentMyStuff.closeStatement(stmtDateStart);
                RentMyStuff.stopConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error while updating Date in Orders");
            }
            setOrderLastUpdate(RentMyStuff.order.getId());
        }

    }

    public static class DateListenerEnd implements DateChangeListener {

        @Override
        public void dateChanged(DateChangeEvent dce) {
            LocalDate newDate = dce.getNewDate();
            RentMyStuff.order.setEndDate(newDate);
            int adjustedDays = (Math.abs(RentMyStuff.order.getStartDate().compareTo(newDate)) + 1);

            if (Math.abs(RentMyStuff.order.getStartDate().compareTo(newDate)) == 0) {
                daysField.setText("1");
            } else if (Math.abs(RentMyStuff.order.getStartDate().compareTo(newDate)) <= 0) {
                daysField.setText("0");
                adjustedDays = 0;
            } else {
                daysField.setText(String.valueOf(adjustedDays));
            }
            RentMyStuff.totalDays = adjustedDays;
            updateTotalPrice(adjustedDays);
            productsTable.repaint();

            Connection con;
            try {
                con = RentMyStuff.startConnection();
                String updateDateEndSQL = "UPDATE [rentmystuff].Orders SET end_rent_date=?, total_days=? WHERE id_order=?";

                PreparedStatement stmtDateEnd = con.prepareStatement(updateDateEndSQL);
                stmtDateEnd.setDate(1, Date.valueOf(dce.getNewDate()));
                stmtDateEnd.setInt(2, adjustedDays);
                stmtDateEnd.setInt(3, RentMyStuff.order.getId());
                stmtDateEnd.executeUpdate();

                RentMyStuff.closeStatement(stmtDateEnd);
                RentMyStuff.stopConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error while updating Date in Orders");
            }
            setOrderLastUpdate(RentMyStuff.order.getId());
        }

    }

    static public List<LocalDate> rangeDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> rangeDates = startDate.datesUntil(endDate.plusDays(1)).map(x -> x).collect(Collectors.toList());
        for (LocalDate date : rangeDates) {
            if (RentMyStuff.DEBUGwin) {
                System.out.println("Working range: " + date);
            }
        }
        return rangeDates;
    }

    static public boolean compareDates(List<LocalDate> orderDates, List<LocalDate> reservedDates) {
        for (LocalDate date : orderDates) {
            if (RentMyStuff.DEBUGwin) {
                System.out.println("Dates Order: " + date);
            }
            if (reservedDates.contains(date)) {
                if (RentMyStuff.DEBUGwin) {
                    System.out.println("Compare dates: true");
                }
                return true;
            }
        }
        if (RentMyStuff.DEBUGwin) {
            System.out.println("Compare dates: false");
        }
        return false;
    }

    static boolean checkId(String idCell) {
        for (Iterator<ReservedProduct> i = rentReserved.iterator(); i.hasNext();) {
            ReservedProduct next = i.next();
            if (RentMyStuff.DEBUGwin) {
                System.out.println("NextProductId: " + next.getProductId());
            }

            if (next.getProductId().equals(idCell)) {
                if (RentMyStuff.DEBUGwin) {
                    System.out.println("\nEquals cell");
                }
                List<LocalDate> orderDates = rangeDates(RentMyStuff.order.getStartDate(), RentMyStuff.order.getEndDate());
                List<LocalDate> nextDates = rangeDates(next.getStartDate(), next.getEndDate());
                for (LocalDate date : orderDates) {
                    if (RentMyStuff.DEBUGwin) {
                        System.out.println("\nOrder Date: " + date);
                    }
                }
                if (compareDates(orderDates, nextDates)) {
                    return true;
                }
            }
        }
        return false;
    }

//    public class GrayableImageIconCellRenderer extends ImageIcon implements  TableCellRenderer {
//        boolean isEditable(int row) {
//            String prodIdCell = String.valueOf(productsTable.getValueAt(row, 1));
//            
//            if (checkId(prodIdCell)) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//        
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
//            boolean editable = isEditable(row);
//            
//            if (!editable) {
//                setBackground(editable ? UIManager.getColor("Table.background") : UIManager.getColor("Label.disabledShadow"));
//            } else {
//                setBackground(isSelected ? UIManager.getColor("Table.selectionBackground") : UIManager.getColor("Table.background"));
//            }
//            
//            return this;
//        }
//    }
//    
    public static class GrayableCellRenderer extends JTextField implements TableCellRenderer {

        boolean isGray(int row) {
            String prodIdCell = String.valueOf(productsTable.getValueAt(row, 1));

            if (checkId(prodIdCell)) {
                setEnabled(false);
                return false;
            } else {
                setEnabled(true);
                return true;
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            boolean editable = isGray(row);
            if (!editable) {
                setBackground(editable ? UIManager.getColor("Table.background") : UIManager.getColor("controlShadow"));
            } else {
                setBackground(isSelected ? UIManager.getColor("Table.selectionBackground") : UIManager.getColor("Table.background"));
            }
            setText(String.valueOf(value));
            setFont(new Font("Sans_Serif", Font.PLAIN, 12));
            setBorder(null);

//            setSelected((Boolean) value);
//            if (isSelected) {
//                // TODO: cell (and perhaps other cells) are selected, need to highlight it
//            }
            return this;
        }
    }

    public static class GrayableCheckboxCellRenderer extends JCheckBox implements TableCellRenderer {

        boolean select = false;

        boolean isGray(int row) {
            String prodIdCell = String.valueOf(productsTable.getValueAt(row, 1));

            if (checkId(prodIdCell)) {
//            setEnabled(false);
                return false;
            } else {
//            setEnabled(true);
                return true;
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setHorizontalAlignment(SwingConstants.CENTER);
            boolean editable = isGray(row);
            if (value != null && value.equals(true)) {
                select = true;
                setSelected(true);
            } else {
                select = false;
                setSelected(false);
            }
            if (!editable) {
                setBackground(editable ? UIManager.getColor("Table.background") : UIManager.getColor("controlShadow"));
                setEnabled(false);
            } else {
                setBackground(isSelected ? UIManager.getColor("Table.selectionBackground") : UIManager.getColor("Table.background"));
                setEnabled(true);
            }

            return this;
        }
    }

//    private class GrayableCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {
//
//        boolean isGray(int row) {
//            String prodIdCell = String.valueOf(productsTable.getValueAt(row, 1));
//            if (checkId(prodIdCell)) {
////            setEnabled(false);
//                return false;
//            } else {
////            setEnabled(true);
//                return true;
//            }
//        }
//        
//        public GrayableCellEditor() {
//            grayableCheckboxRenderer.addItemListener(this);
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//            return grayableCheckboxRenderer.isSelected();
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable jtable, Object value, boolean isSelected, int row, int col) {
//            boolean editable = isGray(row);
//            if (!editable) {
//                setEnabled(false);
//            } else {
//                setEnabled(true);
//            }
//            return grayableCheckboxRenderer;
//        }
//
//        @Override
//        public void itemStateChanged(ItemEvent e) {
//            this.fireEditingStopped();
//        }
//
//    }
    // Checklist Table
    public static class ProductsChecklistTableModel extends DefaultTableModel implements TableModelListener {

        boolean listedProduct = false;
        String priceWithSymbol;
        double pricePerProduct;
        double priceWDiscount;
        String discountWithSymbol;
        int discountPerProduct;
        double discountComma;

        // add tableListener in this & Column Identifiers
        public ProductsChecklistTableModel() {
            super(new String[]{"Select", "ID", "Product", "Image", "Category", "Keywords", "Price/day", "Discount/day"}, 0);
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
                    clazz = ImageIcon.class;
                    break;

            }
            return clazz;
        }

        boolean isGray(int row) {
            String prodIdCell = String.valueOf(productsTable.getValueAt(row, 1));

            if (checkId(prodIdCell)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {

            return column == 0 && isGray(row);
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (aValue instanceof Boolean && column == 0) {
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(0, (boolean) aValue);
                if (RentMyStuff.DEBUG) {
                    System.out.println("Pressed Button makes: " + aValue.toString());
                }
                // to update order_line if selected
                updatedDiscount = false;
                fireTableCellUpdated(row, column);
                setOrderLastUpdate(RentMyStuff.order.getId());
            }
            if (column == 6) {
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(6, (String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        // Listener of checkbox // (row, 0) = Select checkmark // (row, 2) = Product // (row, 5) = Price // --> change +1 after id_product_named
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            TableModel model = (TableModel) e.getSource();

            if (RentMyStuff.DEBUG) {
                System.out.println("Row ProductTable changed nº" + row);
            }

            // data = checkmark
            Object data = model.getValueAt(row, 0);

            // if comes from checkmark only
            if (!updatedDiscount) {
                // if checkmark true
                if (data.equals(true)) {
                    datePickerStart.getComponentToggleCalendarButton();
                    // product list add
                    listModel.addElement(model.getValueAt(row, 2));
                    itemsField.setText(String.valueOf(listModel.getSize()));

                    // total pricePerProduct add
                    priceWithSymbol = model.getValueAt(row, 6).toString();
                    pricePerProduct = Double.parseDouble(priceWithSymbol.replaceAll("[^0-9.]", ""));

                    // total discountPerProduct add
                    discountWithSymbol = model.getValueAt(row, 7).toString();
                    discountPerProduct = Integer.parseInt(discountWithSymbol.replaceAll("[^0-9]", ""));
                    discountComma = (100.0 - discountPerProduct) / 100.0;

                    // INSERTO into SQL ´order_line´ Temp
                    try {
                        Connection con = RentMyStuff.startConnection();

                        PreparedStatement stmtIns = con.prepareStatement("INSERT INTO [rentmystuff].order_line (id_product, id_order) VALUES (?, ?);");
                        if (RentMyStuff.DEBUG) {
                            System.out.println("id_product insert into order_line: " + RentMyStuff.products.get(row).getId());
                        }

                        stmtIns.setInt(1, RentMyStuff.products.get(row).getId());
                        stmtIns.setInt(2, RentMyStuff.order.getId());
                        stmtIns.executeUpdate();

                        RentMyStuff.closeStatement(stmtIns);
                        RentMyStuff.stopConnection(con);
                    } catch (SQLException ex) {
                        System.out.println("order_line INSERT failed");
                        ex.printStackTrace();
                    }

                    // Final Price set
                    if (RentMyStuff.DEBUG) {
                        System.out.println("setValueAt Row: " + row);
                    }
                    selectedProduct.put(row, true);
                    ReservedProduct addProduct = new ReservedProduct(RentMyStuff.products.get(row).getIdNamed(), RentMyStuff.order.getStartDate(), RentMyStuff.order.getEndDate());
                    rentOrder.put(row, addProduct);
                    updateTotalPrice(RentMyStuff.totalDays);

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
                        priceWithSymbol = model.getValueAt(row, 6).toString();
                        pricePerProduct = Double.parseDouble(priceWithSymbol.replaceAll("[^0-9.]", ""));

                        // total discountPerProduct remove
                        discountWithSymbol = model.getValueAt(row, 7).toString();
                        discountPerProduct = Integer.parseInt(discountWithSymbol.replaceAll("[^0-9]", ""));
                        discountComma = (100.0 - discountPerProduct) / 100.0;

                        // DELETE from SQL ´order_line´ Temp
                        try {
                            Connection con = RentMyStuff.startConnection();

                            PreparedStatement stmtDel = con.prepareStatement("DELETE FROM [rentmystuff].order_line WHERE id_product=?;");
                            if (RentMyStuff.DEBUG) {
                                System.out.println("Product ID deleted from order_line: " + RentMyStuff.products.get(row).getId());
                            }
                            stmtDel.setInt(1, RentMyStuff.products.get(row).getId());
                            stmtDel.executeUpdate();

                            updateTotalPrice(RentMyStuff.totalDays);

                            RentMyStuff.closeStatement(stmtDel);
                            RentMyStuff.stopConnection(con);
                        } catch (SQLException ex) {
                            System.out.println("order_line DELETE failed");
                            ex.printStackTrace();
                        }

                        // Final Price set
                        selectedProduct.put(row, false);
                        rentOrder.remove(row);
                        updateTotalPrice(RentMyStuff.totalDays);

                    }
                }
            }
        }
    }

    private static void productsTableView() {

        try {
            Connection con = RentMyStuff.startConnection();

            String selectProductsSQL = "SELECT id_product, id_product_named, Image, CONCAT(brand, ' ', model_name) AS Product, Category, Keywords, price_per_day, discount_per_days FROM [rentmystuff].Products";
            PreparedStatement stmtProducts = con.prepareStatement(selectProductsSQL);
            ResultSet rsProducts = stmtProducts.executeQuery();

            while (rsProducts.next()) {
                // Preparing Icon and get from ResultSetof Products
                ImageIcon icon = null;
//                InputStream is = rsProducts.getBinaryStream("Image");
//                // Decode the inputstream as BufferedImage
//                try {
//                    BufferedImage bufImg = null;
//                    bufImg = ImageIO.read(is);
//                    Image image = bufImg;
//                    icon = new ImageIcon(image);
//                } catch (IOException ioe) {
//                    System.out.println("Error catching image");
//                    ioe.printStackTrace();
//                }
                BufferedImage img = null;
                try {
                    File imagePath = new File("BD Photos/coded/" + rsProducts.getString("id_product_named") + ".png");
                    img = ImageIO.read(imagePath);
                    Image image = img;
                    icon = new ImageIcon(image);
                } catch (IOException e) {

                }

                // Get rest of ResultSet of Products
                int idProduct = rsProducts.getInt("id_product");
                String idProductNamed = rsProducts.getString("id_product_named");
                String productName = rsProducts.getString("Product");
                String category = rsProducts.getString("Category");
                String keywords = rsProducts.getString("Keywords");
                double pricePerDay = rsProducts.getDouble("price_per_day");
                // format price_per_day
                String pricePerDayDisplay = (String.format("%.2f", pricePerDay)) + " €";
                int discountPerDay = rsProducts.getInt("discount_per_days");
                // format discount_per_days
                String discountPerDayDisplay = discountPerDay + " %";

                pricePerDayMap.put(counter, pricePerDay);
                discountPerDayMap.put(counter, discountPerDay);

                RentMyStuff.products.add(new Product(idProduct, idProductNamed, productName, pricePerDay, discountPerDay));

                Object[] productRow = {false, idProductNamed, productName, icon, category, keywords, pricePerDayDisplay, discountPerDayDisplay};
                productsTableModel.addRow(productRow);
                counter++;
            }
            RentMyStuff.closeResultSet(rsProducts);
            RentMyStuff.closeStatement(stmtProducts);
            RentMyStuff.stopConnection(con);

            // Apply discountSQL prices by customer
            for (int i = 0; i < productsTable.getRowCount(); i++) {
                double pricePerProduct = pricePerDayMap.get(i);
                double priceWDiscount = pricePerProduct * ((100.0 - RentMyStuff.customer.getDiscount()) / 100);

                String priceWDiscountSymbol = (String.format("%.2f", priceWDiscount)) + " €";
                if (RentMyStuff.DEBUG) {
                    System.out.println("PriceWDiscountSymbol(" + i + "): " + priceWDiscountSymbol);
                }
                // to not update order_line if discountSQL modified
                updatedDiscount = true;
                productsTableModel.setValueAt(priceWDiscountSymbol, i, 6);
            }
        } catch (SQLException ex) {
            System.out.println("Problem in SQL Table Represent");
            ex.printStackTrace();
        }
        // Table Display Model
        productsTable.setRowHeight(50);
        TableColumnModel columnModel = productsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(3);  // Select
        columnModel.getColumn(1).setPreferredWidth(24); // ID
        columnModel.getColumn(2).setPreferredWidth(138);// Product
        columnModel.getColumn(3).setPreferredWidth(17); // Image
        columnModel.getColumn(4).setPreferredWidth(41); // Category
        columnModel.getColumn(5).setPreferredWidth(100);// Keywords
        columnModel.getColumn(6).setPreferredWidth(30); // Price/Day
        columnModel.getColumn(7).setPreferredWidth(50); // Discount/Day
    }

    // deprecated, for choose days with a JSpinner
//    public class daysListener implements ChangeListener {
//
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            JSpinner spinner = (JSpinner) e.getSource();
//            RentMyStuff.totalDays = (int) spinner.getValue();
//
//            try {
//                Connection con = RentMyStuff.startConnection();
//
//                String updDaysOrderSQL = "UPDATE Orders SET total_days=? WHERE id_order=?";
//                String updDaysOrderLineSQL = "UPDATE order_line SET days=? WHERE id_order=?";
//                PreparedStatement stmtUpdDay = con.prepareStatement(updDaysOrderSQL);
//                stmtUpdDay.setInt(1, RentMyStuff.totalDays);
//                stmtUpdDay.setInt(2, idOrder);
//                stmtUpdDay.executeUpdate();
//                System.out.println("totalDays in Order: " + RentMyStuff.totalDays);
//                PreparedStatement stmtUpdDayLine = con.prepareStatement(updDaysOrderLineSQL);
//                stmtUpdDayLine.setInt(1, RentMyStuff.totalDays);
//                stmtUpdDayLine.setInt(2, idOrder);
//                stmtUpdDayLine.executeUpdate();
//
//                RentMyStuff.closeStatement(stmtUpdDay);
//                RentMyStuff.closeStatement(stmtUpdDayLine);
//                RentMyStuff.stopConnection(con);
//            } catch (SQLException ex) {
//                System.out.println("Cannot UPDATE total_days in Order");
//                ex.printStackTrace();
//            }
//            updateTotalPrice(RentMyStuff.totalDays);
//        }
//    }
    public static void readXML() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        try {
            OrderUI.createInvoicesFolder();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            File dir = new File(OrderUI.xmlFolder.toUri());
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (RentMyStuff.DEBUGwin) {
                        System.out.println("\nFile existing: " + child.getName());
                    }
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(child);
                    doc.getDocumentElement().normalize();

                    if (doc.getDocumentElement().getNodeName().equals("invoice")) {

                        NodeList nodeListId = doc.getElementsByTagName("product");
                        for (int i = 0; i < nodeListId.getLength(); ++i) {
                            Node prod = nodeListId.item(i);
                            ReservedProduct reservedProduct = new ReservedProduct();
                            if (prod.getNodeType() == Node.ELEMENT_NODE) {
                                Element pElement = (Element) prod;
                                String prodId = pElement.getElementsByTagName("id").item(0).getTextContent();
                                reservedProduct.setProductId(prodId);
                                if (RentMyStuff.DEBUGwin) {
                                    System.out.println("Product id: " + prodId);
                                }

                            }
                            // Here nodeList contains all the nodes with
                            // name dates.
                            NodeList nodeListDates = doc.getElementsByTagName("dates");

                            // Iterate through all the nodes in NodeList
                            // using for loop.
                            for (int it = 0; it < nodeListDates.getLength(); ++it) {
                                Node date = nodeListDates.item(it);
                                if (date.getNodeType() == Node.ELEMENT_NODE) {
                                    Element dElement = (Element) date;
                                    String startDate = dElement.getElementsByTagName("start-rent-day").item(0).getTextContent();
                                    String endDate = dElement.getElementsByTagName("end-rent-day").item(0).getTextContent();

                                    reservedProduct.setStartDate(LocalDate.parse(startDate, dateFormat));
                                    reservedProduct.setEndDate(LocalDate.parse(endDate, dateFormat));
                                    if (RentMyStuff.DEBUGwin) {
                                        System.out.println("ReservedProduct StartDate: " + reservedProduct.getStartDate());
                                        System.out.println("ReservedProduct EndDate: " + reservedProduct.getEndDate());
                                    }
                                }
                            }
                            rentReserved.add(reservedProduct);
                        }

                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            System.out.println("Couldn't create DocumentBuilder");
        } catch (SAXException saxe) {
            saxe.printStackTrace();
            System.out.println("Document SAX Exception");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Couln't read document");
        }
    }

    public static void updateOrderLine() {
        String updateOrderLineSQL = "INSERT INTO [rentmystuff].order_line(id_product, id_order) VALUES (?, ?)";
        try {
            Connection con = RentMyStuff.startConnection();
            for (int i = 0; i < productsTableModel.getRowCount(); i++) {
                if (selectedProduct.get(i) != null && selectedProduct.get(i) != false) {
                    PreparedStatement stmtUpdateOrderLine = con.prepareStatement(updateOrderLineSQL);
                    stmtUpdateOrderLine.setInt(1, i + 1);
                    stmtUpdateOrderLine.setInt(2, RentMyStuff.order.getId());
                    stmtUpdateOrderLine.executeUpdate();

                    RentMyStuff.closeStatement(stmtUpdateOrderLine);
                }
            }
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't update order_line New Products");
        }
    }

    private static void updateTotalPrice(int days) {
        double finalPriceSum = 0.0;
        for (int i = 0; i < productsTableModel.getRowCount(); i++) {
            if (selectedProduct.get(i) != null && selectedProduct.get(i) != false) {
                double pricePerProduct = pricePerDayMap.get(i);
                double pricePerMoreDay = (pricePerProduct * ((100.0 - discountPerDayMap.get(i)) / 100));
                double pricePerDays;
                if (days == 0) {
                    pricePerDays = 0.0;
                } else {
                    pricePerDays = pricePerProduct + ((days - 1) * pricePerMoreDay);
                }
                double finalPriceWithCD = pricePerDays * ((100.0 - RentMyStuff.customer.getDiscount()) / 100);
                finalPriceSum += finalPriceWithCD;
            }
        }
        String finalPriceSumFormat = String.format("%.2f", finalPriceSum);
        RentMyStuff.order.setAmount(Double.parseDouble(finalPriceSumFormat));
        totalPriceField.setText(finalPriceSumFormat);

        // update Order amount
        try {
            Connection con = RentMyStuff.startConnection();
            PreparedStatement stmtAmnt = con.prepareStatement("UPDATE [rentmystuff].Orders SET amount=? WHERE id_order=?");
            stmtAmnt.setDouble(1, Double.parseDouble(finalPriceSumFormat));
            stmtAmnt.setInt(2, idOrder);
            stmtAmnt.executeUpdate();

            RentMyStuff.closeStatement(stmtAmnt);
            RentMyStuff.stopConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Cannot update total amount of the Order");
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
        daysSpinner = new javax.swing.JSpinner();
        customerLabel = new javax.swing.JLabel();
        datePanel = new javax.swing.JPanel();
        daysField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        menuPanel = new javax.swing.JPanel();
        userButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        shippingButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        invoiceButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        productAddButton = new javax.swing.JButton();

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

        daysSpinner.setModel(spinnerModel);
        daysSpinner.setValue(1);

        customerLabel.setFont(new java.awt.Font("Birthstone Bounce", 0, 26)); // NOI18N
        customerLabel.setText(com.alocoifindo.ecommerce.RentMyStuff.customer.getUsername() + "    ");

        datePanel.setMaximumSize(new java.awt.Dimension(277, 94));
        datePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        daysField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        daysField.setText("1");

        jLabel1.setText("Start Date:");

        jLabel3.setText("End Date:");

        userButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/customer_20.png"))); // NOI18N
        userButton.setBorder(null);
        userButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userButtonActionPerformed(evt);
            }
        });
        menuPanel.add(userButton);
        menuPanel.add(jSeparator3);

        shippingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/truck_20-03.png"))); // NOI18N
        shippingButton.setBorder(null);
        shippingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shippingButtonActionPerformed(evt);
            }
        });
        menuPanel.add(shippingButton);
        menuPanel.add(jSeparator2);

        invoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/invoice_20-04.png"))); // NOI18N
        invoiceButton.setBorder(null);
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });
        menuPanel.add(invoiceButton);
        menuPanel.add(jSeparator1);

        productAddButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add-to-collection_20-02.png"))); // NOI18N
        productAddButton.setBorder(null);
        productAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productAddButtonActionPerformed(evt);
            }
        });
        menuPanel.add(productAddButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(datePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(73, 73, 73)
                                        .addComponent(jLabel3))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(menuPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(logoLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(toLabel)
                                        .addGap(12, 12, 12)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(0, 3, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(daysLabel))
                            .addComponent(daysField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(totalPriceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(totalPriceField))
                        .addGap(85, 85, 85)
                        .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(createOrderButton))
                    .addComponent(tableScrollPane))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(itemsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(itemsLabel)
                            .addComponent(discountLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(itemsField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                            .addComponent(discountField))
                        .addGap(13, 13, 13))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logoLabel)
                            .addComponent(customerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toLabel)
                            .addComponent(customerLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(menuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addGap(0, 0, 0)
                        .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(daysLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(daysField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(createOrderButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        UserUI.removeMessages();
        UserUI.userUI.setUpButtons();
        UserUI.userUI.setVisible(true);
    }//GEN-LAST:event_userButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        InvoicesUI.invoiceTableView();
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void shippingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shippingButtonActionPerformed
        ShipmentUI.shipmentTableView();
    }//GEN-LAST:event_shippingButtonActionPerformed

    private void productAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productAddButtonActionPerformed
        JOptionPane.showMessageDialog(null, "Add Product Button not available yet!");
        System.out.println("Add Product Button not available yet!");
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
    private javax.swing.JPanel datePanel;
    private static javax.swing.JTextField daysField;
    private javax.swing.JLabel daysLabel;
    private static javax.swing.JSpinner daysSpinner;
    private static javax.swing.JTextField discountField;
    private javax.swing.JLabel discountLabel;
    private javax.swing.JButton invoiceButton;
    public static javax.swing.JTextField itemsField;
    private javax.swing.JLabel itemsLabel;
    private javax.swing.JList<String> itemsList;
    private javax.swing.JScrollPane itemsScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel menuPanel;
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
