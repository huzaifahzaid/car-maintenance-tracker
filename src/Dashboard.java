import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JPanel {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private int currentUserId;

    // UI Components for data entry
    private JTextField makeField, modelField, yearField, vinField, licenseField, mileageField;

    // Table Models
    private DefaultTableModel vehicleTableModel;
    private JTable vehicleTable;

    // Maintenance Panel Components
    private JTable maintenanceTable;
    private DefaultTableModel maintenanceTableModel;
    private JTextField maintenanceDateField, maintenanceMileageField, maintenanceCostField;
    private JTextArea maintenanceDescArea;

    // Fuel Panel Components
    private JTextField fuelDateField, fuelMileageField, fuelGallonsField, fuelCostField;
    private DefaultTableModel fuelTableModel;
    private JTable fuelTable;

    // Reminders Panel Components
    private JTextField reminderTypeField, reminderDateField;
    private JTextArea reminderDescArea;
    private DefaultTableModel reminderTableModel;
    private JTable reminderTable;

    // Colors and Fonts
    private static final Color NAV_BTN_BG = new Color(0, 128, 128); // Teal background
    private static final Color NAV_BTN_HOVER = new Color(32, 178, 170); // Darker teal on hover
    private static final Color BTN_BG = new Color(0, 123, 167); // Blue shade for buttons
    private static final Color BTN_HOVER = new Color(0, 105, 143); // Darker blue on hover

    public Dashboard(int userId) {
        this.currentUserId = userId;
        setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createVehiclesPanel(), "Vehicles");
        mainPanel.add(createAddVehiclePanel(), "Add Vehicle");
        mainPanel.add(createMaintenancePanel(), "Maintenance");
        mainPanel.add(createFuelPanel(), "Fuel");
        mainPanel.add(createRemindersPanel(), "Reminders");
        mainPanel.add(createSettingsPanel(), "Settings");
        mainPanel.add(createAboutPanel(), "About");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(40, 50, 60));
        navPanel.setPreferredSize(new Dimension(220, getHeight()));
        navPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel logoLabel = new JLabel("<html><h2 style='color: #ffffff;'>CarTracker</h2></html>", SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        navPanel.add(logoLabel);

        navPanel.add(createNavButton("Dashboard", "Dashboard"));
        navPanel.add(createNavButton("Vehicles", "Vehicles"));
        navPanel.add(createNavButton("Add Vehicle", "Add Vehicle"));
        navPanel.add(createNavButton("Maintenance", "Maintenance"));
        navPanel.add(createNavButton("Fuel", "Fuel"));
        navPanel.add(createNavButton("Reminders", "Reminders"));
        navPanel.add(Box.createVerticalGlue());

        navPanel.add(createNavButton("Settings", "Settings"));
        navPanel.add(createNavButton("About", "About"));

        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        return navPanel;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setBackground(NAV_BTN_BG);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new RoundedBorder(10, NAV_BTN_BG),
                new EmptyBorder(10, 25, 10, 25)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(NAV_BTN_HOVER);
                button.setBorder(new CompoundBorder(
                        new RoundedBorder(10, NAV_BTN_HOVER),
                        new EmptyBorder(10, 25, 10, 25)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NAV_BTN_BG);
                button.setBorder(new CompoundBorder(
                        new RoundedBorder(10, NAV_BTN_BG),
                        new EmptyBorder(10, 25, 10, 25)
                ));
            }
        });
        button.addActionListener(e -> {
            cardLayout.show(mainPanel, cardName);
            if (cardName.equals("Vehicles")) {
                loadVehicleData();
            }
        });
        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(new Color(244, 246, 249));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new RoundedBorder(20, new Color(220, 220, 220)));

        JLabel welcomeLabel = new JLabel("Welcome back to CarTracker!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // You can add your dashboard content here

        return panel;
    }

    private JPanel createVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("Your Vehicles", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"VIN", "Make", "Model", "Year", "Mileage"};
        vehicleTableModel = new DefaultTableModel(columnNames, 0);
        vehicleTable = new JTable(vehicleTableModel);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton addVehicleButton = new JButton("Add New Vehicle");
        stylePrimaryButton(addVehicleButton);
        addVehicleButton.addActionListener(e -> cardLayout.show(mainPanel, "Add Vehicle"));
        panel.add(addVehicleButton, BorderLayout.SOUTH);

        loadVehicleData();
        return panel;
    }

    private JPanel createAddVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("Add a New Vehicle", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Make:"));
        makeField = new JTextField();
        formPanel.add(makeField);

        formPanel.add(new JLabel("Model:"));
        modelField = new JTextField();
        formPanel.add(modelField);

        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        formPanel.add(yearField);

        formPanel.add(new JLabel("VIN:"));
        vinField = new JTextField();
        formPanel.add(vinField);

        formPanel.add(new JLabel("License Plate:"));
        licenseField = new JTextField();
        formPanel.add(licenseField);

        formPanel.add(new JLabel("Current Mileage:"));
        mileageField = new JTextField();
        formPanel.add(mileageField);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Vehicle");
        stylePrimaryButton(saveButton);
        saveButton.addActionListener(e -> saveVehicle());
        panel.add(saveButton, BorderLayout.SOUTH);

        return panel;
    }

    private void saveVehicle() {
        String make = makeField.getText();
        String model = modelField.getText();
        String yearStr = yearField.getText();
        String vin = vinField.getText();
        String license = licenseField.getText();
        String mileageStr = mileageField.getText();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || vin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            int mileage = mileageStr.isEmpty() ? 0 : Integer.parseInt(mileageStr);

            String sql = "INSERT INTO vehicles (user_id, make, model, year, vin, license_plate, current_mileage) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (DBconnection db = new DBconnection();
                 Connection conn = db.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, currentUserId);
                pstmt.setString(2, make);
                pstmt.setString(3, model);
                pstmt.setInt(4, year);
                pstmt.setString(5, vin);
                pstmt.setString(6, license);
                pstmt.setInt(7, mileage);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearAddVehicleFields();
                    cardLayout.show(mainPanel, "Vehicles");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year and Mileage must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAddVehicleFields() {
        makeField.setText("");
        modelField.setText("");
        yearField.setText("");
        vinField.setText("");
        licenseField.setText("");
        mileageField.setText("");
    }

    // Maintenance panel components and methods
    private JPanel createMaintenancePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("Maintenance Records", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Service Date", "Mileage", "Cost", "Description"};
        maintenanceTableModel = new DefaultTableModel(columns, 0);
        maintenanceTable = new JTable(maintenanceTableModel);
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Maintenance Record"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        maintenanceDateField = new JTextField(12);
        maintenanceMileageField = new JTextField(10);
        maintenanceCostField = new JTextField(10);
        maintenanceDescArea = new JTextArea(3, 20);
        maintenanceDescArea.setLineWrap(true);
        maintenanceDescArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(maintenanceDescArea);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Service Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(maintenanceDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mileage:"), gbc);
        gbc.gridx = 1;
        formPanel.add(maintenanceMileageField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        formPanel.add(maintenanceCostField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);

        JButton addButton = new JButton("Add Record");
        stylePrimaryButton(addButton);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> addMaintenanceRecord());

        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addMaintenanceRecord() {
        String date = maintenanceDateField.getText().trim();
        String mileageStr = maintenanceMileageField.getText().trim();
        String costStr = maintenanceCostField.getText().trim();
        String desc = maintenanceDescArea.getText().trim();

        if (date.isEmpty() || mileageStr.isEmpty() || costStr.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all maintenance fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int mileage = Integer.parseInt(mileageStr);
            double cost = Double.parseDouble(costStr);

            // TODO: Insert into database here

            maintenanceTableModel.addRow(new Object[]{date, mileage, cost, desc});
            maintenanceDateField.setText("");
            maintenanceMileageField.setText("");
            maintenanceCostField.setText("");
            maintenanceDescArea.setText("");
            JOptionPane.showMessageDialog(this, "Maintenance record added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mileage must be integer and cost must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fuel panel components and methods
    private JPanel createFuelPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("Fuel Entries", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Date", "Mileage", "Gallons", "Total Cost"};
        fuelTableModel = new DefaultTableModel(columns, 0);
        fuelTable = new JTable(fuelTableModel);
        JScrollPane scrollPane = new JScrollPane(fuelTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Fuel Entry"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        fuelDateField = new JTextField(12);
        fuelMileageField = new JTextField(10);
        fuelGallonsField = new JTextField(10);
        fuelCostField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(fuelDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mileage:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fuelMileageField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Gallons:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fuelGallonsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Total Cost:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fuelCostField, gbc);

        JButton addButton = new JButton("Add Fuel Entry");
        stylePrimaryButton(addButton);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> addFuelEntry());

        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFuelEntry() {
        String date = fuelDateField.getText().trim();
        String mileageStr = fuelMileageField.getText().trim();
        String gallonsStr = fuelGallonsField.getText().trim();
        String costStr = fuelCostField.getText().trim();

        if (date.isEmpty() || mileageStr.isEmpty() || gallonsStr.isEmpty() || costStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fuel fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int mileage = Integer.parseInt(mileageStr);
            double gallons = Double.parseDouble(gallonsStr);
            double cost = Double.parseDouble(costStr);

            // TODO: Insert into database here

            fuelTableModel.addRow(new Object[]{date, mileage, gallons, cost});

            fuelDateField.setText("");
            fuelMileageField.setText("");
            fuelGallonsField.setText("");
            fuelCostField.setText("");
            JOptionPane.showMessageDialog(this, "Fuel entry added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mileage must be integer and gallons/cost must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding fuel entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reminders panel components and methods
    private JPanel createRemindersPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("Reminders", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Type", "Due Date", "Description"};
        reminderTableModel = new DefaultTableModel(columns, 0);
        reminderTable = new JTable(reminderTableModel);
        JScrollPane scrollPane = new JScrollPane(reminderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Reminder"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        reminderTypeField = new JTextField(15);
        reminderDateField = new JTextField(12);
        reminderDescArea = new JTextArea(3, 20);
        reminderDescArea.setLineWrap(true);
        reminderDescArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(reminderDescArea);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(reminderTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(reminderDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);

        JButton addButton = new JButton("Add Reminder");
        stylePrimaryButton(addButton);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> addReminder());

        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addReminder() {
        String type = reminderTypeField.getText().trim();
        String dueDate = reminderDateField.getText().trim();
        String desc = reminderDescArea.getText().trim();

        if (type.isEmpty() || dueDate.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all reminder fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // TODO: Insert into database here

            reminderTableModel.addRow(new Object[]{type, dueDate, desc});

            reminderTypeField.setText("");
            reminderDateField.setText("");
            reminderDescArea.setText("");
            JOptionPane.showMessageDialog(this, "Reminder added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding reminder: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Settings panel
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("Settings", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        JPanel settingsContent = new JPanel(new GridLayout(3, 1, 15, 15));
        settingsContent.setBackground(Color.WHITE);
        settingsContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel themeLabel = new JLabel("Theme:");
        String[] themes = {"Light", "Dark"};
        JComboBox<String> themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedIndex(0);
        settingsContent.add(new JLabel("Change Theme:"));
        settingsContent.add(themeComboBox);

        JLabel notifLabel = new JLabel("Notifications:");
        JCheckBox notifCheckbox = new JCheckBox("Enable Maintenance Reminders");
        notifCheckbox.setBackground(Color.WHITE);
        settingsContent.add(notifLabel);
        settingsContent.add(notifCheckbox);

        panel.add(settingsContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(new Color(244, 246, 249));

        JLabel header = new JLabel("About This Application", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        JPanel aboutContent = new JPanel(new GridLayout(4, 1, 15, 15));
        aboutContent.setBackground(Color.WHITE);
        aboutContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        aboutContent.add(new JLabel("<html><b>Application:</b> Car Maintenance Tracker</html>"));
        aboutContent.add(new JLabel("<html><b>Version:</b> 1.0</html>"));
        aboutContent.add(new JLabel("<html><b>Author:</b> HUZAIFAH ZAID</html>"));
        aboutContent.add(new JLabel("<html><b>Description:</b> This application helps you manage your vehicle's maintenance, fuel consumption, and reminders.</html>"));

        panel.add(aboutContent, BorderLayout.CENTER);

        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(BTN_BG);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new RoundedBorder(10, BTN_BG),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(BTN_HOVER);
                button.setBorder(new CompoundBorder(
                        new RoundedBorder(10, BTN_HOVER),
                        new EmptyBorder(10, 20, 10, 20)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(BTN_BG);
                button.setBorder(new CompoundBorder(
                        new RoundedBorder(10, BTN_BG),
                        new EmptyBorder(10, 20, 10, 20)
                ));
            }
        });
    }

    private void loadVehicleData() {
        vehicleTableModel.setRowCount(0);

        String sql = "SELECT vin, make, model, year, current_mileage FROM vehicles WHERE user_id = ?";
        try (DBconnection db = new DBconnection();
             Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String vin = rs.getString("vin");
                    String make = rs.getString("make");
                    String model = rs.getString("model");
                    int year = rs.getInt("year");
                    int mileage = rs.getInt("current_mileage");
                    vehicleTableModel.addRow(new Object[]{vin, make, model, year, mileage});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vehicle data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom border class for rounded corners with visible colored border
    class RoundedBorder implements Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}