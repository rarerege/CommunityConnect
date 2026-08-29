package communityconnect;

import java.time.LocalDateTime;

public class Request {
    private int id;
    private int resourceId;
    private int customerId;
    private String resourceName;
    private String customerName;
    private int quantityRequested;
    private String urgency;
    private String status;
    private String notes;
    private String adminNotes; // Add this field
    private LocalDateTime createdAt;

    public Request() {}

    public Request(int id, int resourceId, int customerId, String resourceName,
                   String customerName, int quantityRequested, String urgency,
                   String status, String notes, String adminNotes, LocalDateTime createdAt) {
        this.id = id;
        this.resourceId = resourceId;
        this.customerId = customerId;
        this.resourceName = resourceName;
        this.customerName = customerName;
        this.quantityRequested = quantityRequested;
        this.urgency = urgency;
        this.status = status;
        this.notes = notes;
        this.adminNotes = adminNotes; // Initialize this
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getQuantityRequested() { return quantityRequested; }
    public void setQuantityRequested(int quantityRequested) { this.quantityRequested = quantityRequested; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getAdminNotes() { return adminNotes; } // ADD THIS GETTER
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; } // ADD THIS SETTER
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
