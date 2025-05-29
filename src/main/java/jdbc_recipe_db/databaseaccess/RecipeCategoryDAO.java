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

    public boolean createRecipeCategory(int recipeId, int categoryId) {
        String sql = "INSERT INTO RecipeCategory (recipe_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Check for duplicate entry if your DB throws a specific error code for it
            if (e.getSQLState().equals("23505")) { // Example for PostgreSQL unique violation
                System.err.println("Attempted to insert duplicate RecipeCategory: RecipeID=" + recipeId + ", CategoryID=" + categoryId);
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Renamed for clarity, populates the master JComboBox
    public List<String> getRecipeCategoryAssociationSummaries() {
        List<String> summaries = new ArrayList<>();
        // Ensure Recipe and Category tables have 'name' columns
        String sql = "SELECT rc.recipe_id, r.name AS RecipeName, rc.category_id, c.name AS CategoryName "
                   + "FROM RecipeCategory rc "
                   + "JOIN Recipe r ON rc.recipe_id = r.id "
                   + "JOIN Category c ON rc.category_id = c.id "
                   + "ORDER BY r.name, c.name"; // Order for consistent display

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int recipeId = rs.getInt("recipe_id");
                String recipeName = rs.getString("RecipeName");
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("CategoryName");
                // Format: "RID:recipe_id (Recipe Name) --- CID:category_id (Category Name)"
                // This format is chosen to be distinct and parseable.
                summaries.add(String.format("RID:%d (%s) --- CID:%d (%s)",
                                          recipeId, recipeName, categoryId, categoryName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }
    
    // Original method for the "Read All" text area display (can keep or adapt)
    public List<String> getFullRecipeCategoriesDisplay() {
        List<String> recipeCategories = new ArrayList<>();
        String sql = "SELECT rc.recipe_id, r.name AS RecipeName, rc.category_id, c.name AS CategoryName "
                   + "FROM RecipeCategory rc "
                   + "JOIN Recipe r ON rc.recipe_id = r.id "
                   + "JOIN Category c ON rc.category_id = c.id "
                   + "ORDER BY r.name, c.name";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int recipeId = rs.getInt("recipe_id");
                String recipeName = rs.getString("RecipeName");
                int categoryId = rs.getInt("category_id"); // Get category_id as well
                String categoryName = rs.getString("CategoryName");
                recipeCategories.add(String.format("RecID: %d (%s) --- CatID: %d (%s)",
                                                  recipeId, recipeName, categoryId, categoryName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipeCategories;
    }


    // Updated method: Deletes old association, inserts new one.
    // This effectively "moves" or "changes" an association.
    public boolean updateRecipeCategoryAssociation(int oldRecipeId, int oldCategoryId, int newRecipeId, int newCategoryId) {
        // Check if the new association would be a duplicate of an existing one (excluding the one being "updated" if IDs are the same)
        if (oldRecipeId == newRecipeId && oldCategoryId == newCategoryId) {
            // No change needed, or could be considered a success if the entry exists
            return true; // Or query if it exists and return that status
        }

        // Check if (newRecipeId, newCategoryId) already exists if it's different from old
        String checkSql = "SELECT COUNT(*) FROM RecipeCategory WHERE recipe_id = ? AND category_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)){
            checkStmt.setInt(1, newRecipeId);
            checkStmt.setInt(2, newCategoryId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Update failed: The target association (RecipeID=" + newRecipeId + ", CategoryID=" + newCategoryId + ") already exists.");
                return false; // New association would be a duplicate
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        String deleteSql = "DELETE FROM RecipeCategory WHERE recipe_id=? AND category_id=?";
        String insertSql = "INSERT INTO RecipeCategory (recipe_id, category_id) VALUES (?, ?)";
        
        Connection currentConn = null;
        try {
            currentConn = this.conn; // Use the class member connection
            currentConn.setAutoCommit(false); // Start transaction

            boolean deleted = false;
            try (PreparedStatement deleteStmt = currentConn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, oldRecipeId);
                deleteStmt.setInt(2, oldCategoryId);
                deleted = deleteStmt.executeUpdate() > 0;
            }

            if (!deleted) {
                // If the old record didn't exist, you might still want to proceed with insert,
                // or treat this as an error depending on desired behavior.
                // For an "update", the old record should ideally exist.
                System.err.println("Update failed: Original association (RecipeID=" + oldRecipeId + ", CategoryID=" + oldCategoryId + ") not found for deletion.");
                currentConn.rollback();
                return false;
            }

            boolean inserted = false;
            try (PreparedStatement insertStmt = currentConn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, newRecipeId);
                insertStmt.setInt(2, newCategoryId);
                inserted = insertStmt.executeUpdate() > 0;
            }
            
            if (inserted) {
                currentConn.commit();
                return true;
            } else {
                currentConn.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (currentConn != null) {
                try {
                    currentConn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (currentConn != null) {
                try {
                    currentConn.setAutoCommit(true); // Restore default
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

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