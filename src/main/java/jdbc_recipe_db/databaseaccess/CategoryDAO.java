package jdbc_recipe_db.databaseaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public boolean createCategory(String name) {
        String sql = "INSERT INTO Category (name) VALUES (?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Erfolg prüfen
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // ❌ Fehler behandeln
        }
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categories.add(String.format("ID: %d | Name: %s", id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public boolean updateCategory(int id, String name) {
        String sql = "UPDATE Category SET name=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Erfolg prüfen
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
            return rowsAffected > 0; // ✅ Erfolg prüfen
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
