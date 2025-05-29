package jdbc_recipe_db.databaseaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap; // Import HashMap
import java.util.List;
import java.util.Map;    // Import Map

public class CategoryDAO {

    public boolean createCategory(String name) {
        String sql = "INSERT INTO Category (name) VALUES (?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // This method is fine for the "Read All" button
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM Category ORDER BY id"; // Added ORDER BY
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categories.add(String.format("ID: %d | Name: %s", id, name)); // Current format
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // New method for JComboBox summaries (consistent " - " delimiter)
    public List<String> getCategoryIdNameSummaries() {
        List<String> categorySummaries = new ArrayList<>();
        String sql = "SELECT id, name FROM Category ORDER BY id";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categorySummaries.add(String.format("ID: %d - %s", id, name)); // Using " - "
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorySummaries;
    }

    // New method to get category details as a Map by ID
    public Map<String, String> getCategoryDetailsById(int categoryId) {
        Map<String, String> details = new HashMap<>();
        String sql = "SELECT name FROM Category WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    details.put("name", rs.getString("name"));
                    return details;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if category not found or error
    }

    public boolean updateCategory(int id, String name) {
        String sql = "UPDATE Category SET name=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM Category WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}