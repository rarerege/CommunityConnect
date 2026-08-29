package communityconnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private User currentUser;
    private ResourceDAO resourceDAO;
    private RequestDAO requestDAO;

    private JTabbedPane tabbedPane;
    private JTable resourcesTable, myRequestsTable;
    private DefaultTableModel resourcesTableModel, myRequestsTableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilterCombo;
    private JCheckBox availableOnlyCheckbox;

    public CustomerDashboard(User user) {
        this.currentUser = user;
        this.resourceDAO = new ResourceDAO();
        this.requestDAO = new RequestDAO();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("CommunityConnect - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(156, 39, 176));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Customer Dashboard - Welcome, " + currentUser.getUsername());
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

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Browse Resources", createBrowsePanel());
        tabbedPane.addTab("My Requests", createMyRequestsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("Category:"));
        List<String> categories = resourceDAO.getAllCategories();
        categories.add(0, "All");

        // Fixed JComboBox
        String[] categoriesArray = categories.toArray(new String[0]);
        typeFilterCombo = new JComboBox<>(categoriesArray);
        searchPanel.add(typeFilterCombo);

        availableOnlyCheckbox = new JCheckBox("Available Only", true);
        searchPanel.add(availableOnlyCheckbox);

        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton requestButton = new JButton("Request Resource");

        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(requestButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Resources table
        String[] columns = {"ID", "Name", "Category", "Available", "Supplier", "Location", "Contact", "Description"};
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
        searchButton.addActionListener(e -> searchResources());
        clearButton.addActionListener(e -> clearSearch());
        requestButton.addActionListener(e -> requestSelectedResource());

        // Load initial data
        loadAvailableResources();

        return panel;
    }

    private JPanel createMyRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton cancelButton = new JButton("Cancel Request");
        JButton viewDetailsButton = new JButton("View Details");

        toolbar.add(refreshButton);
        toolbar.add(cancelButton);
        toolbar.add(viewDetailsButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Requests table
        String[] columns = {"ID", "Resource", "Quantity", "Status", "Urgency", "Date", "Admin Notes"};
        myRequestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myRequestsTable = new JTable(myRequestsTableModel);
        myRequestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(myRequestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        refreshButton.addActionListener(e -> loadMyRequests());
        cancelButton.addActionListener(e -> cancelSelectedRequest());
        viewDetailsButton.addActionListener(e -> viewRequestDetails());

        return panel;
    }

    private void loadData() {
        loadAvailableResources();
        loadMyRequests();
    }

    private void loadAvailableResources() {
        resourcesTableModel.setRowCount(0);
        List<Resource> resources = resourceDAO.searchResources("", "All", true);
        for (Resource resource : resources) {
            resourcesTableModel.addRow(new Object[]{
                    resource.getId(),
                    resource.getName(),
                    resource.getCategoryName(),
                    resource.getQuantity(),
                    resource.getSupplierName(),
                    resource.getLocation(),
                    resource.getContactInfo(),
                    resource.getDescription()
            });
        }
    }

    private void loadMyRequests() {
        myRequestsTableModel.setRowCount(0);
        List<Request> requests = requestDAO.getRequestsByCustomer(currentUser.getId());
        for (Request request : requests) {
            myRequestsTableModel.addRow(new Object[]{
                    request.getId(),
                    request.getResourceName(),
                    request.getQuantityRequested(),
                    request.getStatus(),
                    request.getUrgency(),
                    request.getCreatedAt().toString().substring(0, 16),
                    request.getAdminNotes()
            });
        }
    }

    private void searchResources() {
        String searchTerm = searchField.getText().trim();
        String categoryFilter = (String) typeFilterCombo.getSelectedItem();
        boolean availableOnly = availableOnlyCheckbox.isSelected();

        resourcesTableModel.setRowCount(0);
        List<Resource> resources = resourceDAO.searchResources(searchTerm, categoryFilter, availableOnly);
        for (Resource resource : resources) {
            resourcesTableModel.addRow(new Object[]{
                    resource.getId(),
                    resource.getName(),
                    resource.getCategoryName(),
                    resource.getQuantity(),
                    resource.getSupplierName(),
                    resource.getLocation(),
                    resource.getContactInfo(),
                    resource.getDescription()
            });
        }

        JOptionPane.showMessageDialog(this,
                "Found " + resources.size() + " resources matching your criteria",
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearSearch() {
        searchField.setText("");
        typeFilterCombo.setSelectedItem("All");
        availableOnlyCheckbox.setSelected(true);
        loadAvailableResources();
    }

    private void requestSelectedResource() {
        int selectedRow = resourcesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resource to request");
            return;
        }

        int resourceId = (int) resourcesTableModel.getValueAt(selectedRow, 0);
        String resourceName = (String) resourcesTableModel.getValueAt(selectedRow, 1);
        int availableQuantity = (int) resourcesTableModel.getValueAt(selectedRow, 3);

        if (availableQuantity <= 0) {
            JOptionPane.showMessageDialog(this,
                    "This resource is currently unavailable",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Request details panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JLabel resourceLabel = new JLabel("Resource:");
        JLabel resourceValue = new JLabel(resourceName);
        resourceValue.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel availableLabel = new JLabel("Available Quantity:");
        JLabel availableValue = new JLabel(String.valueOf(availableQuantity));

        JLabel quantityLabel = new JLabel("Request Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, availableQuantity, 1));

        JLabel urgencyLabel = new JLabel("Urgency:");

        // Fixed JComboBox for urgency
        List<String> urgencyOptions = requestDAO.getUrgencyOptions();
        String[] urgencyArray = urgencyOptions.toArray(new String[0]);
        JComboBox<String> urgencyCombo = new JComboBox<>(urgencyArray);

        JLabel notesLabel = new JLabel("Notes (Optional):");
        JTextField notesField = new JTextField();

        panel.add(resourceLabel);
        panel.add(resourceValue);
        panel.add(availableLabel);
        panel.add(availableValue);
        panel.add(quantityLabel);
        panel.add(quantitySpinner);
        panel.add(urgencyLabel);
        panel.add(urgencyCombo);
        panel.add(notesLabel);
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Request Resource",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int quantity = (Integer) quantitySpinner.getValue();
            String urgency = (String) urgencyCombo.getSelectedItem();
            String notes = notesField.getText().trim();

            if (quantity > availableQuantity) {
                JOptionPane.showMessageDialog(this,
                        "Requested quantity exceeds available quantity. Available: " + availableQuantity,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (requestDAO.createRequest(resourceId, currentUser.getId(), quantity, urgency, notes)) {
                JOptionPane.showMessageDialog(this,
                        "Resource request submitted successfully!\nYou can track the status in 'My Requests' tab.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMyRequests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit resource request");
            }
        }
    }

    private void cancelSelectedRequest() {
        int selectedRow = myRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to cancel");
            return;
        }

        int requestId = (int) myRequestsTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) myRequestsTableModel.getValueAt(selectedRow, 3);

        if (!currentStatus.equals("pending")) {
            JOptionPane.showMessageDialog(this,
                    "Only requests with 'pending' status can be cancelled. Current status: " + currentStatus,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this request?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (requestDAO.updateRequestStatus(requestId, "cancelled")) {
                JOptionPane.showMessageDialog(this, "Request cancelled successfully");
                loadMyRequests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel request");
            }
        }
    }

    private void viewRequestDetails() {
        int selectedRow = myRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to view details");
            return;
        }

        int requestId = (int) myRequestsTableModel.getValueAt(selectedRow, 0);
        String resourceName = (String) myRequestsTableModel.getValueAt(selectedRow, 1);
        int quantity = (int) myRequestsTableModel.getValueAt(selectedRow, 2);
        String status = (String) myRequestsTableModel.getValueAt(selectedRow, 3);
        String urgency = (String) myRequestsTableModel.getValueAt(selectedRow, 4);
        String date = (String) myRequestsTableModel.getValueAt(selectedRow, 5);
        String adminNotes = (String) myRequestsTableModel.getValueAt(selectedRow, 6);

        String message = String.format(
                "Request Details:\n\n" +
                        "Resource: %s\n" +
                        "Quantity: %d\n" +
                        "Status: %s\n" +
                        "Urgency: %s\n" +
                        "Date: %s\n" +
                        "Admin Notes: %s",
                resourceName, quantity, status, urgency, date,
                adminNotes != null ? adminNotes : "None"
        );

        JOptionPane.showMessageDialog(this, message, "Request Details", JOptionPane.INFORMATION_MESSAGE);
    }
}