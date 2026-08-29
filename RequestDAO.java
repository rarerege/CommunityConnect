package communityconnect;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public boolean createRequest(int resourceId, int customerId, int quantity, String urgency, String notes) {
        String sql = "INSERT INTO requests (resource_id, customer_id, quantity_requested, urgency, notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resourceId);
            stmt.setInt(2, customerId);
            stmt.setInt(3, quantity);
            stmt.setString(4, urgency);
            stmt.setString(5, notes);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating request: " + e.getMessage());
            return false;
        }
    }

    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT rq.*, rs.name as resource_name, u.username as customer_name " +
                "FROM requests rq " +
                "JOIN resources rs ON rq.resource_id = rs.id " +
                "JOIN users u ON rq.customer_id = u.id " +
                "ORDER BY rq.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Request request = new Request();
                request.setId(rs.getInt("id"));
                request.setResourceId(rs.getInt("resource_id"));
                request.setCustomerId(rs.getInt("customer_id"));
                request.setResourceName(rs.getString("resource_name"));
                request.setCustomerName(rs.getString("customer_name"));
                request.setQuantityRequested(rs.getInt("quantity_requested"));
                request.setUrgency(rs.getString("urgency"));
                request.setStatus(rs.getString("status"));
                request.setNotes(rs.getString("notes"));
                request.setAdminNotes(rs.getString("admin_notes"));
                request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting requests: " + e.getMessage());
        }
        return requests;
    }

    public List<Request> getRequestsByCustomer(int customerId) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT rq.*, rs.name as resource_name, u.username as customer_name " +
                "FROM requests rq " +
                "JOIN resources rs ON rq.resource_id = rs.id " +
                "JOIN users u ON rq.customer_id = u.id " +
                "WHERE rq.customer_id = ? ORDER BY rq.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Request request = new Request();
                request.setId(rs.getInt("id"));
                request.setResourceId(rs.getInt("resource_id"));
                request.setCustomerId(rs.getInt("customer_id"));
                request.setResourceName(rs.getString("resource_name"));
                request.setCustomerName(rs.getString("customer_name"));
                request.setQuantityRequested(rs.getInt("quantity_requested"));
                request.setUrgency(rs.getString("urgency"));
                request.setStatus(rs.getString("status"));
                request.setNotes(rs.getString("notes"));
                request.setAdminNotes(rs.getString("admin_notes"));
                request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer requests: " + e.getMessage());
        }
        return requests;
    }

    public List<Request> getRequestsBySupplier(int supplierId) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT rq.*, rs.name as resource_name, u.username as customer_name " +
                "FROM requests rq " +
                "JOIN resources rs ON rq.resource_id = rs.id " +
                "JOIN users u ON rq.customer_id = u.id " +
                "WHERE rs.supplier_id = ? ORDER BY rq.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Request request = new Request();
                request.setId(rs.getInt("id"));
                request.setResourceId(rs.getInt("resource_id"));
                request.setCustomerId(rs.getInt("customer_id"));
                request.setResourceName(rs.getString("resource_name"));
                request.setCustomerName(rs.getString("customer_name"));
                request.setQuantityRequested(rs.getInt("quantity_requested"));
                request.setUrgency(rs.getString("urgency"));
                request.setStatus(rs.getString("status"));
                request.setNotes(rs.getString("notes"));
                request.setAdminNotes(rs.getString("admin_notes"));
                request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting supplier requests: " + e.getMessage());
        }
        return requests;
    }

    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE requests SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRequestStatusWithNotes(int requestId, String status, String adminNotes) {
        String sql = "UPDATE requests SET status = ?, admin_notes = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, adminNotes);
            stmt.setInt(3, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating request status with notes: " + e.getMessage());
            return false;
        }
    }

    public int[] getDashboardStats() {
        int[] stats = new int[6]; // totalResources, availableResources, totalRequests, pendingRequests, approvedRequests, fulfilledRequests
        String sql1 = "SELECT COUNT(*) as total FROM resources";
        String sql2 = "SELECT COUNT(*) as available FROM resources WHERE is_available = TRUE AND quantity > 0";
        String sql3 = "SELECT COUNT(*) as total FROM requests";
        String sql4 = "SELECT COUNT(*) as pending FROM requests WHERE status = 'pending'";
        String sql5 = "SELECT COUNT(*) as approved FROM requests WHERE status = 'approved'";
        String sql6 = "SELECT COUNT(*) as fulfilled FROM requests WHERE status = 'fulfilled'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery(sql1);
            if (rs1.next()) stats[0] = rs1.getInt("total");

            ResultSet rs2 = stmt.executeQuery(sql2);
            if (rs2.next()) stats[1] = rs2.getInt("available");

            ResultSet rs3 = stmt.executeQuery(sql3);
            if (rs3.next()) stats[2] = rs3.getInt("total");

            ResultSet rs4 = stmt.executeQuery(sql4);
            if (rs4.next()) stats[3] = rs4.getInt("pending");

            ResultSet rs5 = stmt.executeQuery(sql5);
            if (rs5.next()) stats[4] = rs5.getInt("approved");

            ResultSet rs6 = stmt.executeQuery(sql6);
            if (rs6.next()) stats[5] = rs6.getInt("fulfilled");

        } catch (SQLException e) {
            System.err.println("Error getting dashboard stats: " + e.getMessage());
        }
        return stats;
    }

    public List<String> getRequestStatusOptions() {
        List<String> statuses = new ArrayList<>();
        statuses.add("pending");
        statuses.add("approved");
        statuses.add("rejected");
        statuses.add("fulfilled");
        statuses.add("cancelled");
        return statuses;
    }

    public List<String> getUrgencyOptions() {
        List<String> urgencies = new ArrayList<>();
        urgencies.add("low");
        urgencies.add("medium");
        urgencies.add("high");
        return urgencies;
    }
}
