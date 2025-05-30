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

import recipedb.dao.RecipeDeletionLogDAO;

public class DeletionLogPanel extends JPanel {

    private RecipeDeletionLogDAO logDAO;
    private JTextArea logDisplayArea;
    private JButton refreshButton;

    public DeletionLogPanel() {
        this.logDAO = new RecipeDeletionLogDAO();
        setLayout(new BorderLayout(10, 10)); // Add some spacing
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel
        createUI();
    }

    private void createUI() {
        // Title Label
        JLabel titleLabel = new JLabel("Recipe Deletion Logs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Log Display Area
        logDisplayArea = new JTextArea(15, 60); // Adjusted size for better viewing
        logDisplayArea.setEditable(false);
        logDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Consistent font
        logDisplayArea.setBackground(new Color(245, 245, 245)); // Light gray background
        logDisplayArea.setForeground(new Color(50, 50, 50));    // Dark gray text
        logDisplayArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Inner padding
        ));
        logDisplayArea.setLineWrap(true); // Wrap lines that are too long
        logDisplayArea.setWrapStyleWord(true); // Wrap at word boundaries

        JScrollPane scrollPane = new JScrollPane(logDisplayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshButton = new JButton("View/Refresh Logs");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setPreferredSize(new Dimension(180, 35));

        // Styling the button a bit
        refreshButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listener for the button
        refreshButton.addActionListener(e -> displayLogs());
    }

    private void displayLogs() {
        List<String> logs = logDAO.getFormattedDeletionLogs();
        logDisplayArea.setText(""); // Clear previous logs

        if (logs.isEmpty()) {
            logDisplayArea.setForeground(Color.RED);
            logDisplayArea.setText("\n   No recipe deletion logs found.");
        } else {
            logDisplayArea.setForeground(new Color(50, 50, 50)); // Reset to default text color
            for (String logEntry : logs) {
                logDisplayArea.append(logEntry + "\n");
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
