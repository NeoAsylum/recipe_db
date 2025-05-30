package recipedb.dao;

import recipedb.model.Ingredient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO implements IdDao<Ingredient> {

    // New method to get ID-Name summaries for the JComboBox
    public List<String> getIngredientIdNameSummaries() {
        List<String> ingredientSummaries = new ArrayList<>();
        String sql = "SELECT id, name FROM Ingredient ORDER BY id";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                ingredientSummaries.add(String.format("ID: %d - %s", id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientSummaries;
    }

    @Override
    public Ingredient findById(int id) {
        String sql = "SELECT name, calories, protein, fat, carbohydrates, fiber FROM Ingredient WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ingredient(
                        id,
                        rs.getString("name"),
                        rs.getInt("calories"),
                        rs.getFloat("protein"),
                        rs.getFloat("fat"),
                        rs.getFloat("carbohydrates"),
                        rs.getFloat("fiber"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Ingredient> findAll() {
        List<Ingredient> result = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient ORDER BY id";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("calories"),
                    rs.getFloat("protein"),
                    rs.getFloat("fat"),
                    rs.getFloat("carbohydrates"),
                    rs.getFloat("fiber")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean create(Ingredient object) {
        String sql = "INSERT INTO Ingredient (name, calories, protein, fat, carbohydrates, fiber) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            fillStatement(object, stmt);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Ingredient object) {
        String sql = "UPDATE Ingredient SET name=?, calories=?, protein=?, fat=?, carbohydrates=?, fiber=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            fillStatement(object, stmt);
            stmt.setInt(7, object.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM Ingredient WHERE id=?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fillStatement(Ingredient object, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, object.getName());
        stmt.setInt(2, object.getCalories());
        stmt.setFloat(3, object.getProtein());
        stmt.setFloat(4, object.getFat());
        stmt.setFloat(5, object.getCarbohydrates());
        stmt.setFloat(6, object.getFiber());
    }
}
