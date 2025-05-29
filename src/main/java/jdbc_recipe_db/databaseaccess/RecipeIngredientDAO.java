package jdbc_recipe_db.databaseaccess;

import java.sql.Connection; // Assuming DatabaseUtil.getConnection() returns this
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap; // For getRecipeIngredientDetailsById
import java.util.List;
import java.util.Map;    // For getRecipeIngredientDetailsById

public class RecipeIngredientDAO {
    // If you have a constructor that gets the connection, keep it.
    // Otherwise, ensure DatabaseUtil.getConnection() is used in each method.

    public boolean createRecipeIngredient(int recipeId, int ingredientId, String quantity) {
        String sql = "INSERT INTO RecipeIngredient (recipe_id, ingredient_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); // Get connection here or use a class member
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, ingredientId);
            stmt.setString(3, quantity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Method for the "Read All" text area
    public List<String> getRecipeIngredientsForDisplay() {
        List<String> recipeIngredients = new ArrayList<>();
        String sql = "SELECT ri.id, r.name AS RecipeName, i.name AS IngredientName, ri.quantity "
                   + "FROM RecipeIngredient ri "
                   + "JOIN Recipe r ON ri.recipe_id = r.id "
                   + "JOIN Ingredient i ON ri.ingredient_id = i.id "
                   + "ORDER BY ri.id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                recipeIngredients.add(String.format("AssocID: %d | Recipe: %s | Ingredient: %s | Quantity: %s",
                                                  rs.getInt("id"),
                                                  rs.getString("RecipeName"),
                                                  rs.getString("IngredientName"),
                                                  rs.getString("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipeIngredients;
    }

    // New method for the master JComboBox
    public List<String> getRecipeIngredientAssociationSummaries() {
        List<String> summaries = new ArrayList<>();
        String sql = "SELECT ri.id AS AssociationID, ri.recipe_id, r.name AS RecipeName, "
                   + "ri.ingredient_id, i.name AS IngredientName, ri.quantity "
                   + "FROM RecipeIngredient ri "
                   + "JOIN Recipe r ON ri.recipe_id = r.id "
                   + "JOIN Ingredient i ON ri.ingredient_id = i.id "
                   + "ORDER BY AssociationID";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Format: "AssocID:id --- Recipe:Name (RID:recipeId) --- Ing:Name (IID:ingId) --- Qty:qty"
                summaries.add(String.format("AssocID:%d --- Recipe:%s (RID:%d) --- Ing:%s (IID:%d) --- Qty:%s",
                                          rs.getInt("AssociationID"),
                                          rs.getString("RecipeName"),
                                          rs.getInt("recipe_id"),
                                          rs.getString("IngredientName"),
                                          rs.getInt("ingredient_id"),
                                          rs.getString("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }
    
    // Optional: Method to get details of a specific RecipeIngredient entry by its own ID
    public Map<String, String> getRecipeIngredientDetailsByAssociationId(int associationId) {
        Map<String, String> details = new HashMap<>();
        String sql = "SELECT recipe_id, ingredient_id, quantity FROM RecipeIngredient WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, associationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    details.put("recipe_id", String.valueOf(rs.getInt("recipe_id")));
                    details.put("ingredient_id", String.valueOf(rs.getInt("ingredient_id")));
                    details.put("quantity", rs.getString("quantity"));
                    return details;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean updateRecipeIngredient(int associationId, int newRecipeId, int newIngredientId, String newQuantity) {
        String sql = "UPDATE RecipeIngredient SET recipe_id=?, ingredient_id=?, quantity=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newRecipeId);
            stmt.setInt(2, newIngredientId);
            stmt.setString(3, newQuantity);
            stmt.setInt(4, associationId); // The ID of the RecipeIngredient row itself
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Check for potential unique constraint violations on (recipe_id, ingredient_id) if you have one
            // (excluding the current row being updated)
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRecipeIngredient(int associationId) {
        String sql = "DELETE FROM RecipeIngredient WHERE id=?"; // Deleting by the association's own ID
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, associationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}