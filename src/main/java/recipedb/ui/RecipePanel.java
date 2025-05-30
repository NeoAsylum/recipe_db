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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import recipedb.dao.RecipeDAO;
import recipedb.model.Recipe;

public class RecipePanel extends JPanel {

    private final RecipeDAO recipeDAO = new RecipeDAO();
    private JTextField recipeNameField, recipeDescField, recipeInstField, recipeCookTimeField, recipePrepTimeField;
    private JTextArea outputArea;
    private JComboBox<Recipe> recipeIdComboBox;
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
        Recipe recipe = (Recipe) recipeIdComboBox.getSelectedItem();
        if (recipe == null) {
            clearInputFields(false);
        } else {
            recipeNameField.setText(recipe.getName());
            recipeDescField.setText(recipe.getDescription());
            recipeInstField.setText(recipe.getInstructions());
            recipeCookTimeField.setText(String.valueOf(recipe.getCookTime()));
            recipePrepTimeField.setText(String.valueOf(recipe.getPrepTime()));
            // outputArea.setText("Selected: " + details.get("name")); // Optional feedback
        }
    }

    private void populateRecipeIdComboBox() {
        isPopulatingComboBox = true; // Set flag
        Recipe selectedItemBeforeUpdate = (Recipe) recipeIdComboBox.getSelectedItem(); // Preserve selection
        recipeIdComboBox.removeAllItems();
        List<Recipe> recipeSummaries = recipeDAO.findAll();
        if (recipeSummaries.isEmpty()) {
            outputArea.append("\nNo recipes available to select.");
        } else {
            for (Recipe summary : recipeSummaries) {
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

        boolean success = recipeDAO.create(new Recipe(-1, name, description, instructions, prepTime, cookTime));
        outputArea.setText(success ? "✅ Recipe added: " + name : "❌ Failed to add recipe.");
    }

    private void readRecipes() {
        List<Recipe> recipes = recipeDAO.findAll();
        outputArea.setText("📜 Recipes:\n" + recipes.stream().map(Recipe::toDetailedString).collect(Collectors.joining("\n")));
    }

    private void updateRecipe() {
        Recipe selectedItem = (Recipe) recipeIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a recipe from the dropdown to update.");
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
            boolean success = recipeDAO.update(new Recipe(selectedItem.getId(), name, description, instructions, prepTime, cookTime));
            if (success) {
                outputArea.setText("✅ Recipe updated: " + name);
                // String oldSelection = (String) recipeIdComboBox.getSelectedItem(); // Not needed if we are re-populating and trying to set new
                populateRecipeIdComboBox(); // Refresh dropdown

                // Attempt to re-select the item (its name might have changed)
                String potentiallyUpdatedItemSummary = String.format("ID: %d - %s", selectedItem.getId(), name);
                javax.swing.DefaultComboBoxModel<Recipe> model = (javax.swing.DefaultComboBoxModel<Recipe>) recipeIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(potentiallyUpdatedItemSummary)) {
                        recipeIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No recipe found with ID: " + selectedItem.getId() + " or update failed.");
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid number format for Prep or Cook Time!");
            ex.printStackTrace();
        }
    }

    private void deleteRecipe() {
        Recipe selectedItem = (Recipe) recipeIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a recipe from the dropdown to delete.");
            return;
        }

        try {
            boolean success = recipeDAO.deleteById(selectedItem.getId());
            if (success) {
                outputArea.setText("🗑️ Recipe deleted (ID: " + selectedItem.getId() + ")");
                populateRecipeIdComboBox(); // Refresh dropdown
                clearInputFields(true); // Clear all fields including combo selection
            } else {
                outputArea.setText("❌ No recipe found with ID: " + selectedItem.getId() + " or delete failed.");
            }
        } catch (Exception ex) { // Broader catch for any unexpected DAO issues
            outputArea.setText("❗ An error occurred during deletion.");
            ex.printStackTrace();
        }
    }
}
