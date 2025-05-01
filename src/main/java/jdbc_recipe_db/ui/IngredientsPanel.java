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

import jdbc_recipe_db.databaseaccess.IngredientDAO;

public class IngredientsPanel extends JPanel {

    private IngredientDAO ingredientDAO;
    private JTextField ingredientIdField, ingredientNameField, caloriesField, proteinField, fatField, carbField, fiberField;
    private JTextArea outputArea;

    public IngredientsPanel() {
        setLayout(new BorderLayout());
        ingredientDAO = new IngredientDAO(); // Initialize DAO
        createUI();
    }

    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Ingredient ID:"));
        ingredientIdField = new JTextField(20);
        inputPanel.add(ingredientIdField);

        inputPanel.add(new JLabel("Ingredient Name:"));
        ingredientNameField = new JTextField(20);
        inputPanel.add(ingredientNameField);

        inputPanel.add(new JLabel("Calories:"));
        caloriesField = new JTextField(20);
        inputPanel.add(caloriesField);

        inputPanel.add(new JLabel("Protein (g):"));
        proteinField = new JTextField(20);
        inputPanel.add(proteinField);

        inputPanel.add(new JLabel("Fat (g):"));
        fatField = new JTextField(20);
        inputPanel.add(fatField);

        inputPanel.add(new JLabel("Carbohydrates (g):"));
        carbField = new JTextField(20);
        inputPanel.add(carbField);

        inputPanel.add(new JLabel("Fiber (g):"));
        fiberField = new JTextField(20);
        inputPanel.add(fiberField);

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
        createBtn.addActionListener(e -> createIngredient());
        readBtn.addActionListener(e -> readIngredients());
        updateBtn.addActionListener(e -> updateIngredient());
        deleteBtn.addActionListener(e -> deleteIngredient());
    }

    private void createIngredient() {
        try {
            String name = ingredientNameField.getText();
            int calories = Integer.parseInt(caloriesField.getText());
            float protein = Float.parseFloat(proteinField.getText());
            float fat = Float.parseFloat(fatField.getText());
            float carbohydrates = Float.parseFloat(carbField.getText());
            float fiber = Float.parseFloat(fiberField.getText());

            boolean success = ingredientDAO.createIngredient(name, calories, protein, fat, carbohydrates, fiber);
            outputArea.setText(success ? "✅ Ingredient created!" : "❌ Failed to create ingredient.");
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }

    private void readIngredients() {
        List<String> ingredients = ingredientDAO.getIngredients();
        outputArea.setText("📂 Ingredients:\n" + String.join("\n", ingredients));
    }

    private void updateIngredient() {
        try {
            int id = Integer.parseInt(ingredientIdField.getText());
            String name = ingredientNameField.getText();
            int calories = Integer.parseInt(caloriesField.getText());
            float protein = Float.parseFloat(proteinField.getText());
            float fat = Float.parseFloat(fatField.getText());
            float carbohydrates = Float.parseFloat(carbField.getText());
            float fiber = Float.parseFloat(fiberField.getText());

            boolean success = ingredientDAO.updateIngredient(id, name, calories, protein, fat, carbohydrates, fiber);
            outputArea.setText(success ? "✅ Ingredient updated!" : "❌ No record found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter numeric values.");
        }
    }

    private void deleteIngredient() {
        try {
            int id = Integer.parseInt(ingredientIdField.getText());
            boolean success = ingredientDAO.deleteIngredient(id);
            outputArea.setText(success ? "🗑️ Ingredient deleted!" : "❌ No record found with ID: " + id);
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter a numeric ID.");
        }
    }
}
