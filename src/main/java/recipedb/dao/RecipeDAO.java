package recipedb.dao;

import recipedb.model.Recipe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO implements IdDao<Recipe>{

    @Override
    public Recipe findById(int id) {
        String sql = "SELECT name, description, instructions, prep_time, cook_time FROM Recipe WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Recipe(
                        id,
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("instructions"),
                        rs.getInt("prep_time"),
                        rs.getInt("cook_time"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Recipe> findAll() {
        List<Recipe> result = new ArrayList<>();
        String sql = "SELECT * FROM Recipe";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(new Recipe(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("instructions"),
                    rs.getInt("prep_time"),
                    rs.getInt("cook_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean create(Recipe object) {
        String sql = "INSERT INTO Recipe (name, description, instructions, prep_time, cook_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, object.getName());
            stmt.setString(2, object.getDescription());
            stmt.setString(3, object.getInstructions());
            stmt.setInt(4, object.getPrepTime());
            stmt.setInt(5, object.getCookTime());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Recipe object) {
        String sql = "UPDATE Recipe SET name=?, description=?, instructions=?, prep_time=?, cook_time=? WHERE id=?";
        // implement transaction to make each field update in a separate sql query,
        // bcs right now users have to fill out each field to not set them to null which is annoying
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, object.getName());
            stmt.setString(2, object.getDescription());
            stmt.setString(3, object.getInstructions());
            stmt.setInt(4, object.getPrepTime());
            stmt.setInt(5, object.getCookTime());
            stmt.setInt(6, object.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM Recipe WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Double> getRecipeNutrition(int recipeId) {
        List<Double> result = new ArrayList<>();
        String sql = "{CALL sp_TotalRecipeNutrition(?)}";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)){
            stmt.setInt(1, recipeId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                result.add(resultSet.getDouble(1)); // Calories
                result.add(resultSet.getDouble(2)); // Protein
                result.add(resultSet.getDouble(3)); // Fat
                result.add(resultSet.getDouble(4)); // Carbohydrates
                result.add(resultSet.getDouble(5)); // Fiber
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
        }
    }
}
