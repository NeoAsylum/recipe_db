package jdbc_recipe_db.databaseaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Hello world!
 */
public final class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/FoodRecipesDB"; // Your DB name
    private static final String USER = "root"; // Replace with MySQL username
    private static final String PASSWORD = ""; // Replace with MySQL password
    protected static Connection connection;

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database successfully!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return null;
    }

}
