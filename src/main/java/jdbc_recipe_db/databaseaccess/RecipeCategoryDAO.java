package jdbc_recipe_db.databaseaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryDAO {

    private final Connection conn;

    public RecipeCategoryDAO() throws SQLException {
        this.conn = DatabaseUtil.getConnection();
    }

    // Create Recipe-Category Relationship
    public boolean createRecipeCategory(int recipeId, int categoryId) {
        String sql = "INSERT INTO RecipeCategory (recipe_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read Recipe-Category Relationships (Returns Recipe ID, Name, and Category Name)
    public List<String> getRecipeCategories() {
        List<String> recipeCategories = new ArrayList<>();
        String sql = "SELECT rc.recipe_id, r.name AS RecipeName, c.name AS CategoryName "
                + "FROM RecipeCategory rc "
                + "JOIN Recipe r ON rc.recipe_id = r.id "
                + "JOIN Category c ON rc.category_id = c.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int recipeId = rs.getInt("recipe_id");
                String recipeName = rs.getString("RecipeName");
                String categoryName = rs.getString("CategoryName");

                recipeCategories.add(String.format("Recipe ID: %d | Recipe: %s | Category: %s", recipeId, recipeName, categoryName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipeCategories;
    }

    // Update Recipe-Category Relationship (Change Category for a Given Recipe)
    public boolean updateRecipeCategory(int recipeId, int newCategoryId) {
        String sql = "UPDATE RecipeCategory SET category_id=? WHERE recipe_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newCategoryId);
            stmt.setInt(2, recipeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete Recipe-Category Relationship (Remove Specific Recipe-Category Pairing)
    public boolean deleteRecipeCategory(int recipeId, int categoryId) {
        String sql = "DELETE FROM RecipeCategory WHERE recipe_id=? AND category_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
