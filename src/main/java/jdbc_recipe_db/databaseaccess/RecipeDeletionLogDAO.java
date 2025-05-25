package jdbc_recipe_db.databaseaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat; // For formatting the timestamp
import java.util.ArrayList;
import java.util.List;

public class RecipeDeletionLogDAO {

    /**
     * Retrieves all recipe deletion log entries from the database, formatted as
     * strings.
     *
     * @return A list of strings, where each string represents a formatted log
     * entry. Returns an empty list if an error occurs or no logs exist.
     */
    public List<String> getFormattedDeletionLogs() {
        List<String> formattedLogs = new ArrayList<>();
        // SQL query to select all columns from the RecipeDeletionLog table
        // Ordering by timestamp descending to get the newest logs first
        String sql = "SELECT log_id, deleted_recipe_id, deleted_recipe_name, deletion_timestamp, deleted_by_user FROM RecipeDeletionLog ORDER BY deletion_timestamp DESC";

        // SimpleDateFormat for consistent timestamp formatting
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Using try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            // Iterate through the result set
            while (rs.next()) {
                // Retrieve data from each column
                int logId = rs.getInt("log_id");
                int deletedRecipeId = rs.getInt("deleted_recipe_id");
                String deletedRecipeName = rs.getString("deleted_recipe_name");
                Timestamp deletionTimestamp = rs.getTimestamp("deletion_timestamp");
                String deletedByUser = rs.getString("deleted_by_user");

                // Format the timestamp
                String formattedTimestamp = "N/A"; // Default if timestamp is null
                if (deletionTimestamp != null) {
                    formattedTimestamp = sdf.format(deletionTimestamp);
                }

                // Format the entire log entry as a string
                String logEntryString = String.format(
                        "Log ID: %d | Deleted Recipe ID: %d | Recipe Name: '%s' | Timestamp: %s | Deleted By: %s",
                        logId,
                        deletedRecipeId,
                        deletedRecipeName,
                        formattedTimestamp,
                        deletedByUser
                );
                formattedLogs.add(logEntryString);
            }
        } catch (SQLException e) {
            // Print stack trace for debugging in case of an SQL error
            // This follows the error handling pattern in your IngredientDAO
            System.err.println("Error fetching formatted recipe deletion logs: " + e.getMessage());
            e.printStackTrace();
        }
        return formattedLogs; // Return the list of formatted log strings
    }

}
