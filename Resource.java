package communityconnect;

import java.time.LocalDateTime;

public class Resource {
    private int id;
    private String name;
    private int categoryId;
    private String categoryName;
    private int quantity;
    private String description;
    private String location;
    private String contactInfo;
    private boolean isAvailable;
    private int supplierId;
    private String supplierName;
    private LocalDateTime createdAt;

    public Resource() {}

    public Resource(int id, String name, int categoryId, String categoryName, int quantity,
                    String description, String location, String contactInfo, boolean isAvailable,
                    int supplierId, String supplierName, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.description = description;
        this.location = location;
        this.contactInfo = contactInfo;
        this.isAvailable = isAvailable;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
