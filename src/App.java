import java.sql.*;
import javax.swing.*;

public class App {
    private static Connection connection;
    private JFrame frame;

    public static void main(String[] args) {
      createTables();
        SwingUtilities.invokeLater(() -> new App().showLoginScreen());
    }

   public static void createTables() {
    try (
        DBconnection db = new DBconnection();
        Connection conn = db.getConnection();
        Statement stmt = conn.createStatement()
    ) {
        // Users table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
            "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) NOT NULL UNIQUE, " +
            "password VARCHAR(255) NOT NULL)"); 

        // Vehicles table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS vehicles (" +
            "vehicle_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "user_id INT, " + 
            "make VARCHAR(50) NOT NULL, " +
            "model VARCHAR(50) NOT NULL, " +
            "year INT NOT NULL, " +
            "vin VARCHAR(17) UNIQUE, " +
            "license_plate VARCHAR(15), " +
            "current_mileage INT NOT NULL, " +
            "photo_path VARCHAR(255), " +
            "notes TEXT, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users(user_id))"); 

        // Maintenance table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS maintenance (" +
            "service_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "vehicle_id INT NOT NULL, " +
            "service_date DATE NOT NULL, " +
            "mileage INT NOT NULL, " +
            "cost DECIMAL(10,2), " +
            "parts TEXT, " +
            "mechanic VARCHAR(100), " +
            "receipt_path VARCHAR(255), " +
            "FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id))");

        // Fuel entries table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS fuel_entries (" +
            "entry_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "vehicle_id INT NOT NULL, " +
            "fuel_date DATE NOT NULL, " +
            "mileage INT NOT NULL, " +
            "gallons DECIMAL(10,3) NOT NULL, " +
            "price_per_unit DECIMAL(10,3) NOT NULL, " +
            "total_price DECIMAL(10,2) NOT NULL, " +
            "station VARCHAR(100), " +
            "notes TEXT, " +
            "FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id))");

        // Reminders table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS reminders (" +
            "reminder_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "vehicle_id INT NOT NULL, " +
            "reminder_type VARCHAR(50) NOT NULL, " +
            "due_mileage INT, " +
            "due_date DATE, " +
            "description TEXT, " +
            "completed BOOLEAN DEFAULT FALSE, " +
            "FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id))");

        System.out.println("✅ All tables are available.");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null,
            "Database table creation failed!\\n" + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
   }

    public void showLoginScreen() {
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("Car Maintenance Tracker - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.add(new LoginPanel(this));
        frame.setVisible(true);
    }

    public void showMainApplication(String username, int userId) {
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("Car Maintenance Tracker - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);
        
        frame.add(new Dashboard(userId));

        frame.setVisible(true);
    }
}