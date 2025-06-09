package recipedb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import recipedb.model.Recipe;
import recipedb.model.Review;

public class ReviewDAO implements IdDao<Review>{
	
	@Override
    public Review findById(int id) {
        String sql = "SELECT recipe_id, recipe_id, message FROM Review WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Review(
                        id,
                        rs.getInt("recipe_id"),
                        rs.getInt("recipe_id"),
                        rs.getString("message"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	@Override
	public List<Review> findAll() {
	    List<Review> result = new ArrayList<>();
	    String sql = "SELECT * FROM Review";
	    try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            result.add(new Review(
	                rs.getInt("id"),
	                rs.getInt("recipe_id"),
	                rs.getInt("user_id"),
	                rs.getString("message")
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	@Override
	public boolean create(Review object) {
        String sql = "INSERT INTO Review (recipe_id, user_id, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipe());
            stmt.setInt(2, object.getUser());
            stmt.setString(3, object.getMessage());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
	
	@Override
    public boolean update(Review object) {
        String sql = "UPDATE Review SET recipe_id=?, user_id=?, message=? WHERE id=?";
        // implement transaction to make each field update in a separate sql query,
        // bcs right now users have to fill out each field to not set them to null which is annoying
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, object.getRecipe());
            stmt.setInt(2, object.getUser());
            stmt.setString(3, object.getMessage());
            stmt.setInt(4, object.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	@Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM Review WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
