package communityconnect;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public List<Resource> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT r.*, c.name as category_name, u.username as supplier_name " +
                "FROM resources r " +
                "LEFT JOIN categories c ON r.category_id = c.id " +
                "LEFT JOIN users u ON r.supplier_id = u.id " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting resources: " + e.getMessage());
        }
        return resources;
    }

    public List<Resource> getResourcesBySupplier(int supplierId) {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT r.*, c.name as category_name, u.username as supplier_name " +
                "FROM resources r " +
                "LEFT JOIN categories c ON r.category_id = c.id " +
                "LEFT JOIN users u ON r.supplier_id = u.id " +
                "WHERE r.supplier_id = ? ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting supplier resources: " + e.getMessage());
        }
        return resources;
    }

    public List<Resource> searchResources(String searchTerm, String categoryFilter, boolean availableOnly) {
        List<Resource> resources = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, c.name as category_name, u.username as supplier_name " +
                        "FROM resources r " +
                        "LEFT JOIN categories c ON r.category_id = c.id " +
                        "LEFT JOIN users u ON r.supplier_id = u.id WHERE 1=1"
        );

        List<Object> parameters = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND (r.name LIKE ? OR r.description LIKE ?)");
            parameters.add("%" + searchTerm + "%");
            parameters.add("%" + searchTerm + "%");
        }
        if (categoryFilter != null && !categoryFilter.equals("All")) {
            sql.append(" AND c.name = ?");
            parameters.add(categoryFilter);
        }
        if (availableOnly) {
            sql.append(" AND r.is_available = TRUE AND r.quantity > 0");
        }
        sql.append(" ORDER BY r.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching resources: " + e.getMessage());
        }
        return resources;
    }

    // Overloaded method for backward compatibility
    public List<Resource> searchResources(String searchTerm, String categoryFilter) {
        return searchResources(searchTerm, categoryFilter, false);
    }

    public boolean addResource(Resource resource) {
        String sql = "INSERT INTO resources (name, category_id, quantity, description, location, contact_info, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resource.getName());
            stmt.setInt(2, resource.getCategoryId());
            stmt.setInt(3, resource.getQuantity());
            stmt.setString(4, resource.getDescription());
            stmt.setString(5, resource.getLocation());
            stmt.setString(6, resource.getContactInfo());
            stmt.setInt(7, resource.getSupplierId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding resource: " + e.getMessage());
            return false;
        }
    }

    public boolean updateResource(Resource resource) {
        String sql = "UPDATE resources SET name = ?, category_id = ?, quantity = ?, description = ?, location = ?, contact_info = ?, is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resource.getName());
            stmt.setInt(2, resource.getCategoryId());
            stmt.setInt(3, resource.getQuantity());
            stmt.setString(4, resource.getDescription());
            stmt.setString(5, resource.getLocation());
            stmt.setString(6, resource.getContactInfo());
            stmt.setBoolean(7, resource.isAvailable());
            stmt.setInt(8, resource.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating resource: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteResource(int resourceId) {
        String sql = "DELETE FROM resources WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resourceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting resource: " + e.getMessage());
            return false;
        }
    }

    public boolean updateResourceQuantity(int resourceId, int newQuantity) {
        String sql = "UPDATE resources SET quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, resourceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating resource quantity: " + e.getMessage());
            return false;
        }
    }

    public Resource getResourceById(int resourceId) {
        String sql = "SELECT r.*, c.name as category_name, u.username as supplier_name " +
                "FROM resources r " +
                "LEFT JOIN categories c ON r.category_id = c.id " +
                "LEFT JOIN users u ON r.supplier_id = u.id " +
                "WHERE r.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resourceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractResourceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting resource by ID: " + e.getMessage());
        }
        return null;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
        return categories;
    }

    public int getCategoryIdByName(String categoryName) {
        String sql = "SELECT id FROM categories WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting category ID: " + e.getMessage());
        }
        return 1; // Default to first category
    }

    private Resource extractResourceFromResultSet(ResultSet rs) throws SQLException {
        return new Resource(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("category_id"),
                rs.getString("category_name"),
                rs.getInt("quantity"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getString("contact_info"),
                rs.getBoolean("is_available"),
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
