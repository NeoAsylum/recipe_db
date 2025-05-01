
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

import jdbc_recipe_db.databaseaccess.RecipeDAO;

public class RecipePanel extends JPanel {

    private RecipeDAO recipeDAO = new RecipeDAO();
    private JTextField recipeIdField, recipeNameField, recipeDescField, recipeInstField;
    private JTextArea outputArea;

    public RecipePanel() {
        setLayout(new BorderLayout());
        createUI();
    }

    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Recipe ID:"));
        recipeIdField = new JTextField(20);
        inputPanel.add(recipeIdField);

        inputPanel.add(new JLabel("Name:"));
        recipeNameField = new JTextField(20);
        inputPanel.add(recipeNameField);

        inputPanel.add(new JLabel("Description:"));
        recipeDescField = new JTextField(20);
        inputPanel.add(recipeDescField);

        inputPanel.add(new JLabel("Instructions:"));
        recipeInstField = new JTextField(20);
        inputPanel.add(recipeInstField);

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
        createBtn.addActionListener(e -> createRecipe());
        readBtn.addActionListener(e -> readRecipes());
        updateBtn.addActionListener(e -> updateRecipe());
        deleteBtn.addActionListener(e -> deleteRecipe());
    }

    private void createRecipe() {
        String name = recipeNameField.getText();
        String description = recipeDescField.getText();
        String instructions = recipeInstField.getText();
        boolean success = recipeDAO.createRecipe(name, description, instructions, 0, 0);
        outputArea.setText(success ? "✅ Recipe added: " + name : "❌ Failed to add recipe.");
    }

    private void readRecipes() {
        List<String> recipes = recipeDAO.getRecipes();
        outputArea.setText("📜 Recipes:\n" + String.join("\n", recipes));
    }

    private void updateRecipe() {
        try {
            int id = Integer.parseInt(recipeIdField.getText());
            String name = recipeNameField.getText();
            boolean success = recipeDAO.updateRecipe(id, name);
            outputArea.setText(success ? "✅ Recipe updated: " + name : "❌ No recipe found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid ID format!");
        }
    }

    private void deleteRecipe() {
        try {
            int id = Integer.parseInt(recipeIdField.getText());
            boolean success = recipeDAO.deleteRecipe(id);
            outputArea.setText(success ? "🗑️ Recipe deleted: " + id : "❌ No recipe found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid ID format!");
        }
    }
}
