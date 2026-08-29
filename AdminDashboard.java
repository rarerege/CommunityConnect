package communityconnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private ResourceDAO resourceDAO;
    private RequestDAO requestDAO;

    private JTabbedPane tabbedPane;
    private JTable usersTable, resourcesTable, requestsTable;
    private DefaultTableModel usersTableModel, resourcesTableModel, requestsTableModel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.resourceDAO = new ResourceDAO();
        this.requestDAO = new RequestDAO();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("CommunityConnect - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard - Welcome, " + currentUser.getUsername());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Users Management", createUsersPanel());
        tabbedPane.addTab("Resources Management", createResourcesPanel());
        tabbedPane.addTab("Requests Management", createRequestsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        int[] stats = requestDAO.getDashboardStats();

        statsPanel.add(createStatCard("Total Resources", String.valueOf(stats[0]), new Color(76, 175, 80)));
        statsPanel.add(createStatCard("Available Resources", String.valueOf(stats[1]), new Color(56, 142, 60)));
        statsPanel.add(createStatCard("Total Requests", String.valueOf(stats[2]), new Color(33, 150, 243)));
        statsPanel.add(createStatCard("Pending Requests", String.valueOf(stats[3]), new Color(255, 152, 0)));
        statsPanel.add(createStatCard("Approved Requests", String.valueOf(stats[4]), new Color(156, 39, 176)));
        statsPanel.add(createStatCard("Fulfilled Requests", String.valueOf(stats[5]), new Color(0, 150, 136)));

        panel.add(statsPanel, BorderLayout.NORTH);

        // Recent activity
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Requests"));
        activityPanel.setBackground(Color.WHITE);

        String[] requestColumns = {"ID", "Resource", "Customer", "Quantity", "Status", "Urgency", "Date"};
        requestsTableModel = new DefaultTableModel(requestColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestsTable = new JTable(requestsTableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(activityPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(150, 100));

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);

        JButton addUserButton = new JButton("Add User");
        JButton refreshButton = new JButton("Refresh");
        JButton editUserButton = new JButton("Edit User");
        JButton deleteUserButton = new JButton("Delete User");

        toolbar.add(addUserButton);
        toolbar.add(refreshButton);
        toolbar.add(editUserButton);
        toolbar.add(deleteUserButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Users table
        String[] columns = {"ID", "Username", "Full Name", "Email", "Phone", "Role", "Active", "Created"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addUserButton.addActionListener(e -> showAddUserDialog());
        refreshButton.addActionListener(e -> loadUsers());
        editUserButton.addActionListener(e -> editSelectedUser());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());

        return panel;
    }

    private JPanel createResourcesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton deleteResourceButton = new JButton("Delete Resource");

        toolbar.add(refreshButton);
        toolbar.add(deleteResourceButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Resources table
        String[] columns = {"ID", "Name", "Category", "Quantity", "Supplier", "Location", "Available"};
        resourcesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resourcesTable = new JTable(resourcesTableModel);
        resourcesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(resourcesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        refreshButton.addActionListener(e -> loadResources());
        deleteResourceButton.addActionListener(e -> deleteSelectedResource());

        return panel;
    }

    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton updateStatusButton = new JButton("Update Status");

        toolbar.add(refreshButton);
        toolbar.add(updateStatusButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Requests table
        String[] columns = {"ID", "Resource", "Customer", "Quantity", "Status", "Urgency", "Date"};
        requestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestsTable = new JTable(requestsTableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        refreshButton.addActionListener(e -> loadRequests());
        updateStatusButton.addActionListener(e -> updateRequestStatus());

        return panel;
    }

    private void loadData() {
        loadUsers();
        loadResources();
        loadRequests();
    }

    private void loadUsers() {
        usersTableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            usersTableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.isActive() ? "Yes" : "No",
                    user.getCreatedAt().toString().substring(0, 16)
            });
        }
    }

    private void loadResources() {
        resourcesTableModel.setRowCount(0);
        List<Resource> resources = resourceDAO.getAllResources();
        for (Resource resource : resources) {
            resourcesTableModel.addRow(new Object[]{
                    resource.getId(),
                    resource.getName(),
                    resource.getCategoryName(),
                    resource.getQuantity(),
                    resource.getSupplierName(),
                    resource.getLocation(),
                    resource.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private void loadRequests() {
        requestsTableModel.setRowCount(0);
        List<Request> requests = requestDAO.getAllRequests();
        for (Request request : requests) {
            requestsTableModel.addRow(new Object[]{
                    request.getId(),
                    request.getResourceName(),
                    request.getCustomerName(),
                    request.getQuantityRequested(),
                    request.getStatus(),
                    request.getUrgency(),
                    request.getCreatedAt().toString().substring(0, 16)
            });
        }
    }

    private void showAddUserDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();

        // Fixed JComboBox
        String[] roles = {"admin", "supplier", "customer"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required");
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setRole(role);

            if (userDAO.createUser(newUser)) {
                JOptionPane.showMessageDialog(this, "User created successfully");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user - username may already exist");
            }
        }
    }

    private void editSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }

        int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
        User user = userDAO.getUserById(userId);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found");
            return;
        }

        JTextField emailField = new JTextField(user.getEmail());
        JTextField fullNameField = new JTextField(user.getFullName());
        JTextField phoneField = new JTextField(user.getPhone());

        // Fixed JComboBox
        String[] roles = {"admin", "supplier", "customer"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setSelectedItem(user.getRole());

        JCheckBox activeCheckbox = new JCheckBox("Active", user.isActive());

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Status:"));
        panel.add(activeCheckbox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            user.setEmail(emailField.getText().trim());
            user.setFullName(fullNameField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            user.setRole((String) roleCombo.getSelectedItem());
            user.setActive(activeCheckbox.isSelected());

            if (userDAO.updateUser(user)) {
                JOptionPane.showMessageDialog(this, "User updated successfully");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user");
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }

        int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
        String username = (String) usersTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user");
            }
        }
    }

    private void deleteSelectedResource() {
        int selectedRow = resourcesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resource to delete");
            return;
        }

        int resourceId = (int) resourcesTableModel.getValueAt(selectedRow, 0);
        String resourceName = (String) resourcesTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete resource: " + resourceName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (resourceDAO.deleteResource(resourceId)) {
                JOptionPane.showMessageDialog(this, "Resource deleted successfully");
                loadResources();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete resource");
            }
        }
    }

    private void updateRequestStatus() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request");
            return;
        }

        int requestId = (int) requestsTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) requestsTableModel.getValueAt(selectedRow, 4);

        List<String> statusOptions = requestDAO.getRequestStatusOptions();
        String[] statusArray = statusOptions.toArray(new String[0]);

        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:", "Update Request Status",
                JOptionPane.QUESTION_MESSAGE, null, statusArray, currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            if (requestDAO.updateRequestStatus(requestId, newStatus)) {
                JOptionPane.showMessageDialog(this, "Request status updated to: " + newStatus);
                loadRequests();
                // Refresh dashboard if on dashboard tab
                if (tabbedPane.getSelectedIndex() == 0) {
                    loadRequests(); // Reload recent requests
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request status");
            }
        }
    }
}
