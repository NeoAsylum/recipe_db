package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import jdbc_recipe_db.databaseaccess.CategoryDAO;
import jdbc_recipe_db.databaseaccess.RecipeCategoryDAO;
import jdbc_recipe_db.databaseaccess.RecipeDAO;

public class RecipeCategoryPanel extends JPanel {

    private RecipeCategoryDAO recipeCategoryDAO;
    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;

    private JComboBox<String> existingAssociationComboBox; // Master dropdown for existing links
    private JComboBox<String> targetRecipeIdComboBox;      // For selecting/displaying recipe part of association
    private JComboBox<String> targetCategoryIdComboBox;    // For selecting/displaying category part of association

    private JTextArea outputArea;
    private boolean isPopulatingExistingAssociationComboBox = false;
    private boolean isPopulatingTargetRecipeComboBox = false;
    private boolean isPopulatingTargetCategoryComboBox = false;
    
    // To store the original IDs of the currently selected association for an update operation
    private int currentSelectedAssociationRecipeId = -1;
    private int currentSelectedAssociationCategoryId = -1;


    public RecipeCategoryPanel() {
        setLayout(new BorderLayout());
        try {
            recipeCategoryDAO = new RecipeCategoryDAO();
            recipeDAO = new RecipeDAO();
            categoryDAO = new CategoryDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database connection failed for DAOs!", "Error", JOptionPane.ERROR_MESSAGE);
            // Disable panel or critical components if DAOs fail to init
        }
        createUI();
    }

    private void createUI() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 rows now
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Existing Association:"));
        existingAssociationComboBox = new JComboBox<>();
        inputPanel.add(existingAssociationComboBox);

        inputPanel.add(new JLabel("Recipe (Target/Current):"));
        targetRecipeIdComboBox = new JComboBox<>();
        inputPanel.add(targetRecipeIdComboBox);

        inputPanel.add(new JLabel("Category (Target/Current):"));
        targetCategoryIdComboBox = new JComboBox<>();
        inputPanel.add(targetCategoryIdComboBox);
        
        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton assignBtn = new JButton("Assign New");
        assignBtn.setPreferredSize(new Dimension(130, 30));
        JButton readBtn = new JButton("Read Associations");
        readBtn.setPreferredSize(new Dimension(160, 30));
        JButton updateSelectedBtn = new JButton("Update Selected");
        updateSelectedBtn.setPreferredSize(new Dimension(150, 30));
        JButton removeSelectedBtn = new JButton("Remove Selected");
        removeSelectedBtn.setPreferredSize(new Dimension(160, 30));

        buttonPanel.add(assignBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(updateSelectedBtn);
        buttonPanel.add(removeSelectedBtn);
        add(buttonPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 50); // Font, color etc. as before
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(40, 40, 40));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Populate JComboBoxes
        existingAssociationComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateExistingAssociationComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        targetRecipeIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateTargetRecipeIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        targetCategoryIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateTargetCategoryIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        
        // Action listener for the master dropdown
        existingAssociationComboBox.addActionListener(e -> {
            if (!isPopulatingExistingAssociationComboBox && existingAssociationComboBox.getSelectedItem() != null) {
                loadSelectedAssociationDetails();
            }
        });

        assignBtn.addActionListener(e -> assignNewRecipeCategory());
        readBtn.addActionListener(e -> readRecipeCategoryAssociations());
        updateSelectedBtn.addActionListener(e -> updateSelectedRecipeCategoryAssociation());
        removeSelectedBtn.addActionListener(e -> removeSelectedRecipeCategoryAssociation());
        
        // Initial population
        populateExistingAssociationComboBox();
        populateTargetRecipeIdComboBox();
        populateTargetCategoryIdComboBox();
    }

    private void populateExistingAssociationComboBox() {
        isPopulatingExistingAssociationComboBox = true;
        String selectedItemBeforeUpdate = (String) existingAssociationComboBox.getSelectedItem();
        existingAssociationComboBox.removeAllItems();
        if (recipeCategoryDAO == null) return;
        List<String> summaries = recipeCategoryDAO.getRecipeCategoryAssociationSummaries();
        if (summaries.isEmpty()) {
            existingAssociationComboBox.addItem("No associations found");
        } else {
            existingAssociationComboBox.addItem("Select an association to edit/delete..."); // Placeholder
            for (String summary : summaries) {
                existingAssociationComboBox.addItem(summary);
            }
        }
        // Try to restore selection or set to placeholder
        if (selectedItemBeforeUpdate != null && summaries.contains(selectedItemBeforeUpdate)) {
            existingAssociationComboBox.setSelectedItem(selectedItemBeforeUpdate);
        } else {
             existingAssociationComboBox.setSelectedIndex(0); // Select placeholder or first item
        }
        if (existingAssociationComboBox.getSelectedIndex() <= 0 || existingAssociationComboBox.getSelectedItem().toString().contains("No associations")) { // If placeholder or no items
            currentSelectedAssociationRecipeId = -1;
            currentSelectedAssociationCategoryId = -1;
            targetRecipeIdComboBox.setSelectedIndex(-1); // Clear target selections
            targetCategoryIdComboBox.setSelectedIndex(-1);
        }
        isPopulatingExistingAssociationComboBox = false;
         // Trigger load for initial selection if any
        if (existingAssociationComboBox.getSelectedIndex() > 0) { // More than placeholder
             loadSelectedAssociationDetails();
        }
    }

    private void populateTargetRecipeIdComboBox() {
        isPopulatingTargetRecipeComboBox = true;
        String selectedRecipe = (String) targetRecipeIdComboBox.getSelectedItem();
        targetRecipeIdComboBox.removeAllItems();
        if (recipeDAO == null) return;
        List<String> recipeSummaries = recipeDAO.getRecipeIdNameSummaries();
        if (recipeSummaries.isEmpty()) {
            targetRecipeIdComboBox.addItem("No Recipes available");
        } else {
            targetRecipeIdComboBox.addItem("Select Recipe..."); // Placeholder
            for (String summary : recipeSummaries) {
                targetRecipeIdComboBox.addItem(summary);
            }
        }
        if (selectedRecipe != null && recipeSummaries.contains(selectedRecipe)) {
            targetRecipeIdComboBox.setSelectedItem(selectedRecipe);
        } else {
            targetRecipeIdComboBox.setSelectedIndex(0); // Default to placeholder
        }
        isPopulatingTargetRecipeComboBox = false;
    }

    private void populateTargetCategoryIdComboBox() {
        isPopulatingTargetCategoryComboBox = true;
        String selectedCategory = (String) targetCategoryIdComboBox.getSelectedItem();
        targetCategoryIdComboBox.removeAllItems();
        if (categoryDAO == null) return;
        List<String> categorySummaries = categoryDAO.getCategoryIdNameSummaries();
        if (categorySummaries.isEmpty()) {
            targetCategoryIdComboBox.addItem("No Categories available");
        } else {
            targetCategoryIdComboBox.addItem("Select Category..."); // Placeholder
            for (String summary : categorySummaries) {
                targetCategoryIdComboBox.addItem(summary);
            }
        }
        if (selectedCategory != null && categorySummaries.contains(selectedCategory)) {
            targetCategoryIdComboBox.setSelectedItem(selectedCategory);
        } else {
            targetCategoryIdComboBox.setSelectedIndex(0); // Default to placeholder
        }
        isPopulatingTargetCategoryComboBox = false;
    }

    // Parses "RID:(\d+) \((.*?)\) --- CID:(\d+) \((.*?)\)"
    private int[] parseAssociationSummary(String summary) {
        if (summary == null || summary.contains("No associations") || summary.contains("Select an association")) {
            return null;
        }
        // Pattern: "RID:recipe_id (Recipe Name) --- CID:category_id (Category Name)"
        Pattern pattern = Pattern.compile("RID:(\\d+) .*? --- CID:(\\d+) .*");
        Matcher matcher = pattern.matcher(summary);
        if (matcher.matches()) {
            try {
                int recipeId = Integer.parseInt(matcher.group(1));
                int categoryId = Integer.parseInt(matcher.group(2));
                return new int[]{recipeId, categoryId};
            } catch (NumberFormatException e) {
                outputArea.setText("Error parsing association summary: " + summary);
                e.printStackTrace();
            }
        }
        return null;
    }
    
    // Parses "ID: id - Name" from target recipe/category dropdowns
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
            currentSelectedAssociationRecipeId = ids[0];
            currentSelectedAssociationCategoryId = ids[1];

            // Select in targetRecipeIdComboBox
            boolean recipeFound = false;
            for (int i = 0; i < targetRecipeIdComboBox.getItemCount(); i++) {
                String recipeItem = targetRecipeIdComboBox.getItemAt(i);
                if (recipeItem.startsWith("ID: " + currentSelectedAssociationRecipeId + " -")) {
                    targetRecipeIdComboBox.setSelectedIndex(i);
                    recipeFound = true;
                    break;
                }
            }
            if (!recipeFound) targetRecipeIdComboBox.setSelectedIndex(0); // Placeholder

            // Select in targetCategoryIdComboBox
            boolean categoryFound = false;
            for (int i = 0; i < targetCategoryIdComboBox.getItemCount(); i++) {
                String categoryItem = targetCategoryIdComboBox.getItemAt(i);
                if (categoryItem.startsWith("ID: " + currentSelectedAssociationCategoryId + " -")) {
                    targetCategoryIdComboBox.setSelectedIndex(i);
                    categoryFound = true;
                    break;
                }
            }
             if (!categoryFound) targetCategoryIdComboBox.setSelectedIndex(0); // Placeholder
        } else {
            currentSelectedAssociationRecipeId = -1;
            currentSelectedAssociationCategoryId = -1;
            targetRecipeIdComboBox.setSelectedIndex(0); // Reset to placeholder
            targetCategoryIdComboBox.setSelectedIndex(0); // Reset to placeholder
        }
    }

    private void assignNewRecipeCategory() {
        String selectedRecipeItem = (String) targetRecipeIdComboBox.getSelectedItem();
        String selectedCategoryItem = (String) targetCategoryIdComboBox.getSelectedItem();

        int recipeId = parseIdFromTargetSelectedItem(selectedRecipeItem, "Recipe");
        int categoryId = parseIdFromTargetSelectedItem(selectedCategoryItem, "Category");

        if (recipeId == -1 || categoryId == -1) {
            outputArea.setText("❗ Please select a valid recipe and category to assign.");
            return;
        }

        if (recipeCategoryDAO == null) { outputArea.setText("DAO Error!"); return; }
        boolean success = recipeCategoryDAO.createRecipeCategory(recipeId, categoryId);
        if (success) {
            outputArea.setText("✅ New association created!");
            populateExistingAssociationComboBox(); // Refresh master list
            readRecipeCategoryAssociations();      // Refresh text area
        } else {
            outputArea.setText("❌ Failed to create association. It might already exist.");
        }
    }

    private void readRecipeCategoryAssociations() {
        if (recipeCategoryDAO == null) { outputArea.setText("DAO Error!"); return; }
        List<String> associations = recipeCategoryDAO.getFullRecipeCategoriesDisplay(); // Use the detailed display
        if (associations.isEmpty()) {
            outputArea.setText("📂 No recipe-category associations found.");
        } else {
            outputArea.setText("📂 Recipe-Category Associations:\n" + String.join("\n", associations));
        }
    }

    private void updateSelectedRecipeCategoryAssociation() {
        if (currentSelectedAssociationRecipeId == -1 || currentSelectedAssociationCategoryId == -1) {
            outputArea.setText("❗ Please select an existing association from the top dropdown first.");
            return;
        }

        String selectedNewRecipeItem = (String) targetRecipeIdComboBox.getSelectedItem();
        String selectedNewCategoryItem = (String) targetCategoryIdComboBox.getSelectedItem();

        int newRecipeId = parseIdFromTargetSelectedItem(selectedNewRecipeItem, "Recipe");
        int newCategoryId = parseIdFromTargetSelectedItem(selectedNewCategoryItem, "Category");

        if (newRecipeId == -1 || newCategoryId == -1) {
            outputArea.setText("❗ Please select valid target recipe and category for the update.");
            return;
        }
        
        if (currentSelectedAssociationRecipeId == newRecipeId && currentSelectedAssociationCategoryId == newCategoryId) {
            outputArea.setText("ℹ️ No changes detected in recipe or category selection for update.");
            return;
        }

        if (recipeCategoryDAO == null) { outputArea.setText("DAO Error!"); return; }
        boolean success = recipeCategoryDAO.updateRecipeCategoryAssociation(
            currentSelectedAssociationRecipeId, currentSelectedAssociationCategoryId,
            newRecipeId, newCategoryId
        );

        if (success) {
            outputArea.setText("✅ Association updated successfully!");
            populateExistingAssociationComboBox(); // Refresh
            readRecipeCategoryAssociations();
            // After update, the original selected item might be gone, so reset
            existingAssociationComboBox.setSelectedIndex(0); // Back to placeholder
            currentSelectedAssociationRecipeId = -1;
            currentSelectedAssociationCategoryId = -1;
            targetRecipeIdComboBox.setSelectedIndex(0);
            targetCategoryIdComboBox.setSelectedIndex(0);
        } else {
            outputArea.setText("❌ Failed to update association. Target might already exist or original not found.");
        }
    }

    private void removeSelectedRecipeCategoryAssociation() {
        if (currentSelectedAssociationRecipeId == -1 || currentSelectedAssociationCategoryId == -1) {
            outputArea.setText("❗ Please select an existing association from the top dropdown to remove.");
            return;
        }

        if (recipeCategoryDAO == null) { outputArea.setText("DAO Error!"); return; }
        boolean success = recipeCategoryDAO.deleteRecipeCategory(
            currentSelectedAssociationRecipeId, currentSelectedAssociationCategoryId
        );

        if (success) {
            outputArea.setText("🗑️ Selected association removed!");
            populateExistingAssociationComboBox(); // Refresh
            readRecipeCategoryAssociations();
            existingAssociationComboBox.setSelectedIndex(0); // Back to placeholder
            currentSelectedAssociationRecipeId = -1;
            currentSelectedAssociationCategoryId = -1;
            targetRecipeIdComboBox.setSelectedIndex(0);
            targetCategoryIdComboBox.setSelectedIndex(0);
        } else {
            outputArea.setText("❌ Failed to remove association. It might not exist.");
        }
    }
}