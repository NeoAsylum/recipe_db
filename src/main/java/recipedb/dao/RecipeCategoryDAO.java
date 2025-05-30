package recipedb.dao;

import recipedb.model.RecipeCategory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryDAO implements MultiKeyDao<RecipeCategory> {

    @Override
    public RecipeCategory findByKeys(RecipeCategory object) {
        String sql = "SELECT COUNT(*) FROM RecipeCategory WHERE recipe_id=? AND category_id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipeId());
            stmt.setInt(2, object.getCategoryId());
            return stmt.executeUpdate() > 0 ? object : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteByKeys(RecipeCategory object) {
        String sql = "DELETE FROM RecipeCategory WHERE recipe_id=? AND category_id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipeId());
            stmt.setInt(2, object.getCategoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RecipeCategory> findAll() {
        List<RecipeCategory> result = new ArrayList<>();
        String sql = "SELECT rc.recipe_id, r.name AS RecipeName, rc.category_id, c.name AS CategoryName "
            + "FROM RecipeCategory rc "
            + "JOIN Recipe r ON rc.recipe_id = r.id "
            + "JOIN Category c ON rc.category_id = c.id "
            + "ORDER BY r.name, c.name";

        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(new RecipeCategory(
                    rs.getInt("recipe_id"),
                    rs.getString("RecipeName"),
                    rs.getInt("category_id"),
                    rs.getString("CategoryName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean create(RecipeCategory object) {
        String sql = "INSERT INTO RecipeCategory (recipe_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipeId());
            stmt.setInt(2, object.getCategoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.err.println("Attempted to insert duplicate RecipeCategory: " +
                    "RecipeID=" + object.getRecipeId() +
                    ", CategoryID=" + object.getCategoryId());
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean update(RecipeCategory object) {
        return false;
    }
}
