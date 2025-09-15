import java.sql.*;
import  javax.swing.*;

public class DBconnection implements AutoCloseable {
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/car_maintenance";
    // WARNING: Storing credentials directly in the code is a security risk.
    // In a production environment, use a secure configuration file or environment variables.
    private final String user = "root";
    private final String password = "Cmt@2222";

    public DBconnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to MySQL database!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database connection failed!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("🔌 Database connection closed.");
        }
    }
}