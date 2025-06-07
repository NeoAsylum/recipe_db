package recipedb.dao;

import recipedb.model.RecipeIngredient;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientDAO implements MultiKeyDao<RecipeIngredient> {

    @Override
    public RecipeIngredient findByKeys(RecipeIngredient object) {
        String sql =
            "SELECT ri.recipe_id, r.name AS RecipeName, "
                +"ri.ingredient_id, i.name AS IngredientName, ri.quantity "
                + "FROM RecipeIngredient ri "
                + "JOIN Recipe r ON ri.recipe_id = r.id "
                + "JOIN Ingredient i ON ri.ingredient_id = i.id "
                + "WHERE ri.recipe_id = ? AND ri.ingredient_id = ?"
                + "ORDER BY r.name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, object.getRecipeId());
             stmt.setInt(2, object.getIngredientId());
             try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RecipeIngredient(
                        rs.getInt("recipe_id"),
                        rs.getString("RecipeName"),
                        rs.getInt("ingredient_id"),
                        rs.getString("IngredientName"),
                        rs.getString("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteByKeys(RecipeIngredient object) {
        String sql = "DELETE FROM RecipeIngredient WHERE recipe_id=? AND ingredient_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipeId());
            stmt.setInt(2, object.getIngredientId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RecipeIngredient> findAll() {
        List<RecipeIngredient> result = new ArrayList<>();
        String sql = "{CALL sp_RecipeIngredient_FindAll()}";

        try (Connection conn = DatabaseUtil.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            while (rs.next()) {
                result.add(new RecipeIngredient(
                    rs.getInt("recipe_id"),
                    rs.getString("RecipeName"),
                    rs.getInt("ingredient_id"),
                    rs.getString("IngredientName"),
                    rs.getString("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean create(RecipeIngredient object) {
        String sql = "INSERT INTO RecipeIngredient (recipe_id, ingredient_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipeId());
            stmt.setInt(2, object.getIngredientId());
            stmt.setString(3, object.getQuantity());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(RecipeIngredient object) {
        String sql = "UPDATE RecipeIngredient SET quantity=? WHERE recipe_id=? AND ingredient_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, object.getQuantity());
            stmt.setInt(2, object.getRecipeId());
            stmt.setInt(3, object.getIngredientId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
