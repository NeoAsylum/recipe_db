package jdbc_recipe_db.databaseaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/FoodRecipesDB";
    private static final String USER = "root";
    private static final String PASSWORD = "passwort";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return null;
    }

}
