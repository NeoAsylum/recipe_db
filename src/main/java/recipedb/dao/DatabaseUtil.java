package recipedb.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseUtil {

    private static String url;
    private static String username;
    private static String password;

    public static void configureConnection(String propertiesFilePath) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        url = props.getProperty("db.url");
        username = props.getProperty("db.username");
        password = props.getProperty("db.password");
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return null;
    }

}
