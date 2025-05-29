package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List; // Make sure this is not needed if DAO handles it
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.event.PopupMenuListener; // Keep for quantityField

import jdbc_recipe_db.databaseaccess.IngredientDAO;
import jdbc_recipe_db.databaseaccess.RecipeDAO;
import jdbc_recipe_db.databaseaccess.RecipeIngredientDAO; // For Ingredient dropdown

public class RecipeIngredientPanel extends JPanel {

    private RecipeIngredientDAO recipeIngredientDAO;
    private RecipeDAO recipeDAO;
    private IngredientDAO ingredientDAO;

    private JComboBox<String> existingAssociationComboBox;
    private JComboBox<String> targetRecipeIdComboBox;
    private JComboBox<String> targetIngredientIdComboBox;
    private JTextField quantityField;

    private JTextArea outputArea;

    private boolean isPopulatingExistingAssoc = false;
    private boolean isPopulatingTargetRecipe = false;
    private boolean isPopulatingTargetIngredient = false;

    // To store the primary ID of the currently selected RecipeIngredient association
    private int currentSelectedAssociationPKId = -1;

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
        String currentSelection = (String) existingAssociationComboBox.getSelectedItem();
        existingAssociationComboBox.removeAllItems();
        if (recipeIngredientDAO == null) {
            return;
        }
        List<String> summaries = recipeIngredientDAO.getRecipeIngredientAssociationSummaries();
        existingAssociationComboBox.addItem("Select an assignment to edit/delete..."); // Placeholder
        if (summaries.isEmpty()) {
            // existingAssociationComboBox.addItem("No assignments found"); // Already handled by placeholder
        } else {
            for (String summary : summaries) {
                existingAssociationComboBox.addItem(summary);
            }
        }
        if (currentSelection != null && summaries.contains(currentSelection)) {
            existingAssociationComboBox.setSelectedItem(currentSelection);
        } else {
            existingAssociationComboBox.setSelectedIndex(0); // Default to placeholder
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
        String currentSelection = (String) targetRecipeIdComboBox.getSelectedItem();
        targetRecipeIdComboBox.removeAllItems();
        if (recipeDAO == null) {
            return;
        }
        List<String> recipeSummaries = recipeDAO.getRecipeIdNameSummaries();
        targetRecipeIdComboBox.addItem("Select Recipe..."); // Placeholder
        if (!recipeSummaries.isEmpty()) {
            for (String summary : recipeSummaries) {
                targetRecipeIdComboBox.addItem(summary);
            }
        }
        if (currentSelection != null && recipeSummaries.contains(currentSelection)) {
            targetRecipeIdComboBox.setSelectedItem(currentSelection);
        } else {
            targetRecipeIdComboBox.setSelectedIndex(0);
        }
        isPopulatingTargetRecipe = false;
    }

    private void populateTargetIngredientIdComboBox() {
        isPopulatingTargetIngredient = true;
        String currentSelection = (String) targetIngredientIdComboBox.getSelectedItem();
        targetIngredientIdComboBox.removeAllItems();
        if (ingredientDAO == null) {
            return;
        }
        List<String> ingredientSummaries = ingredientDAO.getIngredientIdNameSummaries();
        targetIngredientIdComboBox.addItem("Select Ingredient..."); // Placeholder
        if (!ingredientSummaries.isEmpty()) {
            for (String summary : ingredientSummaries) {
                targetIngredientIdComboBox.addItem(summary);
            }
        }
        if (currentSelection != null && ingredientSummaries.contains(currentSelection)) {
            targetIngredientIdComboBox.setSelectedItem(currentSelection);
        } else {
            targetIngredientIdComboBox.setSelectedIndex(0);
        }
        isPopulatingTargetIngredient = false;
    }

    // Parses "AssocID:id --- Recipe:Name (RID:recipeId) --- Ing:Name (IID:ingId) --- Qty:qty"
    // Returns {assocId, recipeId, ingredientId} or null
    private int[] parseAssociationSummary(String summary) {
        if (summary == null || summary.contains("No assignments") || summary.contains("Select an assignment")) {
            return null;
        }
        Pattern pattern = Pattern.compile("AssocID:(\\d+) --- Recipe:.*?\\(RID:(\\d+)\\) --- Ing:.*?\\(IID:(\\d+)\\) --- Qty:(.*)");
        Matcher matcher = pattern.matcher(summary);
        if (matcher.matches()) {
            try {
                int assocId = Integer.parseInt(matcher.group(1));
                int recipeId = Integer.parseInt(matcher.group(2));
                int ingredientId = Integer.parseInt(matcher.group(3));
                // String quantity = matcher.group(4); // We'll fetch quantity separately for precision
                return new int[]{assocId, recipeId, ingredientId};
            } catch (NumberFormatException e) {
                outputArea.setText("Error parsing association summary: " + summary);
                e.printStackTrace();
            }
        }
        return null;
    }

    // Parses "ID: id - Name" from target recipe/ingredient dropdowns
    private int parseIdFromTargetSelectedItem(String selectedItem, String itemTypeForErrorMsg) {
        if (selectedItem == null || !selectedItem.startsWith("ID: ") || selectedItem.contains("No ") || selectedItem.contains("Select ")) {
            return -1;
        }
        try {
            String idStr = selectedItem.substring(4, selectedItem.indexOf(" - "));
            return Integer.parseInt(idStr.trim());
        } catch (Exception e) {
            outputArea.setText("❗ Error parsing ID from selected target " + itemTypeForErrorMsg + ": " + selectedItem);
            e.printStackTrace();
            return -1;
        }
    }

    private void loadSelectedAssociationDetails() {
        String selectedAssociationSummary = (String) existingAssociationComboBox.getSelectedItem();
        int[] ids = parseAssociationSummary(selectedAssociationSummary);

        if (ids != null) {
            currentSelectedAssociationPKId = ids[0]; // This is RecipeIngredient.id
            int recipeIdToSelect = ids[1];
            int ingredientIdToSelect = ids[2];

            // Fetch the quantity using the association's primary key
            Map<String, String> details = recipeIngredientDAO.getRecipeIngredientDetailsByAssociationId(currentSelectedAssociationPKId);
            if (details != null) {
                quantityField.setText(details.get("quantity"));
            } else {
                quantityField.setText(""); // Clear if details not found
            }

            // Select in targetRecipeIdComboBox
            selectItemInComboBox(targetRecipeIdComboBox, "ID: " + recipeIdToSelect + " -");
            // Select in targetIngredientIdComboBox
            selectItemInComboBox(targetIngredientIdComboBox, "ID: " + ingredientIdToSelect + " -");

        } else {
            clearTargetFieldsAndSelectionState();
        }
    }

    private void selectItemInComboBox(JComboBox<String> comboBox, String startsWithPrefix) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).startsWith(startsWithPrefix)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.setSelectedIndex(0); // Default to placeholder if not found
    }

// In RecipeIngredientPanel.java
    private void clearTargetFieldsAndSelectionState() {
        currentSelectedAssociationPKId = -1;
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
        int recipeId = parseIdFromTargetSelectedItem((String) targetRecipeIdComboBox.getSelectedItem(), "Recipe");
        int ingredientId = parseIdFromTargetSelectedItem((String) targetIngredientIdComboBox.getSelectedItem(), "Ingredient");
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
        boolean success = recipeIngredientDAO.createRecipeIngredient(recipeId, ingredientId, quantity);
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
        List<String> associations = recipeIngredientDAO.getRecipeIngredientsForDisplay();
        if (associations.isEmpty()) {
            outputArea.setText("📂 No recipe-ingredient assignments found.");
        } else {
            outputArea.setText("📂 Recipe-Ingredient Assignments:\n" + String.join("\n", associations));
        }
    }

    private void updateSelectedRecipeIngredient() {
        if (currentSelectedAssociationPKId == -1) {
            outputArea.setText("❗ Please select an existing assignment from the top dropdown to update.");
            return;
        }

        int newRecipeId = parseIdFromTargetSelectedItem((String) targetRecipeIdComboBox.getSelectedItem(), "Recipe");
        int newIngredientId = parseIdFromTargetSelectedItem((String) targetIngredientIdComboBox.getSelectedItem(), "Ingredient");
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
        boolean success = recipeIngredientDAO.updateRecipeIngredient(
                currentSelectedAssociationPKId, newRecipeId, newIngredientId, newQuantity
        );

        if (success) {
            outputArea.setText("✅ Assignment (AssocID: " + currentSelectedAssociationPKId + ") updated successfully!");
            populateExistingAssociationComboBox(); // Refresh
            readAllRecipeIngredients();
            // After update, the original selected item might have different text, so reset selection
            existingAssociationComboBox.setSelectedIndex(0);
            clearTargetFieldsAndSelectionState();
        } else {
            outputArea.setText("❌ Failed to update assignment (AssocID: " + currentSelectedAssociationPKId + "). Target Recipe+Ingredient pair might already exist for another entry, or original not found.");
        }
    }

    private void removeSelectedRecipeIngredient() {
        if (currentSelectedAssociationPKId == -1) {
            outputArea.setText("❗ Please select an existing assignment from the top dropdown to remove.");
            return;
        }

        if (recipeIngredientDAO == null) {
            outputArea.setText("DAO Error!");
            return;
        }
        boolean success = recipeIngredientDAO.deleteRecipeIngredient(currentSelectedAssociationPKId);

        if (success) {
            outputArea.setText("🗑️ Selected assignment (AssocID: " + currentSelectedAssociationPKId + ") removed!");
            populateExistingAssociationComboBox(); // Refresh
            readAllRecipeIngredients();
            existingAssociationComboBox.setSelectedIndex(0);
            clearTargetFieldsAndSelectionState();
        } else {
            outputArea.setText("❌ Failed to remove assignment (AssocID: " + currentSelectedAssociationPKId + "). It might not exist.");
        }
    }
}
