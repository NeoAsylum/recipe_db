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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import recipedb.dao.CategoryDAO;
import recipedb.dao.RecipeCategoryDAO;
import recipedb.dao.RecipeDAO;
import recipedb.model.Category;
import recipedb.model.Recipe;
import recipedb.model.RecipeCategory;

public class RecipeCategoryPanel extends JPanel {

    private final RecipeCategoryDAO recipeCategoryDAO;
    private final RecipeDAO recipeDAO;
    private final CategoryDAO categoryDAO;

    private JComboBox<RecipeCategory> existingAssociationComboBox; // Master dropdown for existing links
    private JComboBox<Recipe> targetRecipeIdComboBox;      // For selecting/displaying recipe part of association
    private JComboBox<Category> targetCategoryIdComboBox;    // For selecting/displaying category part of association

    private JTextArea outputArea;
    private boolean isPopulatingExistingAssociationComboBox = false;
    private boolean isPopulatingTargetRecipeComboBox = false;
    private boolean isPopulatingTargetCategoryComboBox = false;

    // To store the original IDs of the currently selected association for an update operation
    private int currentSelectedAssociationRecipeId = -1;
    private int currentSelectedAssociationCategoryId = -1;


    public RecipeCategoryPanel() {
        setLayout(new BorderLayout());
//        try {
            recipeCategoryDAO = new RecipeCategoryDAO();
            recipeDAO = new RecipeDAO();
            categoryDAO = new CategoryDAO();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "❌ Database connection failed for DAOs!", "Error", JOptionPane.ERROR_MESSAGE);
//            // Disable panel or critical components if DAOs fail to init
//        }
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
        RecipeCategory selectedItemBeforeUpdate = (RecipeCategory) existingAssociationComboBox.getSelectedItem();
        existingAssociationComboBox.removeAllItems();
        if (recipeCategoryDAO == null) return;
        List<RecipeCategory> recipeCategories = recipeCategoryDAO.findAll();
        if (recipeCategories.isEmpty()) {
            existingAssociationComboBox.setSelectedItem("No associations found");
        } else {
            existingAssociationComboBox.setSelectedItem("Select an association to edit/delete..."); // Placeholder
            for (RecipeCategory rc : recipeCategories) {
                existingAssociationComboBox.addItem(rc);
            }
        }
        // Try to restore selection or set to placeholder
        if (selectedItemBeforeUpdate != null && recipeCategories.contains(selectedItemBeforeUpdate)) {
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
        Recipe selectedRecipe = (Recipe) targetRecipeIdComboBox.getSelectedItem();
        targetRecipeIdComboBox.removeAllItems();
        if (recipeDAO == null) return;
        List<Recipe> recipeSummaries = recipeDAO.findAll();
        if (recipeSummaries.isEmpty()) {
            targetRecipeIdComboBox.setSelectedItem("No Recipes available");
        } else {
            targetRecipeIdComboBox.setSelectedItem("Select Recipe..."); // Placeholder
            for (Recipe summary : recipeSummaries) {
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
        Category selectedCategory = (Category) targetCategoryIdComboBox.getSelectedItem();
        targetCategoryIdComboBox.removeAllItems();
        if (categoryDAO == null) return;
        List<Category> categorySummaries = categoryDAO.findAll();
        if (categorySummaries.isEmpty()) {
            targetCategoryIdComboBox.setSelectedItem("No Categories available");
        } else {
            targetCategoryIdComboBox.setSelectedItem("Select Category..."); // Placeholder
            for (Category summary : categorySummaries) {
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

    private void loadSelectedAssociationDetails() {
        RecipeCategory selectedAssociationSummary = (RecipeCategory) existingAssociationComboBox.getSelectedItem();

        if (selectedAssociationSummary != null) {
            currentSelectedAssociationRecipeId = selectedAssociationSummary.getRecipeId();
            currentSelectedAssociationCategoryId = selectedAssociationSummary.getCategoryId();

            // Select in targetRecipeIdComboBox
            boolean recipeFound = false;
            for (int i = 0; i < targetRecipeIdComboBox.getItemCount(); i++) {
                Recipe recipeItem = targetRecipeIdComboBox.getItemAt(i);
                if (recipeItem.getId() == currentSelectedAssociationRecipeId) {
                    targetRecipeIdComboBox.setSelectedIndex(i);
                    recipeFound = true;
                    break;
                }
            }
            if (!recipeFound) targetRecipeIdComboBox.setSelectedIndex(0); // Placeholder

            // Select in targetCategoryIdComboBox
            boolean categoryFound = false;
            for (int i = 0; i < targetCategoryIdComboBox.getItemCount(); i++) {
                Category categoryItem = targetCategoryIdComboBox.getItemAt(i);
                if (categoryItem.getId() == currentSelectedAssociationCategoryId) {
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
        Recipe selectedRecipeItem = (Recipe) targetRecipeIdComboBox.getSelectedItem();
        Category selectedCategoryItem = (Category) targetCategoryIdComboBox.getSelectedItem();

        int recipeId = selectedRecipeItem != null ? selectedRecipeItem.getId() : -1;
        int categoryId = selectedCategoryItem != null ? selectedCategoryItem.getId() : -1;

        if (recipeId == -1 || categoryId == -1) {
            outputArea.setText("❗ Please select a valid recipe and category to assign.");
            return;
        }

        if (recipeCategoryDAO == null) { outputArea.setText("DAO Error!"); return; }
        boolean success = recipeCategoryDAO.create(new RecipeCategory(recipeId, categoryId));
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
        List<RecipeCategory> associations = recipeCategoryDAO.findAll(); // Use the detailed display
        if (associations.isEmpty()) {
            outputArea.setText("📂 No recipe-category associations found.");
        } else {
            outputArea.setText("📂 Recipe-Category Associations:\n" + associations.stream().map(RecipeCategory::toString).collect(Collectors.joining("\n")));
        }
    }

    private void updateSelectedRecipeCategoryAssociation() {
        if (currentSelectedAssociationRecipeId == -1 || currentSelectedAssociationCategoryId == -1) {
            outputArea.setText("❗ Please select an existing association from the top dropdown first.");
            return;
        }

        Recipe selectedNewRecipeItem = (Recipe) targetRecipeIdComboBox.getSelectedItem();
        Category selectedNewCategoryItem = (Category) targetCategoryIdComboBox.getSelectedItem();

        int newRecipeId = selectedNewRecipeItem != null ? selectedNewRecipeItem.getId() : -1;
        int newCategoryId = selectedNewCategoryItem != null ? selectedNewCategoryItem.getId() : -1;

        if (newRecipeId == -1 || newCategoryId == -1) {
            outputArea.setText("❗ Please select valid target recipe and category for the update.");
            return;
        }

        if (currentSelectedAssociationRecipeId == newRecipeId && currentSelectedAssociationCategoryId == newCategoryId) {
            outputArea.setText("ℹ️ No changes detected in recipe or category selection for update.");
            return;
        }

        if (recipeCategoryDAO == null) { outputArea.setText("DAO Error!"); return; }
        boolean success = recipeCategoryDAO.update(new RecipeCategory(newRecipeId, newCategoryId));

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
        boolean success = recipeCategoryDAO.deleteByKeys(new RecipeCategory(
            currentSelectedAssociationRecipeId, currentSelectedAssociationCategoryId)
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
