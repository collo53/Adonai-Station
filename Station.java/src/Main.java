import  javax.swing.*;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.toedter.calendar.JDateChooser;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import static com.formdev.flatlaf.util.HiDPIUtils.repaint;


// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JFrame {
    private JPanel authPanel, mainPanel;
    private boolean isAuthenticated = false;
    private JComboBox<String> stationDropdown;
    private JList<String> itemsList; // To display the items for the selected station
    private Helper db;





    public Main() {
        setTitle("ADONAI");

        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Apply FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        db =new Helper();

        initAuthPanel();
        mainPanel = new JPanel(new BorderLayout());

        setContentPane(authPanel);
        setVisible(true);
    }

    private void initAuthPanel() {
        authPanel = new JPanel(new GridBagLayout());
        authPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        authPanel.setBackground(new Color(230, 238, 245)); // Subtle bluish tint

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        FontIcon loginIcon = FontIcon.of(FontAwesomeSolid.BIRTHDAY_CAKE, 20, Color.RED);
        JLabel titleLabel = new JLabel(" Login to Adonai System ", loginIcon, JLabel.LEFT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(237, 41, 57));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        authPanel.add(titleLabel, gbc);

        // Station Label
        JLabel stationLabel = new JLabel("Select Station:");
        stationLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        authPanel.add(stationLabel, gbc);

        // Station Dropdown
        stationDropdown = new JComboBox<>();
        new Helper().populateSubstationDropdown(stationDropdown);
        gbc.gridx = 1;
        gbc.gridy = 1;
        authPanel.add(stationDropdown, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        authPanel.add(passwordLabel, gbc);

        // Password Field
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        authPanel.add(passwordField, gbc);

        // Error Label
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        authPanel.add(errorLabel, gbc);

        // Login Button
        FontIcon loginPageIcon = FontIcon.of(FontAwesomeSolid.SIGN_IN_ALT, 16, Color.WHITE);
        JButton loginButton = new JButton("Login", loginPageIcon);
        loginButton.setBackground(new Color(237, 41, 57));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        authPanel.add(loginButton, gbc);

        // Login Button Action
        loginButton.addActionListener(e -> {
            String selectedStation = (String) stationDropdown.getSelectedItem();
            String enteredPassword = new String(passwordField.getPassword());

            if (enteredPassword.isEmpty()) {
                errorLabel.setText("⚠ Please enter your password.");
            } else if (!authenticateStation(selectedStation, enteredPassword)) {
                errorLabel.setText("❌ Incorrect password. Please try again.");
            } else {
                switchToMainPanel(selectedStation);
                new Helper().displayItemsForStation(selectedStation);
            }
        });

        add(authPanel);
    }

    private boolean authenticateStation(String selectedStation, String enteredPassword) {
        // Call to Helper to authenticate the station (you should implement it in Helper)
        return db.authenticateStation(selectedStation, enteredPassword);
    }

    private void switchToMainPanel(String selectedStation) {
        if (!isAuthenticated) {
            isAuthenticated = true;
            initializeMainPanel( selectedStation); // Setup the main panel with components
            setContentPane(mainPanel);
            revalidate();
            repaint();
        }
    }

    private void initializeMainPanel(String selectedStation ) {
        // Create welcome label
        JLabel welcomeLabel = new JLabel("WELCOME TO " +selectedStation, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(30, 136, 129));
        tabbedPane.setForeground(Color.white);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adding Tabs
        FontIcon addDistributionIcon = FontIcon.of(FontAwesomeSolid.PLUS, 20, Color.WHITE);
        FontIcon orderIcon = FontIcon.of(FontAwesomeSolid.SHOPPING_CART, 20, Color.WHITE);
        FontIcon bookingIcon = FontIcon.of(FontAwesomeSolid.CALENDAR_ALT, 20, Color.WHITE);

        tabbedPane.addTab("Items Distributed",addDistributionIcon, createItemsPanel(welcomeLabel, selectedStation));

        tabbedPane.addTab("Sales",orderIcon, createSalesPanel("Content for Sales",selectedStation));
        tabbedPane.addTab("Bookings",bookingIcon, createBookingsPanel("Content for Bookings",selectedStation));

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }
    private  static  JPanel createItemsPanel(JLabel label, String selectedStation){

        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        itemsPanel.setBackground(UIManager.getColor("Panel.background"));

       DefaultTableModel itemsTableModel =new DefaultTableModel(new Object[]{"Item Name", "Quantity" ,"Distribution date" }, 0);

       Helper db =new Helper();
       List<CakeDistributed> distibutedCakes =db.loadCakesDistributed(selectedStation);

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (CakeDistributed cake :distibutedCakes){
            itemsTableModel.addRow(new Object[]{
                    cake.getCakeName(),
                    cake.getQuantity(),
                    cake.getDistributionDate() != null ? sdf.format(cake.getDistributionDate()):"N/A"
            });
        }



        JTable itemsTable = new JTable(itemsTableModel){
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            if (!isRowSelected(row)) {
                // Alternate row colors for better readability
                c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : new Color(225, 225, 225));
            }
            return c;
        }
    };


        TableRowSorter<TableModel> sorter = new TableRowSorter<>(itemsTableModel);
        itemsTable.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new FlowLayout());
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // Set date format

        filterPanel.add(new JLabel("Filter by Date"));
        filterPanel.add(dateChooser);

// Add an event listener to detect date selection changes
        // Declare SimpleDateFormat at the top to reuse it

        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                Date selectedDate = dateChooser.getDate();
                RowFilter<TableModel, Object> rowFilter = null;

                if (selectedDate != null) {
                    String formattedDate = sdf.format(selectedDate); // Use existing sdf
                    rowFilter = RowFilter.regexFilter("^" + formattedDate, 2); // Column 4 is "Date Booked"
                }

                sorter.setRowFilter(rowFilter);
            }
        });



        itemsTable.setDefaultEditor(Object.class,null);
        JTableHeader header=itemsTable.getTableHeader();
        header.setFont(UIManager.getFont("TableHeader.font"));
        header.setBackground(UIManager.getColor("TableHeader.background"));
        header.setForeground(UIManager.getColor("TableHeader.foreground"));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i=0; i<itemsTable.getColumnModel().getColumnCount(); i++){
            itemsTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        itemsTable.setRowHeight(25);
        itemsTable.setShowGrid(true);
        itemsTable.setGridColor(UIManager.getColor("Table.gridColor"));
        itemsTable.setFillsViewportHeight(true);
        JScrollPane scrollPane= new JScrollPane(itemsTable);


        JPanel buttonPanel= new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        buttonPanel.setOpaque(false);

        FontIcon viewInventoryIcon = FontIcon.of(FontAwesome.EYE, 20, Color.WHITE);
        JButton viewButton = createStyledButton("View", new Color(30,136,129), Color.WHITE);
        buttonPanel.add(viewButton);
        viewButton.setIcon(viewInventoryIcon);


        viewButton.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(itemsPanel, "Please select a date first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            System.out.println("Fetching data for: " + sqlDate + " and Station: " + selectedStation);

            List<CakeDistributed> filteredCakes = db.getCakesDistributedOnDate(sqlDate, selectedStation);
            System.out.println("Total records found: " + filteredCakes.size());

            SwingUtilities.invokeLater(() -> {
                itemsTableModel.setRowCount(0); // Clear table
                System.out.println("Table cleared. Adding new records...");

                for (CakeDistributed cake : filteredCakes) {
                    System.out.println("Adding: " + cake.getCakeName() + " - " + cake.getQuantity() + " - " + sdf.format(cake.getDistributionDate()));
                    itemsTableModel.addRow(new Object[]{
                            cake.getCakeName(),
                            cake.getQuantity(),
                            cake.getDistributionDate() != null ? sdf.format(cake.getDistributionDate()) : "N/A"
                    });
                }

                itemsTableModel.fireTableDataChanged(); // Ensure data is refreshed
                itemsTable.revalidate();
                itemsTable.repaint();

                System.out.println("Table update complete. Rows now: " + itemsTableModel.getRowCount());
            });
        });








        itemsPanel.add(scrollPane, BorderLayout.CENTER);
        itemsPanel.add(filterPanel,BorderLayout.NORTH);
        itemsPanel.add(buttonPanel, BorderLayout.WEST);
        itemsTable.setVisible(true);




        return itemsPanel;
    }


    private JButton createStyledButton3(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255)); // Set button background color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listener for hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255)); // Change background on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 144, 255)); // Revert background color
            }
        });

        return button;
    }


    private  static JPanel createSalesPanel(String labelText, String selectedStation){
        JPanel salesPanel = new JPanel();
        salesPanel.setLayout(new BorderLayout());
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        salesPanel.setBackground(UIManager.getColor("Panel.background"));
        DefaultTableModel salesTableModel =new DefaultTableModel(new Object[]{"Item Name","Quantity","Update Time"} ,0);


        Helper db =new Helper();
        List<ItemsSold> itemsSold =db.loadItemsSoldForStation(selectedStation);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (ItemsSold cake :itemsSold){
            salesTableModel.addRow(new Object[]{
                    cake.getCakeName(),
                    cake.getQuantity(),
                    cake.getUpdateTime() != null ? sdf.format(cake.getUpdateTime()):"N/A"
            });
        }

        JTable salesTable = new JTable(salesTableModel){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Alternate row colors for better readability
                    c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : new Color(225, 225, 225));
                }
                return c;
            }
        };


        TableRowSorter<TableModel> sorter = new TableRowSorter<>(salesTableModel);
        salesTable.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new FlowLayout());
       JDateChooser dateChooser = new JDateChooser();
       dateChooser.setDateFormatString("yyyy-MM-dd");

       filterPanel.add(new JLabel("Filter by Date"));
       filterPanel.add(dateChooser);


        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                Date selectedDate = dateChooser.getDate();
                RowFilter<TableModel, Object> rowFilter = null;

                if (selectedDate != null) {
                    String formattedDate = sdf.format(selectedDate); // Use existing sdf
                    rowFilter = RowFilter.regexFilter("^" + formattedDate, 2); // Column 4 is "Date Booked"
                }

                sorter.setRowFilter(rowFilter);
            }
        });




        salesTable.setDefaultEditor(Object.class,null);
        JTableHeader header=salesTable.getTableHeader();
        header.setFont(UIManager.getFont("TableHeader.font"));
        header.setBackground(UIManager.getColor("TableHeader.background"));
        header.setForeground(UIManager.getColor("TableHeader.foreground"));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i=0; i<salesTable.getColumnModel().getColumnCount(); i++){
            salesTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        salesTable.setRowHeight(25);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(UIManager.getColor("Table.gridColor"));
        salesTable.setFillsViewportHeight(true);
        JScrollPane scrollPane= new JScrollPane(salesTable);

        JPanel buttonPanel =new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.setOpaque(false);

        FontIcon addSaleIcon = FontIcon.of(FontAwesomeSolid.PLUS, 20, Color.WHITE);
        JButton addSaleButton = createStyledButton ("Add Sale",  new Color(30,136,129), Color.WHITE);
       addSaleButton.setIcon(addSaleIcon);
        buttonPanel.add(addSaleButton);

        addSaleButton.addActionListener(e ->openItemsSoldDialog(salesTableModel ,selectedStation));

        FontIcon viewSaleIcon = FontIcon.of(FontAwesome.EYE, 20, Color.WHITE);
        JButton viewSalesButton = createStyledButton("View Sale" ,new Color(30,136,129),Color.WHITE);
        viewSalesButton.setIcon(viewSaleIcon);
        buttonPanel.add(viewSalesButton);



        viewSalesButton.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(salesPanel, "Please select a date first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            System.out.println("Fetching data for: " + sqlDate + " and Station: " + selectedStation);

            // ✅ Fetch sales instead of distributed cakes
            List<ItemsSold> filteredSales = db.getItemsSoldOnDate(sqlDate, selectedStation);
            System.out.println("Total records found: " + filteredSales.size());

            SwingUtilities.invokeLater(() -> {
                salesTableModel.setRowCount(0); // Clear table
                System.out.println("Table cleared. Adding new records...");

                for (ItemsSold sale : filteredSales) {
                    System.out.println("Adding: " + sale.getCakeName() + " - " + sale.getQuantity() + " - " + sdf.format(sale.getUpdateTime()));
                    salesTableModel.addRow(new Object[]{
                            sale.getCakeName(),
                            sale.getQuantity(),
                            sale.getUpdateTime() != null ? sdf.format(sale.getUpdateTime()) : "N/A"
                    });
                }

                salesTableModel.fireTableDataChanged(); // Ensure data is refreshed
                salesTable.revalidate();
                salesTable.repaint();

                System.out.println("Table update complete. Rows now: " + salesTableModel.getRowCount());
            });
        });



        salesPanel.add(scrollPane, BorderLayout.CENTER);
        salesPanel.add(filterPanel,BorderLayout.NORTH);
        salesPanel.add(buttonPanel, BorderLayout.SOUTH);



        salesPanel.setVisible(true);




        return salesPanel;
    }

    private static void openItemsSoldDialog(DefaultTableModel tableModel, String stationName ) {
        JDialog addItemsSoldDialog = new JDialog(); // Create dialog
        addItemsSoldDialog.setTitle("Add Item Sold");
        addItemsSoldDialog.setSize(400, 300);
        addItemsSoldDialog.setLayout(new BorderLayout());
        addItemsSoldDialog.setModal(true); // Makes dialog modal
        addItemsSoldDialog.setLocationRelativeTo(null); // Center on screen

        // Create content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(230, 238, 245)); // Light background color
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure fields stretch
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Item Name ComboBox
        contentPanel.add(new JLabel("Item Name:"), gbc);
        String[] cakeNames = fetchCakeNames(); // Method to fetch cake names from the database
        JComboBox<String> itemNameComboBox = new JComboBox<>(cakeNames);
        itemNameComboBox.setEditable(true); // Make the combo box editable
        itemNameComboBox.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        contentPanel.add(itemNameComboBox, gbc);

        // Quantity TextField
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Quantity:"), gbc);
        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        contentPanel.add(quantityField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245));

        // Add Button
        JButton addButton = createFlatButton("Add Sale", e -> {
            String itemName = (String) itemNameComboBox.getSelectedItem();
            String quantity = quantityField.getText();

            if (itemName.isEmpty() || quantity.isEmpty()) {
                JOptionPane.showMessageDialog(addItemsSoldDialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int qty = Integer.parseInt(quantity);
                Helper db = new Helper();

                // Load distributed cakes for this station
                List<CakeDistributed> distributedCakes = db.loadCakesDistributed(stationName);

                // Find matching cake entry
                CakeDistributed matchedCake = distributedCakes.stream()
                        .filter(c -> c.getCakeName().equalsIgnoreCase(itemName))
                        .findFirst()
                        .orElse(null);

                if (matchedCake == null) {
                    showInsufficientDialog("This item was not distributed to the station.");
                    return;
                }

                if (qty > matchedCake.getQuantity()) {
                    showInsufficientDialog("Only " + matchedCake.getQuantity() + " cakes are available. You cannot sell more than that.");
                    return;
                }

                // Proceed with sale
                db.addItemsSold(stationName, itemName, qty); // Save to DB
                addItemsSoldDialog.dispose();

                // Update table
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                tableModel.addRow(new Object[]{itemName, qty, currentTime});

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addItemsSoldDialog, "Quantity must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Cancel Button
        JButton cancelButton = createFlatButton("Cancel", e -> addItemsSoldDialog.dispose());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        addItemsSoldDialog.add(contentPanel, BorderLayout.CENTER);
        addItemsSoldDialog.add(buttonPanel, BorderLayout.SOUTH);

        addItemsSoldDialog.setVisible(true); // Display dialog
    }

    private static void showInsufficientDialog(String message) {
        JDialog insufficientDialog = new JDialog();
        insufficientDialog.setTitle("⚠️ Insufficient Quantity");
        insufficientDialog.setSize(400, 200);
        insufficientDialog.setLayout(new BorderLayout());
        insufficientDialog.setModal(true);
        insufficientDialog.setLocationRelativeTo(null);
        insufficientDialog.getContentPane().setBackground(new Color(230, 238, 245));

        // Message Label
        JLabel msgLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msgLabel.setForeground(new Color(60, 60, 60));
        msgLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        // Custom OK Button
        JButton okButton = new JButton("OK");
        okButton.setFocusPainted(false);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(new Color(66, 133, 244));
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setBorder(BorderFactory.createEmptyBorder());

        // Hover effect
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                okButton.setBackground(new Color(52, 103, 191));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                okButton.setBackground(new Color(66, 133, 244));
            }
        });

        okButton.addActionListener(e -> insufficientDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        buttonPanel.add(okButton);

        // Rounded dialog (optional for undecorated dialog)
        insufficientDialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        insufficientDialog.add(msgLabel, BorderLayout.CENTER);
        insufficientDialog.add(buttonPanel, BorderLayout.SOUTH);

        insufficientDialog.setVisible(true);
    }


    // Helper method to create flat styled buttons
    private static JButton createFlatButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground( new Color(30,136,129)); // Blue background
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 136, 229))); // Thin border for flat look
        button.addActionListener(actionListener);
        button.setPreferredSize(new Dimension(180, 40)); // Set preferred size for consistency
        return button;
    }
    private static String[] fetchCakeNames() {
        Helper db = new Helper();
        List<String> cakeNamesList = db.getAllCakeNames(); // Assuming you have this method in your DatabaseHelper
        return cakeNamesList.toArray(new String[0]); // Convert list to array
    }

    private static JPanel createBookingsPanel(String labelText, String selectedStation) {
        JPanel bookingsPanel = new JPanel();
        bookingsPanel.setLayout(new BorderLayout());
        bookingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bookingsPanel.setBackground(UIManager.getColor("Panel.background"));

        DefaultTableModel bookingsTableModel = new DefaultTableModel(
                new Object[]{"Name", "Status", "Cake", "Quantity", "Date Booked", "Date Cleared", "Amount Paid", "Amount Left"}, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

        JTable bookingsTable = new JTable(bookingsTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Alternate row colors for better readability
                    c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : new Color(225, 225, 225));
                }
                return c;
            }
        };

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(bookingsTableModel);
        bookingsTable.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new FlowLayout());
        JTextField dateFilterField = new JTextField(10);
        dateFilterField.setToolTipText("Enter date (yyyy-mm-dd) to filter");
        filterPanel.add(new JLabel("Filter by Date"));
        filterPanel.add(dateFilterField);

        dateFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String dateText = dateFilterField.getText();
                RowFilter<TableModel, Object> rowFilter = null;

                if (!dateText.trim().isEmpty()) {
                    try {
                        Date filterDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateText);
                        rowFilter = RowFilter.regexFilter("^" + filterDate.toString(), 2); // Column 2 is "Date Booked"
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(filterPanel, "Invalid date format. Please use yyyy-mm-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                sorter.setRowFilter(rowFilter);
            }
        });

        bookingsTable.setDefaultEditor(Object.class, null);
        JTableHeader header = bookingsTable.getTableHeader();
        header.setFont(UIManager.getFont("TableHeader.font"));
        header.setBackground(UIManager.getColor("TableHeader.background"));
        header.setForeground(UIManager.getColor("TableHeader.foreground"));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < bookingsTable.getColumnModel().getColumnCount(); i++) {
            bookingsTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        bookingsTable.setRowHeight(25);
        bookingsTable.setShowGrid(true);
        bookingsTable.setGridColor(UIManager.getColor("Table.gridColor"));
        bookingsTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.setOpaque(false);
        FontIcon BookingIcon = FontIcon.of(FontAwesome.CREDIT_CARD, 20, Color.WHITE);
        JButton bookButton = createStyledButton("Book", new Color(30, 136, 129), Color.WHITE);
        bookButton.setIcon(BookingIcon);
        buttonPanel.add(bookButton);
        FontIcon ClearIcon = FontIcon.of(FontAwesomeSolid.USER_CHECK, 20, Color.WHITE);
        JButton clearButton = createStyledButton("Clear", new Color(30, 136, 129), Color.WHITE);
        clearButton.setIcon(ClearIcon);
        buttonPanel.add(clearButton);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        searchPanel.setOpaque(false);

        JTextField searchBar = new JTextField(15);
        FontIcon searchBookingIcon = FontIcon.of(FontAwesome.SEARCH, 20, Color.WHITE);
        JButton searchButton = createStyledButton("Search", new Color(30, 136, 129), Color.WHITE);
        searchButton.setIcon(searchBookingIcon);
        searchPanel.add(searchBar);
        searchPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String searchText = searchBar.getText().trim();

            if (!searchText.isEmpty()) {
                RowFilter<TableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchText, 0); // Column 0 is "Name"
                sorter.setRowFilter(rowFilter);
            } else {
                sorter.setRowFilter(null); // Remove filter when search text is empty
            }
        });

        bookingsPanel.add(scrollPane, BorderLayout.CENTER);
        bookingsPanel.add(filterPanel, BorderLayout.NORTH);
        bookingsPanel.add(buttonPanel, BorderLayout.WEST);
        bookingsPanel.add(searchPanel, BorderLayout.NORTH);

        bookButton.addActionListener(e -> openBookDialog(bookingsTableModel, bookingsTable, selectedStation));
        clearButton.addActionListener(e -> clearButtonDialog(bookingsTableModel, bookingsTable));

        // Call the method to load data
        loadBookingsData(bookingsTableModel, selectedStation);

        bookingsPanel.setVisible(true);

        return bookingsPanel;
    }

    // Modify to load data based on station
    private static void loadBookingsData(DefaultTableModel tableModel, String selectedStation) {
        Helper db = new Helper();
        List<Booking> bookings = db.fetchBookingsForStation(selectedStation); // Ensure this is returning the correct data

        // Debugging: Check if bookings are being fetched
        System.out.println("Fetched bookings for station " + selectedStation + ": " + bookings.size() + " bookings found.");

        // Clear the table before adding new data
        tableModel.setRowCount(0);

        // Add rows to the table model
        for (Booking booking : bookings) {
            tableModel.addRow(new Object[]{
                    booking.getBookerName(),
                    booking.getStatus(),
                    booking.getCakeName(),
                    booking.getQuantity(),
                    booking.getDateBooked(),
                    booking.getDateCleared(),
                    booking.getAmountPaid(),
                    booking.getAmountLeft(),
                    booking.getStation() // Add station data if necessary
            });
        }
    }


    private static JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Add padding inside the button
        return button;
    }

    private static void openBookDialog(DefaultTableModel tableModel, JTable bookingsTable, String selectedStation) {
        JDialog openBookDialog = new JDialog();
        openBookDialog.setTitle("Add Booking");
        openBookDialog.setSize(500, 500);
        openBookDialog.setLayout(new BorderLayout());
        openBookDialog.setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(255, 255, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Helper db = new Helper();  // Instantiate only once

        // Booker Name
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Booker Name:"), gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        contentPanel.add(nameField, gbc);

        // Cake Name
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Cake Name:"), gbc);

        String[] cakeNames = fetchCakeNames(); // Fetch cake names
        JComboBox<String> cakeNameComboBox = new JComboBox<>(cakeNames);
        cakeNameComboBox.setEditable(true);
        cakeNameComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        contentPanel.add(cakeNameComboBox, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("Quantity:"), gbc);

        JTextField quantityField = new JTextField(10);
        gbc.gridx = 1;
        contentPanel.add(quantityField, gbc);

        // Amount Paid
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("Amount Paid:"), gbc);

        JTextField amountPaidField = new JTextField(10);
        gbc.gridx = 1;
        contentPanel.add(amountPaidField, gbc);

        // Amount Left
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("Amount Left:"), gbc);

        JTextField amountLeftField = new JTextField(10);
        amountLeftField.setEditable(false);  // Make read-only
        gbc.gridx = 1;
        contentPanel.add(amountLeftField, gbc);

        // Auto-update Amount Left
        Runnable updateAmountLeft = () -> {
            try {
                String selectedCake = (String) cakeNameComboBox.getSelectedItem();
                Double pricePerCake = db.getCakePriceByName(selectedCake);

                if (pricePerCake == null) return;

                int quantity = Integer.parseInt(quantityField.getText().trim());
                double amountPaid = Double.parseDouble(amountPaidField.getText().trim());

                double totalPrice = quantity * pricePerCake;
                double amountLeft = totalPrice - amountPaid;
                amountLeftField.setText(String.format("%.2f", amountLeft));
            } catch (Exception ignored) {
                amountLeftField.setText("");
            }
        };

        // Listeners to trigger update
        quantityField.getDocument().addDocumentListener(new SimpleDocumentListener(updateAmountLeft));
        amountPaidField.getDocument().addDocumentListener(new SimpleDocumentListener(updateAmountLeft));
        cakeNameComboBox.addActionListener(e -> updateAmountLeft.run());

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(30, 136, 129));
        saveButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 5;
        contentPanel.add(saveButton, gbc);

        openBookDialog.add(contentPanel, BorderLayout.CENTER);

        // Save logic
        saveButton.addActionListener(e -> {
            String bookerName = nameField.getText().trim();
            String cakeName = (String) cakeNameComboBox.getSelectedItem();
            String quantityText = quantityField.getText().trim();
            String amountPaidText = amountPaidField.getText().trim();
            String amountLeftText = amountLeftField.getText().trim();

            if (bookerName.isEmpty() || cakeName.isEmpty() || quantityText.isEmpty() ||
                    amountPaidText.isEmpty() || amountLeftText.isEmpty()) {
                JOptionPane.showMessageDialog(openBookDialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String dateBooked = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            tableModel.addRow(new Object[]{
                    bookerName,
                    "Pending",
                    cakeName,
                    Integer.parseInt(quantityText),
                    dateBooked,
                    null,
                    Double.parseDouble(amountPaidText),
                    Double.parseDouble(amountLeftText),
                    selectedStation
            });

            db.saveBookingsToDatabase(bookingsTable, selectedStation);
            openBookDialog.dispose();
        });

        openBookDialog.setVisible(true);
    }


    private static void clearButtonDialog(DefaultTableModel tableModel, JTable bookingsTable) {
        int selectedRow = bookingsTable.getSelectedRow();

        // Ensure a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a booking to clear.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the original row index (accounting for any sorting)
        int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);

        // Create the dialog
        JDialog clearDialog = new JDialog();
        clearDialog.setTitle("Clear Booking");
        clearDialog.setSize(400, 300);
        clearDialog.setLayout(new BorderLayout());
        clearDialog.setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(255, 255, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Display selected booking details
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Booker Name:"), gbc);

        JTextField nameField = new JTextField(tableModel.getValueAt(modelRow, 0).toString(), 20);
        nameField.setEditable(false);
        gbc.gridx = 1;
        contentPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Cake Name:"), gbc);

        JTextField cakeField = new JTextField(tableModel.getValueAt(modelRow, 2).toString(), 20);
        cakeField.setEditable(false);
        gbc.gridx = 1;
        contentPanel.add(cakeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Date Cleared:"), gbc);

        // Date Cleared Field
        JTextField dateClearedField = new JTextField(10);
        dateClearedField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // Default to today's date
        gbc.gridx = 1;
        contentPanel.add(dateClearedField, gbc);

        // Save Button
        JButton saveButton = new JButton("Mark as Cleared");
        saveButton.setBackground(new Color(30, 136, 129));
        saveButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        contentPanel.add(saveButton, gbc);

        clearDialog.add(contentPanel, BorderLayout.CENTER);

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String dateCleared = dateClearedField.getText().trim();

            // Validate the date
            if (dateCleared.isEmpty()) {
                JOptionPane.showMessageDialog(clearDialog, "Please enter a valid cleared date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected row from the table
            int rowIndex= bookingsTable.getSelectedRow();

            // If no row is selected, show an error
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(clearDialog, "Please select a booking first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert the row index from view (filtered) to model
            int modelRowIndex = bookingsTable.convertRowIndexToModel(rowIndex);

            // Get the booker's name from the selected row (column 0 is where the name is stored)
            String bookerName = bookingsTable.getModel().getValueAt(modelRowIndex, 0).toString();


            String currentStatus = bookingsTable.getModel().getValueAt(modelRowIndex, 1).toString();

            // Check if the booking is already cleared
            if ("Cleared".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(clearDialog, "This booking is already cleared!", "Already Cleared", JOptionPane.ERROR_MESSAGE);
                return;
            }


            // Print the booker's name to debug
            System.out.println("Booker's Name: " + bookerName);

            // Update in Database
            Helper db = new Helper();
            boolean success = db.markBookingAsClearedByName(bookerName, dateCleared);

            if (success) {
                // Update in Table Model
                tableModel.setValueAt("Cleared", modelRowIndex, 1); // Update the status to "Cleared"
                tableModel.setValueAt(dateCleared, modelRowIndex, 5); // Set the "Date Cleared" field
                JOptionPane.showMessageDialog(clearDialog, "Booking marked as cleared successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearDialog.dispose(); // Close the dialog


            } else {
                JOptionPane.showMessageDialog(clearDialog, "Booking not found or could not be cleared.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearDialog.setVisible(true);
    }


    // Helper method to check if a string is numeric

    private void changeTheme(String theme) {
        try {
            switch (theme.toLowerCase()) {
                case "light":
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    break;
                case "dark":
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    break;
                case "intellij":
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    break;
                case "darcula":
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown theme: " + theme);
            }
            Component frame = null;
            SwingUtilities.updateComponentTreeUI(frame); // Refresh UI to apply the theme
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        new Main();

    }
}
