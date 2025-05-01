package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jdbc_recipe_db.databaseaccess.RecipeCategoryDAO;

public class RecipeCategoryPanel extends JPanel {

    private RecipeCategoryDAO recipeCategoryDAO;
    private JTextField recipeIdField, categoryIdField;
    private JTextArea outputArea;

    public RecipeCategoryPanel() {
        setLayout(new BorderLayout());
        try {
            recipeCategoryDAO = new RecipeCategoryDAO(); // Initialize DAO
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        createUI();
    }

    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Recipe ID:"));
        recipeIdField = new JTextField(20);
        inputPanel.add(recipeIdField);

        inputPanel.add(new JLabel("Category ID:"));
        categoryIdField = new JTextField(20);
        inputPanel.add(categoryIdField);

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

        // Button Actions (Using Updated DAO)
        createBtn.addActionListener(e -> createRecipeCategory());
        readBtn.addActionListener(e -> readRecipeCategories());
        updateBtn.addActionListener(e -> updateRecipeCategory());
        deleteBtn.addActionListener(e -> deleteRecipeCategory());
    }

    private void createRecipeCategory() {
        try {
            int recipeId = Integer.parseInt(recipeIdField.getText());
            int categoryId = Integer.parseInt(categoryIdField.getText());

            boolean success = recipeCategoryDAO.createRecipeCategory(recipeId, categoryId);
            outputArea.setText(success ? "✅ RecipeCategory created!" : "❌ Failed to create RecipeCategory.");
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }

    private void readRecipeCategories() {
        List<String> recipeCategories = recipeCategoryDAO.getRecipeCategories();
        outputArea.setText("📂 Recipe Categories:\n" + String.join("\n", recipeCategories));
    }

    private void updateRecipeCategory() {
        try {
            int recipeId = Integer.parseInt(recipeIdField.getText());
            int newCategoryId = Integer.parseInt(categoryIdField.getText());

            boolean success = recipeCategoryDAO.updateRecipeCategory(recipeId, newCategoryId);
            outputArea.setText(success ? "✅ RecipeCategory updated!" : "❌ No record found with Recipe ID: " + recipeId);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }

    private void deleteRecipeCategory() {
        try {
            int recipeId = Integer.parseInt(recipeIdField.getText());
            int categoryId = Integer.parseInt(categoryIdField.getText());

            boolean success = recipeCategoryDAO.deleteRecipeCategory(recipeId, categoryId);
            outputArea.setText(success ? "🗑️ RecipeCategory deleted!" : "❌ No record found with Recipe ID: " + recipeId);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }
}
