/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for sharing purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author facundoferreyra
 */
public class OrderUI extends javax.swing.JFrame {

    static OrderChecklistTableModel orderTableModel = new OrderChecklistTableModel();
    static OrderUI orderUI = new OrderUI();
    
    static Map<Integer, Double> totalPricesMap = new HashMap<Integer, Double>();
    static int counter = 0;
    static Map<Integer, Double> finalPricesUncheckMap = new HashMap<Integer, Double>();
    static List<Double> pricePerDayList = new ArrayList<>();
    static List<Integer> discountPerDayList = new ArrayList<>();
    static double finalPriceSum = 0.0;
    static double taxes;
    
    static int discountCustomer;
    
    static boolean data_remain;
    
    static String xmlFile;
    public static final String XML_DIR = "src/main/resources/xml_invoice/";
    public static final String OUTPUT_DIR = "src/main/resources/output";
    
    /**
     * Creates new form OrderUI
     */
    public OrderUI() {
        initComponents();
        setLocationRelativeTo(null);

        orderTable.setRowHeight(25);
        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(4);  // Select
        columnModel.getColumn(1).setPreferredWidth(24); // ID
        columnModel.getColumn(2).setPreferredWidth(146);// Product
        columnModel.getColumn(3).setPreferredWidth(33); // Base Price
        columnModel.getColumn(4).setPreferredWidth(15); // Days
        columnModel.getColumn(5).setPreferredWidth(58); // Discount/Day
        columnModel.getColumn(6).setPreferredWidth(53); // Price on Days
        columnModel.getColumn(7).setPreferredWidth(33); // Customer Discount
        columnModel.getColumn(8).setPreferredWidth(50); // Price with Discount

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);


        JTableHeader header = orderTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(orderTable));


        orderTable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );
        orderTable.getColumnModel().getColumn(4).setCellRenderer( centerRenderer );
        orderTable.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
        orderTable.getColumnModel().getColumn(6).setCellRenderer( centerRenderer );
        orderTable.getColumnModel().getColumn(7).setCellRenderer( centerRenderer ); 
        
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        // dispose by ESCAPE_KEY
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        am.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
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
            super(new String[]{"Select", "ID", "Product", "Base Price", "Days", "Discount/Day", "Price on Days", "User Disc.", "Final Price"}, 0);
            addTableModelListener(this);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Class clazz = String.class;
            switch (columnIndex) {
                case 0:
                    clazz = Boolean.class;
                    break;
                case 4:
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
                  // deprecated function
//                case 4:
//                    return column == 3;
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
                
            }  else if (4 == column) {                                          // Days
                Vector rowData = (Vector)getDataVector().get(row);
                int days = (Integer) aValue;
                rowData.set(4, days);
                System.out.println("Data entry in Days Cell: " + days);
                
                System.out.println("setValueAt Row: " + row);
                pricePerProduct = pricePerDayList.get(row);
                double pricePerMoreDay = (pricePerProduct * ((100.0 - discountPerDayList.get(row)) / 100));
                double pricePerDays = pricePerProduct + ((days -1) * pricePerMoreDay);
                double finalPriceWithCD = pricePerDays * ((100.0 - discountCustomer) / 100);

                totalPricesMap.remove(row);
                totalPricesMap.put(row, finalPriceWithCD);
                finalPricesUncheckMap.put(row, finalPriceWithCD);

                System.out.println("Value of product per Day: " + pricePerProduct);
                System.out.println("Value of product per each MoreDay: " + pricePerMoreDay);
                
                String pricePerProductDisplay = (String.format("%.2f", pricePerProduct)) + " €";
                String pricePerDaysDisplay = (String.format("%.2f", pricePerDays)) + " €";
                String finalPriceWithCDDisplay = (String.format("%.2f", finalPriceWithCD)) + " €";
                String priceZeroDisplay = (String.format("%.2f", 00.00)) + " €";
                
                if (days > 1) {
                    rowData.set(6, pricePerDaysDisplay);
                    rowData.set(8, finalPriceWithCDDisplay);
                } else if (days <= 0) {
                    rowData.set(6, priceZeroDisplay);
                    rowData.set(8, priceZeroDisplay);
                } else {
                    rowData.set(6, pricePerProductDisplay);
                    rowData.set(8, finalPriceWithCDDisplay);
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
                // if modify days in OrderUI table
//                if (col == 3) {
//
//                    int daysRow = (Integer)tableModel.getValueAt(row, 3);
//                    System.out.println("Day uploaded to SQL after tableChanged: " + daysRow);
//
//                    int productRow = ApplicationMain.productsInOrder.get(row).getId();
//                    System.out.println("productsInOrder: " + ApplicationMain.productsInOrder.get(row).getProductName());
//
//                    try {
//                        Connection con = ApplicationMain.startConnection();
//                        // UPDATE row 3 "Days" to form daysPerProduct
//                        PreparedStatement stmtUpdDays = con.prepareStatement("UPDATE order_line SET days=? WHERE id_product=?");
//
//                            stmtUpdDays.setInt(1, daysRow);
//                            stmtUpdDays.setInt(2, productRow);
//                            System.out.println("id_product update in order_line: " + productRow);
//                            System.out.println("days updated in order_line: " + daysRow);
//
//                            stmtUpdDays.executeUpdate();
//
//                        ApplicationMain.stopConnection(con);
//                    } catch (SQLException ex) {
//                        System.out.println("order_line INSERT failed");
//                        ex.printStackTrace();
//                    }
//                }

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
                        totalPricesMap.put(row, finalPricesUncheckMap.get(row));
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
                        System.out.println("Value to retrieve from order_line: " + totalPricesMap.get(row) + " (from row): " + row);
                    }
                    finalPricesUncheckMap.put(row, totalPricesMap.get(row));
                    totalPricesMap.remove(row);
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
        totalPricesMap.clear();
        counter = 0;
        try {
            Connection con = ApplicationMain.startConnection();

            String selectProductsOrderSQL = "SELECT \n" +
                                            "orders.id_tocustomer,\n" +
                                            "order_line.id_order,\n" +
                                            "order_line.id_product,\n" +
                                            "id_product_named,\n" +
                                            "CONCAT(brand, \" \", model_name) AS Product, \n" +
                                            "price_per_day, \n" +
                                            "days, \n" +
                                            "discount_per_days, \n" +
                                            "customers.discount\n" +
                                            "FROM Products \n" +
                                            "INNER JOIN order_line ON Products.id_product = order_line.id_product\n" +
                                            "INNER JOIN Orders ON order_line.id_order = Orders.id_order\n" +
                                            "INNER JOIN Customers ON Orders.id_toCustomer=Customers.id_user\n" +
                                            "INNER JOIN Users ON Customers.id_user = Users.id_user\n" +
                                            "WHERE Orders.id_order=?";
            
            PreparedStatement stmtProductsOrder = con.prepareStatement(selectProductsOrderSQL);
            stmtProductsOrder.setInt(1, ApplicationMain.order.getId());

            ResultSet rsProductsOrder = stmtProductsOrder.executeQuery();
            
            while (rsProductsOrder.next()) {
                // Get ResultSet of Products of the Order
                int idProduct = rsProductsOrder.getInt("id_product");
                String idProductNamed = rsProductsOrder.getString("id_product_named");
                String productName = rsProductsOrder.getString("Product");
                int days = rsProductsOrder.getInt("days");
                System.out.println("Day retrieved from SQL to Table after SELECT: " + days);
                
                // price_per_day
                double pricePerDay = rsProductsOrder.getDouble("price_per_day");
                String pricePerDayDisplay = (String.format("%.2f", pricePerDay)) + " €";
                pricePerDayList.add(pricePerDay);
                
                // discount_per_days
                int discountPerDay = rsProductsOrder.getInt("discount_per_days");
                String discountPerDayDisplay = discountPerDay + " %";
                discountPerDayList.add(discountPerDay);
                
                // discount_per_customer
                discountCustomer = rsProductsOrder.getInt("discount");
                String discountCustomerDisplay = discountCustomer+ " %";
                
                // price_per_days (Before Customer Discount) & total_price_per_product
                double priceBfCD = 00.00;
                double totalPricePerProduct = 00.00;
                if (days > 1) {
                    priceBfCD = pricePerDay + ((pricePerDay * (days - 1)) * ((100.0 - discountPerDay) / 100));
                    totalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                } else {
                    priceBfCD = pricePerDay * days;
                    totalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                }
                String priceBfCDDisplay = String.format("%.2f", priceBfCD) + " €";
                String totalPricePerProductDisplay = String.format("%.2f", totalPricePerProduct) + " €";
                totalPricesMap.put(counter, totalPricePerProduct);
                counter++;
                ApplicationMain.productsInOrder.add(new Product(idProduct, idProductNamed, productName, pricePerDay, discountPerDay));
                Object[] orderRow = {true, idProductNamed, productName, pricePerDayDisplay, days, discountPerDayDisplay, priceBfCDDisplay, discountCustomerDisplay, totalPricePerProductDisplay};
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
            if (totalPricesMap.get(i) != null) {
                finalPriceSum += totalPricesMap.get(i);
            }
        }
        finalPriceField.setText(String.format("%.2f", finalPriceSum));
    }
    
    public static void updateTaxes() {
        taxes = finalPriceSum * 0.21;
        taxesField.setText(String.format("%.2f", taxes));
    }
    
    public static void createDocXML() {
        // XML document build
        xmlFile = String.format("%06d", ApplicationMain.order.getId()) + ".xml";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //root elements
            Document doc = docBuilder.newDocument();

            Element invoiceElm = doc.createElement("invoice");
            doc.appendChild(invoiceElm);

            //set attribute to invoice element
            invoiceElm.setAttribute("id", String.format("%06d", ApplicationMain.order.getId()));
            
            //date element
            Element dateElm = doc.createElement("date");
            dateElm.appendChild(doc.createTextNode(ApplicationMain.order.getCreationDate()));
            invoiceElm.appendChild(dateElm);

            //invoice-to elements
            Element invoiceToElm = doc.createElement("invoice-to");
            invoiceElm.appendChild(invoiceToElm);
            
                //firstname element
                Element nameCstm = doc.createElement("name");
                nameCstm.appendChild(doc.createTextNode(ApplicationMain.customer.getFirstname() + " " + ApplicationMain.customer.getLastname()));
                invoiceToElm.appendChild(nameCstm);

                //address-line element
                Element addressLineCstm = doc.createElement("address-line");
                addressLineCstm.appendChild(doc.createTextNode(ApplicationMain.customer.getAddressLine()));
                invoiceToElm.appendChild(addressLineCstm);

                //city element
                Element cityCstm = doc.createElement("city");
                cityCstm.appendChild(doc.createTextNode(ApplicationMain.customer.getCity()));
                invoiceToElm.appendChild(cityCstm);

                //city element
                Element postalcodeCstm = doc.createElement("postalcode");
                postalcodeCstm.appendChild(doc.createTextNode(String.valueOf(ApplicationMain.customer.getPostalcode())));
                invoiceToElm.appendChild(postalcodeCstm);

                //country element
                Element countryCstm = doc.createElement("country");
                countryCstm.appendChild(doc.createTextNode("Spain"));
                invoiceToElm.appendChild(countryCstm);

                //email element
                Element emailCstm = doc.createElement("email");
                emailCstm.appendChild(doc.createTextNode(ApplicationMain.customer.getEmail()));
                invoiceToElm.appendChild(emailCstm);
                
                //telephone element
                Element telephoneCstm = doc.createElement("telephone");
                telephoneCstm.appendChild(doc.createTextNode(String.valueOf(ApplicationMain.customer.getTelephone())));
                invoiceToElm.appendChild(telephoneCstm);
                
            //invoice-from elements
            Element invoiceFromElm = doc.createElement("invoice-from");
            invoiceElm.appendChild(invoiceFromElm);
            
                //firstname element
                Element nameOwnr = doc.createElement("name");
                nameOwnr.appendChild(doc.createTextNode("Facundo Ferreyra"));
                invoiceFromElm.appendChild(nameOwnr);
                
                //nif element
                Element nifOwnr = doc.createElement("nif");
                nifOwnr.appendChild(doc.createTextNode("X5554778X"));
                invoiceFromElm.appendChild(nifOwnr);

                //address-line element
                Element addressLineOwnr = doc.createElement("address-line");
                addressLineOwnr.appendChild(doc.createTextNode("C. de Sumatra 88"));
                invoiceFromElm.appendChild(addressLineOwnr);

                //city element
                Element cityOwnr = doc.createElement("city");
                cityOwnr.appendChild(doc.createTextNode("Rubí"));
                invoiceFromElm.appendChild(cityOwnr);

                //city element
                Element postalcodeOwnr = doc.createElement("postalcode");
                postalcodeOwnr.appendChild(doc.createTextNode("08191"));
                invoiceFromElm.appendChild(postalcodeOwnr);

                //country element
                Element countryOwnr = doc.createElement("country");
                countryOwnr.appendChild(doc.createTextNode("Spain"));
                invoiceFromElm.appendChild(countryOwnr);

                //email element
                Element emailOwnr = doc.createElement("email");
                emailOwnr.appendChild(doc.createTextNode("alocoifindo@gmail.com"));
                invoiceFromElm.appendChild(emailOwnr);
                
                //telephone element
                Element telephoneOwnr = doc.createElement("telephone");
                telephoneOwnr.appendChild(doc.createTextNode("626544440"));
                invoiceFromElm.appendChild(telephoneOwnr);

            //order elements
            Element orderElm = doc.createElement("order");
            invoiceElm.appendChild(orderElm);
                
                try {
                    Connection con = ApplicationMain.startConnection();

                    String selectProductsOrderSQL = "SELECT \n" +
                                                    "orders.id_tocustomer,\n" +
                                                    "order_line.id_order,\n" +
                                                    "order_line.id_product,\n" +
                                                    "CONCAT(brand, \" \", model_name) AS Product, \n" +
                                                    "price_per_day, \n" +
                                                    "days, \n" +
                                                    "discount_per_days, \n" +
                                                    "customers.discount\n" +
                                                    "FROM Products \n" +
                                                    "INNER JOIN order_line ON Products.id_product = order_line.id_product\n" +
                                                    "INNER JOIN Orders ON order_line.id_order = Orders.id_order\n" +
                                                    "INNER JOIN Customers ON Orders.id_toCustomer=Customers.id_user\n" +
                                                    "INNER JOIN Users ON Customers.id_user = Users.id_user\n" +
                                                    "WHERE Orders.id_order=?";

                    PreparedStatement stmtProductsOrder = con.prepareStatement(selectProductsOrderSQL);
                    stmtProductsOrder.setInt(1, ApplicationMain.order.getId());

                    ResultSet rsProductsOrder = stmtProductsOrder.executeQuery();

                    while (rsProductsOrder.next()) {
                        //Product elements
                        Element productElm = doc.createElement("product");
                        orderElm.appendChild(productElm);
                        
                        //id element
                        int idProduct = rsProductsOrder.getInt("id_product");
                        Element idProductElm = doc.createElement("id");
                        idProductElm.appendChild(doc.createTextNode(String.valueOf(idProduct)));
                        productElm.appendChild(idProductElm);
                        
                        //product-name element
                        String productName = rsProductsOrder.getString("Product");
                        Element productNameElm = doc.createElement("product-name");
                        productNameElm.appendChild(doc.createTextNode(productName));
                        productElm.appendChild(productNameElm);
                        
                        //days element
                        int days = rsProductsOrder.getInt("days");
                        Element daysElm = doc.createElement("days");
                        daysElm.appendChild(doc.createTextNode(String.valueOf(days)));
                        productElm.appendChild(daysElm);

                        // price_per_day element
                        double pricePerDay = rsProductsOrder.getDouble("price_per_day");
                        Element pricePerDayElm = doc.createElement("price_per_day");
                        pricePerDayElm.appendChild(doc.createTextNode(String.format("%.2f", pricePerDay)));
                        productElm.appendChild(pricePerDayElm);

                        // discount_per_days element
                        int discountPerDay = rsProductsOrder.getInt("discount_per_days");
                        Element discountPerDayElm = doc.createElement("discount_per_days");
                        discountPerDayElm.appendChild(doc.createTextNode(String.valueOf(discountPerDay)));
                        productElm.appendChild(discountPerDayElm);

                        // price_per_days (Before Customer Discount) element
                        double priceBfCD = 00.00;
                        double totalPricePerProduct = 00.00;
                        if (days > 1) {
                            priceBfCD = pricePerDay + ((pricePerDay * (days - 1)) * ((100.0 - discountPerDay) / 100));
                            totalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                        } else {
                            priceBfCD = pricePerDay * days;
                            totalPricePerProduct = priceBfCD * ((100.0 - discountCustomer) / 100);
                        }
                        Element productLineElm = doc.createElement("unit-price");
                        productLineElm.appendChild(doc.createTextNode(String.format("%.2f", priceBfCD)));
                        productElm.appendChild(productLineElm);
                        
                        // discount_per_customer element
                        int discountCustomer = rsProductsOrder.getInt("discount");
                        Element discountCustomerElm = doc.createElement("discount");
                        discountCustomerElm.appendChild(doc.createTextNode(String.valueOf(discountCustomer)));
                        productElm.appendChild(discountCustomerElm);
                        
                        // total_price_per_product element
                        Element totalPricePerProductElm = doc.createElement("total-price");
                        totalPricePerProductElm.appendChild(doc.createTextNode(String.format("%.2f", totalPricePerProduct)));
                        productElm.appendChild(totalPricePerProductElm);
                    }
                    ApplicationMain.stopConnection(con);

                } catch (SQLException ex) {
                    System.out.println("Problem in SQL Order retrieve");
                    ex.printStackTrace();
                }
            
            // taxes element
            Element taxesElm = doc.createElement("taxes-applied");
            taxesElm.appendChild(doc.createTextNode(String.format("%.2f", taxes)));
            invoiceElm.appendChild(taxesElm);
            //set attribute to taxes element
            taxesElm.setAttribute("percentage", "21");

            // total_price element
            Element totalPriceElm = doc.createElement("total-invoice-price");
            totalPriceElm.appendChild(doc.createTextNode(String.format("%.2f", finalPriceSum)));
            invoiceElm.appendChild(totalPriceElm);
                
            //write the content into xml file
            TransformerFactory transformerFactory =  TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result =  new StreamResult(new File(XML_DIR + xmlFile));
            transformer.transform(source, result);

            System.out.println("XML Invoice Done");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    public static void convertToPDF() throws IOException, FOPException, TransformerException {
        // the XSL FO file
        File xsltFile = new File(XML_DIR + "invoice_template.xsl");
        // the XML file which provides the input
        StreamSource xmlSource = new StreamSource(new File(XML_DIR + xmlFile));
        // create an instance of fop factory
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        // a user agent is needed for transformation
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        // Setup output
        OutputStream out = new java.io.FileOutputStream(OUTPUT_DIR + "/invoice_" + String.format("%06d", ApplicationMain.order.getId()) + ".pdf");

        try {
            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            // Resulting SAX events (the generated FO) must be piped through to
            // FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            // That's where the XML is first transformed to XSL-FO and then
            // PDF is created
            transformer.transform(xmlSource, res);
        } finally {
            out.close();
        }
    }
    
    private boolean userDataCheck() {
        String firstnameSet = "";
        String lastnameSet = "";
        String addressLineSet = "";
        String citySet = "";
        int postalcodeSet = 0;
        int telephoneSet = 0;
        String emailSet = ""; 
        
        try {
            Connection con = ApplicationMain.startConnection();
            PreparedStatement stmtDataChck = con.prepareStatement("SELECT * FROM Customers WHERE id_user=?");
            stmtDataChck.setInt(1, ApplicationMain.customer.getId());
            ResultSet rsDataChck = stmtDataChck.executeQuery();
            rsDataChck.next();
            
            firstnameSet = rsDataChck.getString("firstname");
            lastnameSet = rsDataChck.getString("lastname");
            addressLineSet = rsDataChck.getString("address_line");
            citySet = rsDataChck.getString("city");
            postalcodeSet = rsDataChck.getInt("postalcode");
            telephoneSet = rsDataChck.getInt("telephone");
            emailSet = rsDataChck.getString("email");
            
            System.out.println("--- User Data Check ---");
            System.out.println(firstnameSet);
            System.out.println(lastnameSet);
            System.out.println(addressLineSet);
            System.out.println(citySet);
            System.out.println(postalcodeSet);
            System.out.println(telephoneSet);
            System.out.println(emailSet);
            System.out.println("------------------------");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't check customer info");
        }        
        
        if (firstnameSet.equals("") || lastnameSet.equals("") || addressLineSet.equals("") || 
            citySet.equals("") || postalcodeSet == 0 || telephoneSet == 0 || emailSet.equals("")) {
             return true;
        } else {
            return false;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        taxesField = new javax.swing.JTextField();
        finalPriceField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        invoiceButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        customerLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        orderTable.setModel(orderTableModel);
        orderTable.setToolTipText("");
        jScrollPane1.setViewportView(orderTable);

        jLabel2.setText("Taxes Incl.:");

        taxesField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

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

        customerLabel.setText(ApplicationMain.customer.getUsername());

        jLabel1.setText("Customer:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerLabel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(customerLabel))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(invoiceButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(finalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taxesField, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)))
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(taxesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finalPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(invoiceButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        // Checker of completed profile to create invoice.
        data_remain = userDataCheck();
        
        
        // if profile not updated, open UserUI
        if (!data_remain) {
            createDocXML();
            try {
                convertToPDF();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (FOPException fope) {
                fope.printStackTrace();
            } catch (TransformerException te) {
                te.printStackTrace();
            }
        } else {
            int n = JOptionPane.showConfirmDialog(orderUI, "User data needed for invoice, would you like to update?", "Complete profile for Invoice", JOptionPane.YES_NO_OPTION);
            System.out.println("Ansewer to user_data_update: " + n);
            if (n == 0) {
                UserUI.userUI.setVisible(true);
            }
        }
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable orderTable;
    private static javax.swing.JTextField taxesField;
    // End of variables declaration//GEN-END:variables
}
