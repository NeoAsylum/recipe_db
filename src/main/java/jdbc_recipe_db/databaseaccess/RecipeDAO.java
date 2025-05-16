package jdbc_recipe_db.databaseaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO {

    public boolean createRecipe(String name, String description, String instructions, int prepTime, int cookTime) {
        String sql = "INSERT INTO Recipe (name, description, instructions, prep_time, cook_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, instructions);
            stmt.setInt(4, prepTime);
            stmt.setInt(5, cookTime);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Return success status
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // ✅ Return failure status
        }
    }

    public List<String> getRecipes() {
        List<String> recipes = new ArrayList<>();
        String sql = "SELECT * FROM Recipe";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                // Retrieve all attributes from the Recipe table
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String instructions = rs.getString("instructions");
                int prepTime = rs.getInt("prep_time");
                int cookTime = rs.getInt("cook_time");
    
                // Format them into a string
                String recipeDetails = String.format(
                    "ID: %d | Name: %s | Description: %s | Instructions: %s | Prep Time: %d min | Cook Time: %d min",
                    id, name, description, instructions, prepTime, cookTime
                );
    
                // Add formatted string to the list
                recipes.add(recipeDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }
    

    public boolean updateRecipe(int id, String name, String description, String instructions, int prepTime, int cookTime) {
        String sql = "UPDATE Recipe SET name=?, description=?, instructions=?, prep_time=?, cook_time=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, instructions);
            stmt.setInt(4, prepTime);
            stmt.setInt(5, cookTime);
            stmt.setInt(6, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRecipe(int id) {
        String sql = "DELETE FROM Recipe WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // ✅ Only return true if something was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
