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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import recipedb.dao.IngredientDAO;
import recipedb.dao.RecipeDAO;
import recipedb.dao.RecipeIngredientDAO;
import recipedb.model.Ingredient;
import recipedb.model.Recipe;
import recipedb.model.RecipeIngredient;

public class RecipeIngredientPanel extends JPanel {

    private RecipeIngredientDAO recipeIngredientDAO;
    private RecipeDAO recipeDAO;
    private IngredientDAO ingredientDAO;

    private JComboBox<RecipeIngredient> existingAssociationComboBox;
    private JComboBox<Recipe> targetRecipeIdComboBox;
    private JComboBox<Ingredient> targetIngredientIdComboBox;
    private JTextField quantityField;

    private JTextArea outputArea;

    private boolean isPopulatingExistingAssoc = false;
    private boolean isPopulatingTargetRecipe = false;
    private boolean isPopulatingTargetIngredient = false;

    private RecipeIngredient selectedRecipeIngredient = null;

    public RecipeIngredientPanel() {
        setLayout(new BorderLayout());
        // Initialize DAOs (handle potential SQLException from RecipeCategoryDAO constructor if any)
        try {
            recipeIngredientDAO = new RecipeIngredientDAO();
            recipeDAO = new RecipeDAO();
            ingredientDAO = new IngredientDAO();
        } catch (Exception e) { // Broader catch if other DAOs might throw
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ DAO Initialization Failed: " + e.getMessage(), "DAO Error", JOptionPane.ERROR_MESSAGE);
        }
        createUI();
    }

    private void createUI() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // 4 rows
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Existing Assignment:"));
        existingAssociationComboBox = new JComboBox<>();
        inputPanel.add(existingAssociationComboBox);

        inputPanel.add(new JLabel("Recipe (Target/Current):"));
        targetRecipeIdComboBox = new JComboBox<>();
        inputPanel.add(targetRecipeIdComboBox);

        inputPanel.add(new JLabel("Ingredient (Target/Current):"));
        targetIngredientIdComboBox = new JComboBox<>();
        inputPanel.add(targetIngredientIdComboBox);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(20);
        inputPanel.add(quantityField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton assignBtn = new JButton("Assign New");
        assignBtn.setPreferredSize(new Dimension(130, 30));
        JButton readBtn = new JButton("Read All");
        readBtn.setPreferredSize(new Dimension(120, 30));
        JButton updateSelectedBtn = new JButton("Update Selected");
        updateSelectedBtn.setPreferredSize(new Dimension(150, 30));
        JButton removeSelectedBtn = new JButton("Remove Selected");
        removeSelectedBtn.setPreferredSize(new Dimension(160, 30));

        buttonPanel.add(assignBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(updateSelectedBtn);
        buttonPanel.add(removeSelectedBtn);
        add(buttonPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(40, 40, 40));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // PopupMenuListeners for JComboBoxes
        existingAssociationComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                populateExistingAssociationComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        targetRecipeIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                populateTargetRecipeIdComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        targetIngredientIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                populateTargetIngredientIdComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        existingAssociationComboBox.addActionListener(e -> {
            if (!isPopulatingExistingAssoc && existingAssociationComboBox.getSelectedItem() != null) {
                loadSelectedAssociationDetails();
            }
        });

        assignBtn.addActionListener(e -> assignNewRecipeIngredient());
        readBtn.addActionListener(e -> readAllRecipeIngredients());
        updateSelectedBtn.addActionListener(e -> updateSelectedRecipeIngredient());
        removeSelectedBtn.addActionListener(e -> removeSelectedRecipeIngredient());

        // Initial population
        populateTargetRecipeIdComboBox();
        populateTargetIngredientIdComboBox();
        populateExistingAssociationComboBox();

    }

    private void populateExistingAssociationComboBox() {
        isPopulatingExistingAssoc = true;
        RecipeIngredient currentSelection = (RecipeIngredient) existingAssociationComboBox.getSelectedItem();
        existingAssociationComboBox.removeAllItems();
        if (recipeIngredientDAO == null) {
            return;
        }
        List<RecipeIngredient> summaries = recipeIngredientDAO.findAll();
        if (!summaries.isEmpty()) {
            for (RecipeIngredient ri : summaries) {
                existingAssociationComboBox.addItem(ri);
            }
        }
        if (currentSelection != null && summaries.contains(currentSelection)) {
            existingAssociationComboBox.setSelectedItem(currentSelection);
        } else {
            existingAssociationComboBox.setSelectedIndex(-1); // Default to placeholder
        }
        if (existingAssociationComboBox.getSelectedIndex() <= 0) {
            clearTargetFieldsAndSelectionState();
        }
        isPopulatingExistingAssoc = false;
        if (existingAssociationComboBox.getSelectedIndex() > 0) { // Not placeholder
            loadSelectedAssociationDetails();
        }
    }

    private void populateTargetRecipeIdComboBox() {
        isPopulatingTargetRecipe = true;
        Recipe currentSelection = (Recipe) targetRecipeIdComboBox.getSelectedItem();
        targetRecipeIdComboBox.removeAllItems();
        if (recipeDAO == null) {
            return;
        }
        List<Recipe> recipes = recipeDAO.findAll();
        if (!recipes.isEmpty()) {
            for (Recipe recipe : recipes) {
                targetRecipeIdComboBox.addItem(recipe);
            }
        }
        if (currentSelection != null && recipes.contains(currentSelection)) {
            targetRecipeIdComboBox.setSelectedItem(currentSelection);
        } else {
            targetRecipeIdComboBox.setSelectedIndex(-1);
        }
        isPopulatingTargetRecipe = false;
    }

    private void populateTargetIngredientIdComboBox() {
        isPopulatingTargetIngredient = true;
        Ingredient currentSelection = (Ingredient) targetIngredientIdComboBox.getSelectedItem();
        targetIngredientIdComboBox.removeAllItems();
        if (ingredientDAO == null) {
            return;
        }
        List<Ingredient> ingredients = ingredientDAO.findAll();
        if (!ingredients.isEmpty()) {
            for (Ingredient ingredient : ingredients) {
                targetIngredientIdComboBox.addItem(ingredient);
            }
        }
        if (currentSelection != null && ingredients.contains(currentSelection)) {
            targetIngredientIdComboBox.setSelectedItem(currentSelection);
        } else {
            targetIngredientIdComboBox.setSelectedIndex(-1);
        }
        isPopulatingTargetIngredient = false;
    }

    private void loadSelectedAssociationDetails() {
        RecipeIngredient recipeIngredient = (RecipeIngredient) existingAssociationComboBox.getSelectedItem();

        if (recipeIngredient != null) {
            quantityField.setText(recipeIngredient.getQuantity());

            for (int i = 0; i < targetRecipeIdComboBox.getItemCount(); i++) {
                Recipe recipe = targetRecipeIdComboBox.getItemAt(i);
                    if (recipe.getId() == recipeIngredient.getRecipeId()) {
                        targetRecipeIdComboBox.setSelectedItem(recipe);
                    }
                    else {
                        targetRecipeIdComboBox.setSelectedIndex(0);
                    }
            }

            for (int i = 0; i < targetIngredientIdComboBox.getItemCount(); i++) {
                Ingredient ingredient = targetIngredientIdComboBox.getItemAt(i);
                if (ingredient.getId() == recipeIngredient.getIngredientId()) {
                    targetIngredientIdComboBox.setSelectedItem(ingredient);
                }
                else {
                    targetIngredientIdComboBox.setSelectedIndex(0);
                }
            }

            quantityField.setText(recipeIngredient.getQuantity());
        } else {
            clearTargetFieldsAndSelectionState();
        }
    }

// In RecipeIngredientPanel.java
    private void clearTargetFieldsAndSelectionState() {
        selectedRecipeIngredient = null;
        // These should now be safe due to the new call order in createUI,
        // as the target combo boxes will have their placeholder items.
        if (targetRecipeIdComboBox.getItemCount() > 0) {
            targetRecipeIdComboBox.setSelectedIndex(0); // Select placeholder "Select Recipe..."
        } else {
            targetRecipeIdComboBox.setSelectedIndex(-1); // Fallback if somehow still empty
        }
        if (targetIngredientIdComboBox.getItemCount() > 0) {
            targetIngredientIdComboBox.setSelectedIndex(0); // Select placeholder "Select Ingredient..."
        } else {
            targetIngredientIdComboBox.setSelectedIndex(-1); // Fallback if somehow still empty
        }
        quantityField.setText("");
    }

    private void assignNewRecipeIngredient() {
        Recipe selectedRecipe = (Recipe) targetRecipeIdComboBox.getSelectedItem();
        Ingredient selectedIngredient = (Ingredient) targetIngredientIdComboBox.getSelectedItem();

        int recipeId = selectedRecipe != null ? selectedRecipe.getId() : -1;
        int ingredientId = selectedIngredient != null ? selectedIngredient.getId() : -1;
        String quantity = quantityField.getText();

        if (recipeId == -1 || ingredientId == -1) {
            outputArea.setText("❗ Please select a valid recipe and ingredient.");
            return;
        }
        if (quantity.trim().isEmpty()) {
            outputArea.setText("❗ Quantity cannot be empty.");
            return;
        }

        if (recipeIngredientDAO == null) {
            outputArea.setText("DAO Error!");
            return;
        }
        boolean success = recipeIngredientDAO.create(new RecipeIngredient(recipeId, ingredientId, quantity));
        if (success) {
            outputArea.setText("✅ New Recipe-Ingredient assignment created!");
            populateExistingAssociationComboBox();
            readAllRecipeIngredients();
        } else {
            outputArea.setText("❌ Failed to create assignment. It might already exist (Recipe+Ingredient pair).");
        }
    }

    private void readAllRecipeIngredients() {
        if (recipeIngredientDAO == null) {
            outputArea.setText("DAO Error!");
            return;
        }
        List<RecipeIngredient> associations = recipeIngredientDAO.findAll();
        if (associations.isEmpty()) {
            outputArea.setText("📂 No recipe-ingredient assignments found.");
        } else {
            outputArea.setText("📂 Recipe-Ingredient Assignments:\n" + associations.stream().map(RecipeIngredient::toString).collect(Collectors.joining("\n")));
        }
    }

    private void updateSelectedRecipeIngredient() {
        if (selectedRecipeIngredient == null) {
            outputArea.setText("❗ Please select an existing assignment from the top dropdown to update.");
            return;
        }

        Recipe selectedRecipe = (Recipe) targetRecipeIdComboBox.getSelectedItem();
        Ingredient selectedIngredient = (Ingredient) targetIngredientIdComboBox.getSelectedItem();

        int newRecipeId = selectedRecipe != null ? selectedRecipe.getId() : -1;
        int newIngredientId = selectedIngredient != null ? selectedIngredient.getId() : -1;
        String newQuantity = quantityField.getText();

        if (newRecipeId == -1 || newIngredientId == -1) {
            outputArea.setText("❗ Please select valid target recipe and ingredient for the update.");
            return;
        }
        if (newQuantity.trim().isEmpty()) {
            outputArea.setText("❗ Quantity cannot be empty for update.");
            return;
        }

        // Optional: Check if there are actual changes before attempting an update
        // This would require fetching the original recipeId, ingredientId, and quantity
        // for `currentSelectedAssociationPKId` and comparing.
        // For now, we proceed with the update call.
        if (recipeIngredientDAO == null) {
            outputArea.setText("DAO Error!");
            return;
        }
        boolean success = recipeIngredientDAO.update(new RecipeIngredient(newRecipeId, newIngredientId, newQuantity));

        if (success) {
            outputArea.setText("✅ Assignment (AssocID: " + selectedRecipeIngredient + ") updated successfully!");
            populateExistingAssociationComboBox(); // Refresh
            readAllRecipeIngredients();
            // After update, the original selected item might have different text, so reset selection
            existingAssociationComboBox.setSelectedIndex(0);
            clearTargetFieldsAndSelectionState();
        } else {
            outputArea.setText("❌ Failed to update assignment (AssocID: " + selectedRecipeIngredient + "). Target Recipe+Ingredient pair might already exist for another entry, or original not found.");
        }
    }

    private void removeSelectedRecipeIngredient() {
        if (selectedRecipeIngredient == null) {
            outputArea.setText("❗ Please select an existing assignment from the top dropdown to remove.");
            return;
        }

        if (recipeIngredientDAO == null) {
            outputArea.setText("DAO Error!");
            return;
        }
        boolean success = recipeIngredientDAO.deleteByKeys(selectedRecipeIngredient);

        if (success) {
            outputArea.setText("🗑️ Selected assignment (AssocID: " + selectedRecipeIngredient + ") removed!");
            populateExistingAssociationComboBox(); // Refresh
            readAllRecipeIngredients();
            existingAssociationComboBox.setSelectedIndex(0);
            clearTargetFieldsAndSelectionState();
        } else {
            outputArea.setText("❌ Failed to remove assignment (AssocID: " + selectedRecipeIngredient + "). It might not exist.");
        }
    }
}
