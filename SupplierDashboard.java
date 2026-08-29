package communityconnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SupplierDashboard extends JFrame {
    private User currentUser;
    private ResourceDAO resourceDAO;
    private RequestDAO requestDAO;

    private JTabbedPane tabbedPane;
    private JTable myResourcesTable, requestsTable;
    private DefaultTableModel myResourcesTableModel, requestsTableModel;

    public SupplierDashboard(User user) {
        this.currentUser = user;
        this.resourceDAO = new ResourceDAO();
        this.requestDAO = new RequestDAO();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("CommunityConnect - Supplier Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 175, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Supplier Dashboard - Welcome, " + currentUser.getUsername());
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
        tabbedPane.addTab("My Resources", createMyResourcesPanel());
        tabbedPane.addTab("Resource Requests", createRequestsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createMyResourcesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);

        JButton addResourceButton = new JButton("Add Resource");
        JButton editResourceButton = new JButton("Edit Resource");
        JButton deleteResourceButton = new JButton("Delete Resource");
        JButton refreshButton = new JButton("Refresh");

        toolbar.add(addResourceButton);
        toolbar.add(editResourceButton);
        toolbar.add(deleteResourceButton);
        toolbar.add(refreshButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Resources table
        String[] columns = {"ID", "Name", "Category", "Quantity", "Location", "Available", "Description"};
        myResourcesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myResourcesTable = new JTable(myResourcesTableModel);
        myResourcesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(myResourcesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addResourceButton.addActionListener(e -> showAddResourceDialog());
        editResourceButton.addActionListener(e -> editSelectedResource());
        deleteResourceButton.addActionListener(e -> deleteSelectedResource());
        refreshButton.addActionListener(e -> loadMyResources());

        return panel;
    }

    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JButton fulfillButton = new JButton("Mark Fulfilled");

        toolbar.add(refreshButton);
        toolbar.add(approveButton);
        toolbar.add(rejectButton);
        toolbar.add(fulfillButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Requests table
        String[] columns = {"ID", "Resource", "Customer", "Quantity", "Status", "Urgency", "Notes", "Date"};
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
        approveButton.addActionListener(e -> updateRequestStatus("approved"));
        rejectButton.addActionListener(e -> updateRequestStatus("rejected"));
        fulfillButton.addActionListener(e -> updateRequestStatus("fulfilled"));

        return panel;
    }

    private void loadData() {
        loadMyResources();
        loadRequests();
    }

    private void loadMyResources() {
        myResourcesTableModel.setRowCount(0);
        List<Resource> resources = resourceDAO.getResourcesBySupplier(currentUser.getId());
        for (Resource resource : resources) {
            myResourcesTableModel.addRow(new Object[]{
                    resource.getId(),
                    resource.getName(),
                    resource.getCategoryName(),
                    resource.getQuantity(),
                    resource.getLocation(),
                    resource.isAvailable() ? "Yes" : "No",
                    resource.getDescription()
            });
        }
    }

    private void loadRequests() {
        requestsTableModel.setRowCount(0);
        List<Request> requests = requestDAO.getRequestsBySupplier(currentUser.getId());
        for (Request request : requests) {
            requestsTableModel.addRow(new Object[]{
                    request.getId(),
                    request.getResourceName(),
                    request.getCustomerName(),
                    request.getQuantityRequested(),
                    request.getStatus(),
                    request.getUrgency(),
                    request.getNotes(),
                    request.getCreatedAt().toString().substring(0, 16)
            });
        }
    }

    private void showAddResourceDialog() {
        JTextField nameField = new JTextField();

        // Fixed JComboBox
        List<String> categoriesList = resourceDAO.getAllCategories();
        String[] categoriesArray = categoriesList.toArray(new String[0]);
        JComboBox<String> categoryCombo = new JComboBox<>(categoriesArray);

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JCheckBox availableCheckbox = new JCheckBox("Available", true);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.add(new JLabel("Resource Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantitySpinner);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        panel.add(new JLabel("Availability:"));
        panel.add(availableCheckbox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Resource",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            String location = locationField.getText().trim();
            String contactInfo = contactField.getText().trim();
            String description = descriptionArea.getText().trim();
            boolean isAvailable = availableCheckbox.isSelected();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a resource name");
                return;
            }

            int categoryId = resourceDAO.getCategoryIdByName(category);
            if (categoryId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid category selected");
                return;
            }

            Resource resource = new Resource();
            resource.setName(name);
            resource.setCategoryId(categoryId);
            resource.setQuantity(quantity);
            resource.setLocation(location);
            resource.setContactInfo(contactInfo);
            resource.setDescription(description);
            resource.setAvailable(isAvailable);
            resource.setSupplierId(currentUser.getId());

            if (resourceDAO.addResource(resource)) {
                JOptionPane.showMessageDialog(this, "Resource added successfully");
                loadMyResources();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add resource");
            }
        }
    }

    private void editSelectedResource() {
        int selectedRow = myResourcesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resource to edit");
            return;
        }

        int resourceId = (int) myResourcesTableModel.getValueAt(selectedRow, 0);
        Resource resource = resourceDAO.getResourceById(resourceId);

        if (resource == null) {
            JOptionPane.showMessageDialog(this, "Resource not found");
            return;
        }

        JTextField nameField = new JTextField(resource.getName());

        // Fixed JComboBox
        List<String> categoriesList = resourceDAO.getAllCategories();
        String[] categoriesArray = categoriesList.toArray(new String[0]);
        JComboBox<String> categoryCombo = new JComboBox<>(categoriesArray);
        categoryCombo.setSelectedItem(resource.getCategoryName());

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(resource.getQuantity(), 0, 10000, 1));
        JTextField locationField = new JTextField(resource.getLocation());
        JTextField contactField = new JTextField(resource.getContactInfo());
        JTextArea descriptionArea = new JTextArea(resource.getDescription(), 3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JCheckBox availableCheckbox = new JCheckBox("Available", resource.isAvailable());

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.add(new JLabel("Resource Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantitySpinner);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        panel.add(new JLabel("Availability:"));
        panel.add(availableCheckbox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Resource",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            String location = locationField.getText().trim();
            String contactInfo = contactField.getText().trim();
            String description = descriptionArea.getText().trim();
            boolean isAvailable = availableCheckbox.isSelected();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a resource name");
                return;
            }

            int categoryId = resourceDAO.getCategoryIdByName(category);
            if (categoryId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid category selected");
                return;
            }

            resource.setName(name);
            resource.setCategoryId(categoryId);
            resource.setQuantity(quantity);
            resource.setLocation(location);
            resource.setContactInfo(contactInfo);
            resource.setDescription(description);
            resource.setAvailable(isAvailable);

            if (resourceDAO.updateResource(resource)) {
                JOptionPane.showMessageDialog(this, "Resource updated successfully");
                loadMyResources();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update resource");
            }
        }
    }

    private void deleteSelectedResource() {
        int selectedRow = myResourcesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resource to delete");
            return;
        }

        int resourceId = (int) myResourcesTableModel.getValueAt(selectedRow, 0);
        String resourceName = (String) myResourcesTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete resource: " + resourceName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (resourceDAO.deleteResource(resourceId)) {
                JOptionPane.showMessageDialog(this, "Resource deleted successfully");
                loadMyResources();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete resource");
            }
        }
    }

    private void updateRequestStatus(String newStatus) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request");
            return;
        }

        int requestId = (int) requestsTableModel.getValueAt(selectedRow, 0);
        String resourceName = (String) requestsTableModel.getValueAt(selectedRow, 1);
        int quantityRequested = (int) requestsTableModel.getValueAt(selectedRow, 3);
        String currentStatus = (String) requestsTableModel.getValueAt(selectedRow, 4);

        if (currentStatus.equals(newStatus)) {
            JOptionPane.showMessageDialog(this, "Request is already in status: " + newStatus);
            return;
        }

        // For fulfilled status, check if we have enough quantity
        if (newStatus.equals("fulfilled")) {
            int resourceId = getResourceIdFromRequest(requestId);
            Resource resource = resourceDAO.getResourceById(resourceId);
            if (resource != null && resource.getQuantity() < quantityRequested) {
                JOptionPane.showMessageDialog(this,
                        "Insufficient quantity available. Available: " + resource.getQuantity(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String adminNotes = "";
        if (newStatus.equals("rejected")) {
            adminNotes = JOptionPane.showInputDialog(this,
                    "Please provide reason for rejection:", "Rejection Reason");
            if (adminNotes == null) return; // User cancelled
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to mark this request as '" + newStatus + "'?",
                "Confirm Status Update", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success;
            if (adminNotes.isEmpty()) {
                success = requestDAO.updateRequestStatus(requestId, newStatus);
            } else {
                success = requestDAO.updateRequestStatusWithNotes(requestId, newStatus, adminNotes);
            }

            if (success) {
                // If fulfilled, update resource quantity
                if (newStatus.equals("fulfilled")) {
                    int resourceId = getResourceIdFromRequest(requestId);
                    Resource resource = resourceDAO.getResourceById(resourceId);
                    if (resource != null) {
                        int newQuantity = resource.getQuantity() - quantityRequested;
                        resourceDAO.updateResourceQuantity(resourceId, newQuantity);
                    }
                }

                JOptionPane.showMessageDialog(this, "Request status updated to: " + newStatus);
                loadRequests();
                loadMyResources(); // Refresh resources to show updated quantities
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request status");
            }
        }
    }

    private int getResourceIdFromRequest(int requestId) {
        List<Request> requests = requestDAO.getRequestsBySupplier(currentUser.getId());
        for (Request request : requests) {
            if (request.getId() == requestId) {
                return request.getResourceId();
            }
        }
        return -1;
    }
}