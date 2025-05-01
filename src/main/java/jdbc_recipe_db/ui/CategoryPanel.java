package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jdbc_recipe_db.databaseaccess.CategoryDAO;

public class CategoryPanel extends JPanel {

    private CategoryDAO categoryDAO;
    private JTextField categoryIdField, categoryNameField;
    private JTextArea outputArea;

    public CategoryPanel() {
        setLayout(new BorderLayout());
        categoryDAO = new CategoryDAO(); // Initialize DAO
        createUI();
    }

    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Category ID:"));
        categoryIdField = new JTextField(20);
        inputPanel.add(categoryIdField);

        inputPanel.add(new JLabel("Category Name:"));
        categoryNameField = new JTextField(20);
        inputPanel.add(categoryNameField);

        add(inputPanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBtn = new JButton("Create");
        createBtn.setPreferredSize(new Dimension(80, 30));
        JButton readBtn = new JButton("Read");
        readBtn.setPreferredSize(new Dimension(80, 30));
        JButton updateBtn = new JButton("Update");
        updateBtn.setPreferredSize(new Dimension(80, 30));
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        
        buttonPanel.add(createBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.CENTER);

        // Output Panel
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(40, 40, 40));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Button Actions
        createBtn.addActionListener(e -> createCategory());
        readBtn.addActionListener(e -> readCategories());
        updateBtn.addActionListener(e -> updateCategory());
        deleteBtn.addActionListener(e -> deleteCategory());
    }

    private void createCategory() {
        try {
            String name = categoryNameField.getText();
            boolean success = categoryDAO.createCategory(name);
            outputArea.setText(success ? "✅ Category created!" : "❌ Failed to create category.");
        } catch (Exception ex) {
            outputArea.setText("❗ Error processing request.");
        }
    }

    private void readCategories() {
        List<String> categories = categoryDAO.getCategories();
        outputArea.setText("📂 Categories:\n" + String.join("\n", categories));
    }

    private void updateCategory() {
        try {
            int id = Integer.parseInt(categoryIdField.getText());
            String name = categoryNameField.getText();

            boolean success = categoryDAO.updateCategory(id, name);
            outputArea.setText(success ? "✅ Category updated!" : "❌ No record found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter a numeric ID.");
        }
    }

    private void deleteCategory() {
        try {
            int id = Integer.parseInt(categoryIdField.getText());
            boolean success = categoryDAO.deleteCategory(id);
            outputArea.setText(success ? "🗑️ Category deleted!" : "❌ No record found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter a numeric ID.");
        }
    }
}
