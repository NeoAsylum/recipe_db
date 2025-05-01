package jdbc_recipe_db.databaseaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientDAO {

    public boolean createRecipeIngredient(int recipeId, int ingredientId, String quantity) {
        String sql = "INSERT INTO RecipeIngredient (recipe_id, ingredient_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, ingredientId);
            stmt.setString(3, quantity);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Success check
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // ❌ Error handling
        }
    }

    public List<String> getRecipeIngredients() {
        List<String> recipeIngredients = new ArrayList<>();
        String sql = "SELECT * FROM RecipeIngredient";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int recipeId = rs.getInt("recipe_id");
                int ingredientId = rs.getInt("ingredient_id");
                String quantity = rs.getString("quantity");

                recipeIngredients.add(String.format("ID: %d | Recipe ID: %d | Ingredient ID: %d | Quantity: %s",
                        id, recipeId, ingredientId, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipeIngredients;
    }

    public boolean updateRecipeIngredient(int id, int recipeId, int ingredientId, String quantity) {
        String sql = "UPDATE RecipeIngredient SET recipe_id=?, ingredient_id=?, quantity=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, ingredientId);
            stmt.setString(3, quantity);
            stmt.setInt(4, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Success check
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRecipeIngredient(int id) {
        String sql = "DELETE FROM RecipeIngredient WHERE id=?";
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
