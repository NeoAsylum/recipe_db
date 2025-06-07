package recipedb.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import recipedb.dao.ChangeLogDAO; // UPDATED import

/**
 * A UI panel that displays all database change logs from the ChangeLog table.
 */
public class ChangeLogPanel extends JPanel {

    private ChangeLogDAO logDAO; // UPDATED to use the generic ChangeLogDAO
    private JTextArea logDisplayArea;
    private JButton refreshButton;

    public ChangeLogPanel() {
        this.logDAO = new ChangeLogDAO(); // UPDATED instantiation
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createUI();
    }

    private void createUI() {
        // Title Label - UPDATED to be more generic
        JLabel titleLabel = new JLabel("Database Change Logs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Log Display Area
        logDisplayArea = new JTextArea(15, 60);
        logDisplayArea.setEditable(false);
        logDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logDisplayArea.setBackground(new Color(245, 245, 245));
        logDisplayArea.setForeground(new Color(50, 50, 50));
        logDisplayArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        // Note: Line wrapping is less ideal for the new multi-line format, but kept for consistency.
        // You might want to consider horizontal scrolling instead for very long JSON strings.
        logDisplayArea.setLineWrap(true);
        logDisplayArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logDisplayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshButton = new JButton("View/Refresh Logs");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setPreferredSize(new Dimension(180, 35));

        // Styling the button
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listener for the button
        refreshButton.addActionListener(e -> displayLogs());
    }

    private void displayLogs() {
        // UPDATED to call the new method from ChangeLogDAO
        List<String> logs = logDAO.getFormattedChangeLogs();
        logDisplayArea.setText(""); // Clear previous logs

        if (logs.isEmpty()) {
            logDisplayArea.setForeground(Color.RED);
            // UPDATED message for when no logs are found
            logDisplayArea.setText("\n   No change logs found.");
        } else {
            logDisplayArea.setForeground(new Color(50, 50, 50));
            for (String logEntry : logs) {
                // The new format includes newline characters, so we add another for spacing
                logDisplayArea.append(logEntry + "\n\n");
            }
            // Scroll to the top after loading logs
            logDisplayArea.setCaretPosition(0);
        }
    }

    /**
     * Public method to allow external refreshing of logs if needed.
     */
    public void refreshLogView() {
        displayLogs();
    }
}