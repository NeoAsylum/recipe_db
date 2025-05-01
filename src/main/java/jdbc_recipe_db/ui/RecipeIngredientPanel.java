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

import jdbc_recipe_db.databaseaccess.RecipeIngredientDAO;

public class RecipeIngredientPanel extends JPanel {

    private RecipeIngredientDAO recipeIngredientDAO;
    private JTextField recipeIngredientIdField, recipeIdField, ingredientIdField, quantityField;
    private JTextArea outputArea;

    public RecipeIngredientPanel() {
        setLayout(new BorderLayout());
        recipeIngredientDAO = new RecipeIngredientDAO(); // Initialize DAO
        createUI();
    }
    
    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Recipe-Ingredient ID:"));
        recipeIngredientIdField = new JTextField(20);
        inputPanel.add(recipeIngredientIdField);

        inputPanel.add(new JLabel("Recipe ID:"));
        recipeIdField = new JTextField(20);
        inputPanel.add(recipeIdField);

        inputPanel.add(new JLabel("Ingredient ID:"));
        ingredientIdField = new JTextField(20);
        inputPanel.add(ingredientIdField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(20);
        inputPanel.add(quantityField);

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
        createBtn.addActionListener(e -> createRecipeIngredient());
        readBtn.addActionListener(e -> readRecipeIngredients());
        updateBtn.addActionListener(e -> updateRecipeIngredient());
        deleteBtn.addActionListener(e -> deleteRecipeIngredient());
    }

    private void createRecipeIngredient() {
        try {
            int recipeId = Integer.parseInt(recipeIdField.getText());
            int ingredientId = Integer.parseInt(ingredientIdField.getText());
            String quantity = quantityField.getText();

            boolean success = recipeIngredientDAO.createRecipeIngredient(recipeId, ingredientId, quantity);
            outputArea.setText(success ? "✅ RecipeIngredient created!" : "❌ Failed to create RecipeIngredient.");
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }

    private void readRecipeIngredients() {
        List<String> recipeIngredients = recipeIngredientDAO.getRecipeIngredients();
        outputArea.setText("📂 Recipe Ingredients:\n" + String.join("\n", recipeIngredients));
    }

    private void updateRecipeIngredient() {
        try {
            int id = Integer.parseInt(recipeIngredientIdField.getText());
            int recipeId = Integer.parseInt(recipeIdField.getText());
            int ingredientId = Integer.parseInt(ingredientIdField.getText());
            String quantity = quantityField.getText();

            boolean success = recipeIngredientDAO.updateRecipeIngredient(id, recipeId, ingredientId, quantity);
            outputArea.setText(success ? "✅ RecipeIngredient updated!" : "❌ No record found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }

    private void deleteRecipeIngredient() {
        try {
            int id = Integer.parseInt(recipeIngredientIdField.getText());
            boolean success = recipeIngredientDAO.deleteRecipeIngredient(id);
            outputArea.setText(success ? "🗑️ RecipeIngredient deleted!" : "❌ No record found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter a numeric ID.");
        }
    }
}
