package recipedb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for retrieving records from the ChangeLog table.
 */
public class ChangeLogDAO {

    /**
     * Retrieves all change log entries from the database, formatted as readable strings.
     *
     * @return A list of strings, where each string represents a formatted log entry.
     * Returns an empty list if an error occurs or no logs exist.
     */
    public List<String> getFormattedChangeLogs() {
        List<String> formattedLogs = new ArrayList<>();
        // SQL query to select all columns from the generic ChangeLog table
        // Ordering by timestamp descending to get the newest logs first
        String sql = "SELECT id, table_name, record_id, operation_type, old_values, new_values, changed_at, changed_by FROM ChangeLog ORDER BY changed_at DESC";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Using try-with-resources ensures the connection, statement, and result set are closed
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate through the result set
            while (rs.next()) {
                // Retrieve data from each column of the ChangeLog table
                int logId = rs.getInt("id");
                String tableName = rs.getString("table_name");
                String recordId = rs.getString("record_id");
                String operationType = rs.getString("operation_type");
                String oldValues = rs.getString("old_values");
                String newValues = rs.getString("new_values");
                Timestamp changedAt = rs.getTimestamp("changed_at");
                String changedBy = rs.getString("changed_by");

                // Handle potential null values for JSON fields
                if (oldValues == null) {
                    oldValues = "N/A";
                }
                if (newValues == null) {
                    newValues = "N/A";
                }

                // Format the timestamp for display
                String formattedTimestamp = (changedAt != null) ? sdf.format(changedAt) : "N/A";

                // Format the entire log entry into a detailed, multi-line string for clarity
                String logEntryString = String.format(
                        "LOG ID: %d | Table: %s | Operation: %s | Record ID: %s | User: %s | Timestamp: %s\n\tOLD VALUES: %s\n\tNEW VALUES: %s",
                        logId,
                        tableName,
                        operationType.toUpperCase(),
                        recordId,
                        changedBy,
                        formattedTimestamp,
                        oldValues,
                        newValues
                );
                formattedLogs.add(logEntryString);
            }
        } catch (SQLException e) {
            // Print a more relevant error message for debugging
            System.err.println("Error fetching formatted change logs: " + e.getMessage());
            e.printStackTrace();
        }
        return formattedLogs; // Return the list of formatted log strings
    }
}