package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import jdbc_recipe_db.databaseaccess.RecipeDAO;

public class RecipePanel extends JPanel {

    private final RecipeDAO RECIPEDAO = new RecipeDAO();
    private JTextField recipeNameField, recipeDescField, recipeInstField, recipeCookTimeField, recipePrepTimeField;
    private JTextArea outputArea;
    private JComboBox<String> recipeIdComboBox; // JComboBox of Strings
    private boolean isPopulatingComboBox = false; // Flag to prevent event recursion

    public RecipePanel() {
        setLayout(new BorderLayout());
        createUI();
    }

    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Recipe:"));
        recipeIdComboBox = new JComboBox<>();
        inputPanel.add(recipeIdComboBox);

        inputPanel.add(new JLabel("Name:"));
        recipeNameField = new JTextField(20);
        inputPanel.add(recipeNameField);

        inputPanel.add(new JLabel("Description:"));
        recipeDescField = new JTextField(20);
        inputPanel.add(recipeDescField);

        inputPanel.add(new JLabel("Instructions:"));
        recipeInstField = new JTextField(20);
        inputPanel.add(recipeInstField);

        inputPanel.add(new JLabel("Cook time:"));
        recipeCookTimeField = new JTextField(20);
        inputPanel.add(recipeCookTimeField);

        inputPanel.add(new JLabel("Prep time:"));
        recipePrepTimeField = new JTextField(20);
        inputPanel.add(recipePrepTimeField);

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
        // Populate JComboBox when it's about to become visible
        recipeIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                populateRecipeIdComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        // Load details when an item is selected
        recipeIdComboBox.addActionListener(e -> {
            // Ensure this doesn't fire due to programmatic changes during population
            if (!isPopulatingComboBox && recipeIdComboBox.getSelectedItem() != null) {
                loadSelectedRecipeDetails();
            }
        });

        // Button Actions
        createBtn.addActionListener(e -> createRecipe());
        readBtn.addActionListener(e -> readRecipes());
        updateBtn.addActionListener(e -> updateRecipe());
        deleteBtn.addActionListener(e -> deleteRecipe());
    }

    private void loadSelectedRecipeDetails() {
        String selectedItem = (String) recipeIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            clearInputFields(false);
            return;
        }

        int recipeId = parseIdFromSelectedItem(selectedItem);
        if (recipeId == -1) {
            clearInputFields(false); // Clear fields if ID parsing failed
            return;
        }

        Map<String, String> details = RECIPEDAO.getRecipeDetailsById(recipeId);
        if (details != null) {
            recipeNameField.setText(details.get("name"));
            recipeDescField.setText(details.get("description"));
            recipeInstField.setText(details.get("instructions"));
            recipeCookTimeField.setText(details.get("cook_time"));
            recipePrepTimeField.setText(details.get("prep_time"));
            // outputArea.setText("Selected: " + details.get("name")); // Optional feedback
        } else {
            outputArea.setText("❌ Error: Could not load details for ID: " + recipeId);
            clearInputFields(false);
        }
    }

    private int parseIdFromSelectedItem(String selectedItem) {
        if (selectedItem == null || !selectedItem.startsWith("ID: ")) {
            return -1;
        }
        try {
            // Example: "ID: 123 - Recipe Name"
            String idStr = selectedItem.substring(4, selectedItem.indexOf(" - "));
            return Integer.parseInt(idStr.trim());
        } catch (Exception e) {
            outputArea.setText("❗ Error parsing ID from selected item: " + selectedItem);
            e.printStackTrace();
            return -1;
        }
    }

    private void populateRecipeIdComboBox() {
        isPopulatingComboBox = true; // Set flag
        String selectedItemBeforeUpdate = (String) recipeIdComboBox.getSelectedItem(); // Preserve selection
        recipeIdComboBox.removeAllItems();
        List<String> recipeSummaries = RECIPEDAO.getRecipeIdNameSummaries();
        if (recipeSummaries.isEmpty()) {
            outputArea.append("\nNo recipes available to select.");
        } else {
            for (String summary : recipeSummaries) {
                recipeIdComboBox.addItem(summary);
            }
            // Try to restore previous selection
            if (selectedItemBeforeUpdate != null && recipeSummaries.contains(selectedItemBeforeUpdate)) {
                recipeIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!recipeSummaries.isEmpty()) {
                recipeIdComboBox.setSelectedIndex(-1); // Or 0 for first item
            }
        }
        if (recipeIdComboBox.getSelectedIndex() == -1) {
            clearInputFields(false); // Clear fields if no selection (or after population)
        }
        isPopulatingComboBox = false; // Reset flag
    }

    private void clearInputFields(boolean clearComboBoxAlso) {
        if (clearComboBoxAlso) {
            // Setting selected index to -1 might trigger action listener if not careful
            // The isPopulatingComboBox flag helps manage this
            Object currentSelection = recipeIdComboBox.getSelectedItem();
            recipeIdComboBox.setSelectedIndex(-1);
            if (currentSelection != null) { // If something was selected, now it's not, so clear fields.
                // The action listener for selection change should handle clearing fields
                // if it's correctly guarded by isPopulatingComboBox
            }
        }
        recipeNameField.setText("");
        recipeDescField.setText("");
        recipeInstField.setText("");
        recipeCookTimeField.setText("");
        recipePrepTimeField.setText("");
    }

    private void createRecipe() {
        String name = recipeNameField.getText();
        String description = recipeDescField.getText();
        String instructions = recipeInstField.getText();
        int cookTime = Integer.parseInt(recipeCookTimeField.getText());
        int prepTime = Integer.parseInt(recipePrepTimeField.getText());

        boolean success = RECIPEDAO.createRecipe(name, description, instructions, prepTime, cookTime);
        outputArea.setText(success ? "✅ Recipe added: " + name : "❌ Failed to add recipe.");
    }

    private void readRecipes() {
        List<String> recipes = RECIPEDAO.getRecipes();
        outputArea.setText("📜 Recipes:\n" + String.join("\n", recipes));
    }

    private void updateRecipe() {
        String selectedItem = (String) recipeIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a recipe from the dropdown to update.");
            return;
        }

        int id = parseIdFromSelectedItem(selectedItem); // Use the helper to get ID
        if (id == -1) {
            // parseIdFromSelectedItem already shows an error message in outputArea
            return;
        }

        try {
            String name = recipeNameField.getText();
            String description = recipeDescField.getText();
            String instructions = recipeInstField.getText();

            if (name.trim().isEmpty() || recipePrepTimeField.getText().trim().isEmpty() || recipeCookTimeField.getText().trim().isEmpty()) {
                outputArea.setText("❗ Name, Prep Time, and Cook Time cannot be empty for update.");
                return;
            }
            int prepTime = Integer.parseInt(recipePrepTimeField.getText().trim()); // Correct order for DAO
            int cookTime = Integer.parseInt(recipeCookTimeField.getText().trim()); // Correct order for DAO

            // Ensure your DAO's updateRecipe method expects prepTime then cookTime
            boolean success = RECIPEDAO.updateRecipe(id, name, description, instructions, prepTime, cookTime);
            if (success) {
                outputArea.setText("✅ Recipe updated: " + name);
                // String oldSelection = (String) recipeIdComboBox.getSelectedItem(); // Not needed if we are re-populating and trying to set new
                populateRecipeIdComboBox(); // Refresh dropdown

                // Attempt to re-select the item (its name might have changed)
                String potentiallyUpdatedItemSummary = String.format("ID: %d - %s", id, name);
                javax.swing.DefaultComboBoxModel<String> model = (javax.swing.DefaultComboBoxModel<String>) recipeIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(potentiallyUpdatedItemSummary)) {
                        recipeIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No recipe found with ID: " + id + " or update failed.");
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid number format for Prep or Cook Time!");
            ex.printStackTrace();
        }
    }

    private void deleteRecipe() {
        String selectedItem = (String) recipeIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a recipe from the dropdown to delete.");
            return;
        }

        int id = parseIdFromSelectedItem(selectedItem); // Use the helper to get ID
        if (id == -1) {
            // parseIdFromSelectedItem already shows an error message in outputArea
            return;
        }

        try {
            boolean success = RECIPEDAO.deleteRecipe(id);
            if (success) {
                outputArea.setText("🗑️ Recipe deleted (ID: " + id + ")");
                populateRecipeIdComboBox(); // Refresh dropdown
                clearInputFields(true);     // Clear all fields including combo selection
            } else {
                outputArea.setText("❌ No recipe found with ID: " + id + " or delete failed.");
            }
        } catch (Exception ex) { // Broader catch for any unexpected DAO issues
            outputArea.setText("❗ An error occurred during deletion.");
            ex.printStackTrace();
        }
    }
}
