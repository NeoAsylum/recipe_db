package recipedb.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import recipedb.dao.IngredientDAO;
import recipedb.model.Ingredient;

public class IngredientsPanel extends JPanel {

    private final IngredientDAO ingredientDAO;
    // private JTextField ingredientIdField;
    private JTextField ingredientNameField, caloriesField, proteinField, fatField, carbField, fiberField;
    private JTextArea outputArea;
    private JComboBox<String> ingredientIdComboBox; // JComboBox for ingredient selection
    private boolean isPopulatingComboBox = false; // Flag to prevent event recursion

    public IngredientsPanel() {
        setLayout(new BorderLayout());
        ingredientDAO = new IngredientDAO();
        createUI();
    }

    private void createUI() {
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Ingredient:")); // Changed label
        ingredientIdComboBox = new JComboBox<>(); // Initialize JComboBox
        inputPanel.add(ingredientIdComboBox);

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBtn = new JButton("Create");
        createBtn.setPreferredSize(new Dimension(80, 30));
        JButton readBtn = new JButton("Read All"); // Changed label for clarity
        readBtn.setPreferredSize(new Dimension(100, 30));
        JButton updateBtn = new JButton("Update");
        updateBtn.setPreferredSize(new Dimension(80, 30));
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setPreferredSize(new Dimension(80, 30));

        buttonPanel.add(createBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(40, 40, 40));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Populate JComboBox when it's about to become visible
        ingredientIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                populateIngredientIdComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        // Load details when an item is selected
        ingredientIdComboBox.addActionListener(e -> {
            if (!isPopulatingComboBox && ingredientIdComboBox.getSelectedItem() != null) {
                loadSelectedIngredientDetails();
            }
        });

        createBtn.addActionListener(e -> createIngredient());
        readBtn.addActionListener(e -> readIngredients());
        updateBtn.addActionListener(e -> updateIngredient());
        deleteBtn.addActionListener(e -> deleteIngredient());

        // Initial population
        populateIngredientIdComboBox();
    }

    private void populateIngredientIdComboBox() {
        isPopulatingComboBox = true;
        String selectedItemBeforeUpdate = (String) ingredientIdComboBox.getSelectedItem();
        ingredientIdComboBox.removeAllItems(); // Important to call before adding new items

        List<String> ingredientSummaries = ingredientDAO.getIngredientIdNameSummaries();
        if (ingredientSummaries.isEmpty()) {
            outputArea.append("\nNo ingredients available to select.");
        } else {
            for (String summary : ingredientSummaries) {
                ingredientIdComboBox.addItem(summary);
            }
            if (selectedItemBeforeUpdate != null && ingredientSummaries.contains(selectedItemBeforeUpdate)) {
                ingredientIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!ingredientSummaries.isEmpty()) {
                ingredientIdComboBox.setSelectedIndex(-1); // Default to no selection or first item
            }
        }
        if (ingredientIdComboBox.getSelectedIndex() == -1) {
            clearInputFields(false);
        }
        isPopulatingComboBox = false;
    }

    private int parseIdFromSelectedItem(String selectedItem) {
        if (selectedItem == null || !selectedItem.startsWith("ID: ")) {
            return -1;
        }
        try {
            String idStr = selectedItem.substring(4, selectedItem.indexOf(" - "));
            return Integer.parseInt(idStr.trim());
        } catch (Exception e) {
            outputArea.setText("❗ Error parsing ID from selected item: " + selectedItem);
            e.printStackTrace();
            return -1;
        }
    }

    private void loadSelectedIngredientDetails() {
        String selectedItem = (String) ingredientIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            clearInputFields(false);
            return;
        }
        int ingredientId = parseIdFromSelectedItem(selectedItem);
        if (ingredientId == -1) {
            clearInputFields(false);
            return;
        }

        Ingredient details = ingredientDAO.findById(ingredientId);
        if (details != null) {
            ingredientNameField.setText(details.getName());
            caloriesField.setText(String.valueOf(details.getCalories()));
            proteinField.setText(String.valueOf(details.getProtein()));
            fatField.setText(String.valueOf(details.getFat()));
            carbField.setText(String.valueOf(details.getCarbohydrates()));
            fiberField.setText(String.valueOf(details.getFiber()));
        } else {
            outputArea.setText("❌ Error: Could not load details for Ingredient ID: " + ingredientId);
            clearInputFields(false);
        }
    }

    private void clearInputFields(boolean clearComboBoxAlso) {
        if (clearComboBoxAlso) {
            ingredientIdComboBox.setSelectedIndex(-1);
        }
        ingredientNameField.setText("");
        caloriesField.setText("");
        proteinField.setText("");
        fatField.setText("");
        carbField.setText("");
        fiberField.setText("");
    }

    private void createIngredient() {
        try {
            String name = ingredientNameField.getText();
            if (name.trim().isEmpty()) {
                outputArea.setText("❗ Ingredient name cannot be empty.");
                return;
            }
            // Add similar checks for other fields if they cannot be empty or need specific formats
            int calories = Integer.parseInt(caloriesField.getText());
            float protein = Float.parseFloat(proteinField.getText());
            float fat = Float.parseFloat(fatField.getText());
            float carbohydrates = Float.parseFloat(carbField.getText());
            float fiber = Float.parseFloat(fiberField.getText());

            boolean success = ingredientDAO.create(new Ingredient(-1, name, calories, protein, fat, carbohydrates, fiber));
            if (success) {
                outputArea.setText("✅ Ingredient created: " + name);
                populateIngredientIdComboBox();
                clearInputFields(true);
            } else {
                outputArea.setText("❌ Failed to create ingredient.");
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter valid numbers for nutritional values.");
        }
    }

    private void readIngredients() {
        List<Ingredient> ingredients = ingredientDAO.findAll();
        outputArea.setText("📂 Ingredients:\n" + ingredients.stream().map(Ingredient::toDetailedString).collect(Collectors.joining("\n")));
    }

    private void updateIngredient() {
        String selectedItem = (String) ingredientIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select an ingredient from the dropdown to update.");
            return;
        }
        int id = parseIdFromSelectedItem(selectedItem);
        if (id == -1) {
            return;
        }

        try {
            String name = ingredientNameField.getText();
            if (name.trim().isEmpty()) {
                outputArea.setText("❗ Ingredient name cannot be empty for update.");
                return;
            }
            int calories = Integer.parseInt(caloriesField.getText());
            float protein = Float.parseFloat(proteinField.getText());
            float fat = Float.parseFloat(fatField.getText());
            float carbohydrates = Float.parseFloat(carbField.getText());
            float fiber = Float.parseFloat(fiberField.getText());

            boolean success = ingredientDAO.update(new Ingredient(id, name, calories, protein, fat, carbohydrates, fiber));
            if (success) {
                outputArea.setText("✅ Ingredient updated: " + name);
                populateIngredientIdComboBox();

                // Attempt to re-select the updated item
                String potentiallyUpdatedItemSummary = String.format("ID: %d - %s", id, name);
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) ingredientIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(potentiallyUpdatedItemSummary)) {
                        ingredientIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No ingredient found with ID: " + id + " or update failed.");
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid input format! Please enter valid numbers for nutritional values.");
        }
    }

    private void deleteIngredient() {
        String selectedItem = (String) ingredientIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select an ingredient from the dropdown to delete.");
            return;
        }
        int id = parseIdFromSelectedItem(selectedItem);
        if (id == -1) {
            return;
        }

        try {
            boolean success = ingredientDAO.deleteById(id);
            if (success) {
                outputArea.setText("🗑️ Ingredient deleted (ID: " + id + ")");
                populateIngredientIdComboBox();
                clearInputFields(true);
            } else {
                outputArea.setText("❌ No ingredient found with ID: " + id + " or delete failed.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ An error occurred during deletion.");
            ex.printStackTrace();
        }
    }
}
