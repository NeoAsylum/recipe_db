package jdbc_recipe_db.databaseaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {

    public boolean createIngredient(String name, int calories, float protein, float fat, float carbohydrates, float fiber) {
        String sql = "INSERT INTO Ingredient (name, calories, protein, fat, carbohydrates, fiber) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, calories);
            stmt.setFloat(3, protein);
            stmt.setFloat(4, fat);
            stmt.setFloat(5, carbohydrates);
            stmt.setFloat(6, fiber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Success check
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // ❌ Error handling
        }
    }

    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int calories = rs.getInt("calories");
                float protein = rs.getFloat("protein");
                float fat = rs.getFloat("fat");
                float carbohydrates = rs.getFloat("carbohydrates");
                float fiber = rs.getFloat("fiber");

                ingredients.add(String.format("ID: %d | Name: %s | Calories: %d | Protein: %.2fg | Fat: %.2fg | Carbs: %.2fg | Fiber: %.2fg",
                        id, name, calories, protein, fat, carbohydrates, fiber));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public boolean updateIngredient(int id, String name, int calories, float protein, float fat, float carbohydrates, float fiber) {
        String sql = "UPDATE Ingredient SET name=?, calories=?, protein=?, fat=?, carbohydrates=?, fiber=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, calories);
            stmt.setFloat(3, protein);
            stmt.setFloat(4, fat);
            stmt.setFloat(5, carbohydrates);
            stmt.setFloat(6, fiber);
            stmt.setInt(7, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Success check
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteIngredient(int id) {
        String sql = "DELETE FROM Ingredient WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Success check
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
