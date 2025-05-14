import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;
import static javax.management.remote.JMXConnectorFactory.connect;

public class Helper {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/original"; // Ensure this matches your DB
    private final String dbUsername = "root"; // Your MySQL username
    private final String dbPassword = "collins09"; // Your MySQL password

    // Method to get a connection to the database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }
    public void addStationToDatabase(String stationName) {
        String normalizedStationName = stationName.trim().toLowerCase(); // Normalize name
        String queryCheck = "SELECT * FROM stations WHERE LOWER(TRIM(station_name)) = ?";
        String queryUpdate = "UPDATE stations SET total_sales = total_sales + ?, created_at = ? WHERE LOWER(TRIM(station_name)) = ?";
        String queryInsert = "INSERT INTO stations (station_name, total_sales, created_at) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            // Check if the station already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(queryCheck)) {
                checkStmt.setString(1, normalizedStationName); // Use normalized name for checking
                ResultSet rs = checkStmt.executeQuery();

                double totalSales = 0;
                if (rs.next()) {
                    // Station exists, update total_sales
                    try (PreparedStatement updateStmt = conn.prepareStatement(queryUpdate)) {
                        updateStmt.setDouble(1, totalSales); // Adding to total sales
                        updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // Update created_at timestamp
                        updateStmt.setString(3, normalizedStationName); // Use normalized name for the condition
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Station " + stationName + " updated in the database.");
                        }
                    }
                } else {
                    // Station does not exist, add new record
                    try (PreparedStatement insertStmt = conn.prepareStatement(queryInsert)) {
                        insertStmt.setString(1, stationName.trim()); // Use original name with trimmed spaces
                        insertStmt.setDouble(2, totalSales);
                        insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                        int rowsAffected = insertStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Station " + stationName + " added to the database.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding/updating station to the database.");
        }
    }


    public String loadStationName() {
        String query = "SELECT station_name FROM stations LIMIT 1"; // Assuming there is only one station
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("station_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving station name from the database.");
        }
        return null; // Return null if no station is found
    }



    public List<CakeDistributed> loadCakesDistributed(String selectedStation) {
        List<CakeDistributed> cakesDistributed = new ArrayList<>();
        String sql = "SELECT * FROM cakes_distributed WHERE station_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the selected station in the prepared statement
            pstmt.setString(1, selectedStation);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String stationName = rs.getString("station_name");
                    String cakeName = rs.getString("cake_name");
                    int quantity = rs.getInt("quantity");
                    Timestamp distributionTimestamp = rs.getTimestamp("distribution_date"); // Retrieve as Timestamp

                    Date distributionDate = distributionTimestamp != null ? new Date(distributionTimestamp.getTime()) : null; // Convert to Date if not null

                    CakeDistributed cakeDistributed = new CakeDistributed(id, stationName, cakeName, quantity, distributionDate);
                    cakesDistributed.add(cakeDistributed);
                    System.out.println("Fetched distributed cake record: " + cakeName + " from " + stationName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading distributed cakes from the database.");
        }

        return cakesDistributed;
    }



   public void populateSubstationDropdown(JComboBox<String> stationDropdown) {
        try {
            Connection conn = getConnection();
            String query = "SELECT  station_name FROM stations"; // Assuming your stations table has id and name
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Create a DefaultComboBoxModel to hold the station names
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

            while (rs.next()) {
                String stationName = rs.getString("station_name");
                model.addElement(stationName); // Add the station name to the dropdown
            }

            // Set the model to the JComboBox
            stationDropdown.setModel(model);

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean authenticateStation(String selectedStation, String enteredPassword) {
        try {
            Connection conn = getConnection();
            String query = "SELECT password FROM stations WHERE station_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedStation);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(enteredPassword); // Check if passwords match
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void displayItemsForStation(String selectedStation) {
        // Get the distributed cakes for the selected station
        List<CakeDistributed> distributedItems = loadCakesDistributed(selectedStation);

        // Check if there are any items
        if (distributedItems.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items distributed for this station.", "No Items", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Prepare the table model to display distributed items
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Cake Name", "Quantity", "Distribution Date"}, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Add distributed cakes to the table model
        for (CakeDistributed item : distributedItems) {
            tableModel.addRow(new Object[]{
                    item.getCakeName(),
                    item.getQuantity(),
                    item.getDistributionDate() != null ? sdf.format(item.getDistributionDate()) : "N/A"
            });
        }

        // Create the JTable and show it in a JScrollPane
        JTable itemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        // Display the table in a dialog
        JOptionPane.showMessageDialog(null, scrollPane, "Items for " + selectedStation, JOptionPane.INFORMATION_MESSAGE);
    }

    public List<String> getAllCakeNames() {
        List<String> cakeNames = new ArrayList<>();
        String query = "SELECT cake_name FROM inventory"; // Assuming your table has a column named cake_name

        try (Connection conn = getConnection(); // Get a connection
             Statement stmt = conn.createStatement(); // Create a Statement
             ResultSet rs = stmt.executeQuery(query)) { // Execute the query

            while (rs.next()) {
                String cakeName = rs.getString("cake_name"); // Fetch cake name from result set
                cakeNames.add(cakeName); // Add it to the list
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle query execution error
        }

        return cakeNames; // Return the list of cake names
    }

    public void addItemsSold(String stationName, String cakeName, int quantity) {
        String queryCheckItem = "SELECT * FROM items_sold WHERE station_name = ? AND cake_name = ? ORDER BY update_time DESC LIMIT 1";
        String queryUpdateItem = "UPDATE items_sold SET quantity = quantity + ? WHERE id = ?";
        String queryInsertItem = "INSERT INTO items_sold (station_name, cake_name, quantity, update_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        String queryCheckDaily = "SELECT total_sales FROM daily_sales WHERE station_name = ? AND sales_date = ?";
        String queryUpdateDaily = "UPDATE daily_sales SET total_sales = total_sales + ? WHERE station_name = ? AND sales_date = ?";
        String queryInsertDaily = "INSERT INTO daily_sales (station_name, sales_date, total_sales) VALUES (?, ?, ?)";

        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Check or update items_sold
            try (PreparedStatement checkItemStmt = conn.prepareStatement(queryCheckItem)) {
                checkItemStmt.setString(1, stationName);
                checkItemStmt.setString(2, cakeName);
                ResultSet rsItem = checkItemStmt.executeQuery();

                if (rsItem.next()) {
                    Timestamp updateTime = rsItem.getTimestamp("update_time");
                    int recordId = rsItem.getInt("id");

                    java.sql.Date recordDate = new java.sql.Date(updateTime.getTime());
                    java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

                    if (recordDate.equals(currentDate)) {
                        try (PreparedStatement updateItemStmt = conn.prepareStatement(queryUpdateItem)) {
                            updateItemStmt.setInt(1, quantity);
                            updateItemStmt.setInt(2, recordId);
                            updateItemStmt.executeUpdate();
                        }
                    } else {
                        insertNewRecord(conn, stationName, cakeName, quantity);
                    }
                } else {
                    insertNewRecord(conn, stationName, cakeName, quantity);
                }
            }

            // Update daily_sales
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            try (PreparedStatement checkDailyStmt = conn.prepareStatement(queryCheckDaily)) {
                checkDailyStmt.setString(1, stationName);
                checkDailyStmt.setDate(2, currentDate);
                ResultSet rsDaily = checkDailyStmt.executeQuery();

                if (rsDaily.next()) {
                    try (PreparedStatement updateDailyStmt = conn.prepareStatement(queryUpdateDaily)) {
                        updateDailyStmt.setInt(1, quantity);
                        updateDailyStmt.setString(2, stationName);
                        updateDailyStmt.setDate(3, currentDate);
                        updateDailyStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertDailyStmt = conn.prepareStatement(queryInsertDaily)) {
                        insertDailyStmt.setString(1, stationName);
                        insertDailyStmt.setDate(2, currentDate);
                        insertDailyStmt.setInt(3, quantity);
                        insertDailyStmt.executeUpdate();
                    }
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("Item and daily sales records updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            System.out.println("Error adding/updating item and daily sales records.");
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Close connection
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
    private void insertNewRecord(Connection conn, String stationName, String cakeName, int quantity) throws SQLException {
        String queryInsertItem = "INSERT INTO items_sold (station_name, cake_name, quantity, update_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement insertItemStmt = conn.prepareStatement(queryInsertItem)) {
            insertItemStmt.setString(1, stationName);
            insertItemStmt.setString(2, cakeName);
            insertItemStmt.setInt(3, quantity);
            insertItemStmt.executeUpdate();
            System.out.println("New item sales record added: " + cakeName + " at " + stationName);
        }


    }
    public List<ItemsSold> loadItemsSoldForStation(String stationName) {
        List<ItemsSold> itemsSold = new ArrayList<>();
        String query = "SELECT id, cake_name, quantity, update_time FROM items_sold WHERE station_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stationName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String cakeName = rs.getString("cake_name");
                int quantity = rs.getInt("quantity");
                Timestamp updateTimestamp = rs.getTimestamp("update_time"); // Correct type
                Date updateTime = updateTimestamp != null ? new Date(updateTimestamp.getTime()) : null; // Convert to Date

                itemsSold.add(new ItemsSold(id, stationName, cakeName, quantity, updateTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemsSold;
    }

    public void saveBookingsToDatabase(JTable bookingsTable, String selectedStation) {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        int lastRow = model.getRowCount() - 1;

        if (lastRow >= 0) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO bookings (booker_name, status, cake_name, quantity, date_booked, amount_paid, amount_left, station) " +
                                 "VALUES (?, 'Pending', ?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, model.getValueAt(lastRow, 0).toString()); // Booker Name
                stmt.setString(2, model.getValueAt(lastRow, 2).toString()); // Cake Name
                stmt.setInt(3, Integer.parseInt(model.getValueAt(lastRow, 3).toString())); // Quantity
                stmt.setString(4, model.getValueAt(lastRow, 4).toString()); // Date Booked
                stmt.setDouble(5, Double.parseDouble(model.getValueAt(lastRow, 6).toString())); // Amount Paid
                stmt.setDouble(6, Double.parseDouble(model.getValueAt(lastRow, 7).toString())); // Amount Left
                stmt.setString(7, selectedStation); // Add selected station

                stmt.executeUpdate();
                System.out.println("Booking saved successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Method to update the booking as cleared
    public boolean markBookingAsClearedByName(String bookerName, String dateCleared) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE bookings SET status = 'Cleared', date_cleared = ? WHERE booker_name = ?")) {

            stmt.setString(1, dateCleared);
            stmt.setString(2, bookerName);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Booking marked as cleared.");
                return true; // Success
            } else {
                System.out.println("Booking not found.");
                return false; // No booking found with the provided name
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> fetchBookingsForStation(String selectedStation) {
        List<Booking> bookings = new ArrayList<>();

        String query = "SELECT id, booker_name, status, cake_name, quantity, date_booked, date_cleared, amount_paid, amount_left, station FROM bookings WHERE station = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, selectedStation);  // Pass the selected station to filter the data

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                            rs.getInt("id"),
                            rs.getString("booker_name"),
                            rs.getString("status"),
                            rs.getString("cake_name"),
                            rs.getInt("quantity"),
                            rs.getDate("date_booked"),
                            rs.getDate("date_cleared"),
                            rs.getDouble("amount_paid"),
                            rs.getDouble("amount_left"),
                            rs.getString("station")
                    );
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }



public List<CakeDistributed> getCakesDistributedOnDate(Date selectedDate, String stationName) {
        List<CakeDistributed> cakes = new ArrayList<>();
        String query = "SELECT cake_name, quantity, distribution_date FROM cakes_distributed WHERE station_name = ? AND DATE(distribution_date) = ?";

        System.out.println("Executing getCakesDistributedOnDate with: Station=" + stationName + ", Date=" + selectedDate);

        if (stationName == null || stationName.trim().isEmpty()) {
            System.err.println("ERROR: Station name is null or empty!");
            return cakes;
        }

        if (selectedDate == null) {
            System.err.println("ERROR: Selected date is null!");
            return cakes;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, stationName);
            stmt.setDate(2, new java.sql.Date(selectedDate.getTime()));

            System.out.println("SQL Query: " + stmt.toString());  // Debugging SQL query

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CakeDistributed cake = new CakeDistributed(
                        rs.getString("cake_name"),
                        rs.getInt("quantity"),
                        rs.getDate("distribution_date")
                );
                System.out.println("Retrieved: " + cake.getCakeName() + " - " + cake.getQuantity() + " - " + cake.getDistributionDate());
                cakes.add(cake);
            }

        } catch (SQLException e) {
            System.err.println("SQL ERROR: Failed to execute query - " + e.getMessage());
            e.printStackTrace();
        }
        return cakes;
    }



    public List<ItemsSold> getItemsSoldOnDate(Date selectedDate, String stationName) {
        List<ItemsSold> sales = new ArrayList<>();
        String query = "SELECT cake_name, quantity, update_time FROM items_sold WHERE station_name = ? AND DATE(update_time) = ?";

        System.out.println("Executing getItemsSoldOnDate with: Station=" + stationName + ", Date=" + selectedDate);

        if (stationName == null || stationName.trim().isEmpty()) {
            System.err.println("ERROR: Station name is null or empty!");
            return sales;
        }

        if (selectedDate == null) {
            System.err.println("ERROR: Selected date is null!");
            return sales;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, stationName);
            stmt.setDate(2, new java.sql.Date(selectedDate.getTime()));

            System.out.println("SQL Query: " + stmt.toString());  // Debugging SQL query

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemsSold sale = new ItemsSold(
                        rs.getInt("id"),
                        stationName,
                        rs.getString("cake_name"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("update_time") // Ensure correct type
                );
                System.out.println("Retrieved: " + sale.getCakeName() + " - " + sale.getQuantity() + " - " + sale.getUpdateTime());
                sales.add(sale);
            }

        } catch (SQLException e) {
            System.err.println("SQL ERROR: Failed to execute query - " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }
    public Double getCakePriceByName(String cakeName) {
        String query = "SELECT price FROM inventory WHERE cake_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cakeName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    System.out.println("Fetched price for " + cakeName + ": " + price);
                    return price;
                } else {
                    System.out.println("Cake not found: " + cakeName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found or on error
    }


}





