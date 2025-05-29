package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton; // Import DefaultComboBoxModel
import javax.swing.JComboBox;
import javax.swing.JLabel; // Import JComboBox
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener; // Import PopupMenuEvent

import jdbc_recipe_db.databaseaccess.CategoryDAO; // Import PopupMenuListener
import jdbc_recipe_db.databaseaccess.RecipeCategoryDAO;
import jdbc_recipe_db.databaseaccess.RecipeDAO;     // To populate Recipe dropdown

public class RecipeCategoryPanel extends JPanel {

    private RecipeCategoryDAO recipeCategoryDAO;
    private RecipeDAO recipeDAO;         // For Recipe dropdown data
    private CategoryDAO categoryDAO;     // For Category dropdown data

    // private JTextField recipeIdField, categoryIdField; // Remove these
    private JComboBox<String> recipeIdComboBox;
    private JComboBox<String> categoryIdComboBox;

    private JTextArea outputArea;
    private boolean isPopulatingRecipeComboBox = false;
    private boolean isPopulatingCategoryComboBox = false;
    private boolean isPopulatingOldCategoryComboBox = false;


    public RecipeCategoryPanel() {
        setLayout(new BorderLayout());
        try {
            recipeCategoryDAO = new RecipeCategoryDAO();
            recipeDAO = new RecipeDAO();         // Initialize RecipeDAO
            categoryDAO = new CategoryDAO();     // Initialize CategoryDAO
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database connection failed for DAOs!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        createUI();
    }

    private void createUI() {
        // Input Panel - increased rows for update scenario
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); 
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Recipe:"));
        recipeIdComboBox = new JComboBox<>();
        inputPanel.add(recipeIdComboBox);

        inputPanel.add(new JLabel("Select New/Target Category:"));
        categoryIdComboBox = new JComboBox<>();
        inputPanel.add(categoryIdComboBox);
        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBtn = new JButton("Assign Category"); // Renamed for clarity
        createBtn.setPreferredSize(new Dimension(140, 30));
        JButton readBtn = new JButton("Read Associations");
        readBtn.setPreferredSize(new Dimension(150, 30));
        JButton deleteBtn = new JButton("Remove Category"); // Renamed
        deleteBtn.setPreferredSize(new Dimension(150, 30));

        buttonPanel.add(createBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false); // etc.
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(40, 40, 40));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Populate JComboBoxes when they are about to become visible
        recipeIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateRecipeIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        categoryIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateCategoryIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        
        // No action listener to load details, as these are just IDs for association
        // If you wanted to show Recipe Name / Category Name next to dropdowns, you could add that.

        createBtn.addActionListener(e -> createRecipeCategory());
        readBtn.addActionListener(e -> readRecipeCategories());
        deleteBtn.addActionListener(e -> deleteRecipeCategory());
        
        // Initial population
        populateRecipeIdComboBox();
        populateCategoryIdComboBox();
    }

    private void populateRecipeIdComboBox() {
        isPopulatingRecipeComboBox = true;
        String selectedItemBeforeUpdate = (String) recipeIdComboBox.getSelectedItem();
        recipeIdComboBox.removeAllItems();
        if (recipeDAO == null) return; // Guard against null DAO
        List<String> recipeSummaries = recipeDAO.getRecipeIdNameSummaries();
        if (recipeSummaries.isEmpty()) {
            recipeIdComboBox.addItem("No Recipes available");
        } else {
            for (String summary : recipeSummaries) {
                recipeIdComboBox.addItem(summary);
            }
            if (selectedItemBeforeUpdate != null && recipeSummaries.contains(selectedItemBeforeUpdate)) {
                recipeIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!recipeSummaries.isEmpty()){
                recipeIdComboBox.setSelectedIndex(-1); 
            }
        }
        isPopulatingRecipeComboBox = false;
    }

    private void populateCategoryIdComboBox() {
        isPopulatingCategoryComboBox = true;
        String selectedItemBeforeUpdate = (String) categoryIdComboBox.getSelectedItem();
        categoryIdComboBox.removeAllItems();
        if (categoryDAO == null) return; // Guard against null DAO
        List<String> categorySummaries = categoryDAO.getCategoryIdNameSummaries();
        if (categorySummaries.isEmpty()) {
            categoryIdComboBox.addItem("No Categories available");
        } else {
            for (String summary : categorySummaries) {
                categoryIdComboBox.addItem(summary);
            }
            if (selectedItemBeforeUpdate != null && categorySummaries.contains(selectedItemBeforeUpdate)) {
                categoryIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!categorySummaries.isEmpty()){
                categoryIdComboBox.setSelectedIndex(-1);
            }
        }
        isPopulatingCategoryComboBox = false;
    }
    
    private int parseIdFromSelectedItem(String selectedItem, String itemTypeForErrorMsg) {
        if (selectedItem == null || !selectedItem.startsWith("ID: ") || selectedItem.contains("No " + itemTypeForErrorMsg)) {
            return -1; 
        }
        try {
            String idStr = selectedItem.substring(4, selectedItem.indexOf(" - "));
            return Integer.parseInt(idStr.trim());
        } catch (Exception e) {
            outputArea.setText("❗ Error parsing ID from selected " + itemTypeForErrorMsg + ": " + selectedItem);
            e.printStackTrace();
            return -1;
        }
    }

    private void createRecipeCategory() {
        String selectedRecipeItem = (String) recipeIdComboBox.getSelectedItem();
        String selectedCategoryItem = (String) categoryIdComboBox.getSelectedItem();

        if (selectedRecipeItem == null || selectedCategoryItem == null || 
            selectedRecipeItem.contains("No Recipes") || selectedCategoryItem.contains("No Categories")) {
            outputArea.setText("❗ Please select both a recipe and a category.");
            return;
        }

        int recipeId = parseIdFromSelectedItem(selectedRecipeItem, "Recipe");
        int categoryId = parseIdFromSelectedItem(selectedCategoryItem, "Category");

        if (recipeId == -1 || categoryId == -1) {
            outputArea.setText("❗ Invalid recipe or category selection.");
            return;
        }

        try {
            if (recipeCategoryDAO == null) {
                 outputArea.setText("❌ RecipeCategoryDAO not initialized!"); return;
            }
            boolean success = recipeCategoryDAO.createRecipeCategory(recipeId, categoryId);
            if (success) {
                outputArea.setText("✅ Recipe successfully assigned to category!");
                readRecipeCategories(); // Refresh the list of associations
            } else {
                 outputArea.setText("❌ Failed to assign recipe to category. Association might already exist or an error occurred.");
            }
        } catch (Exception ex) { // Catch broader exceptions from DAO
            outputArea.setText("❗ Error creating recipe-category link: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void readRecipeCategories() {
        if (recipeCategoryDAO == null) {
             outputArea.setText("❌ RecipeCategoryDAO not initialized!"); return;
        }
        List<String> recipeCategories = recipeCategoryDAO.getRecipeCategories();
        if (recipeCategories.isEmpty()){
            outputArea.setText("📂 No recipe-category associations found.");
        } else {
            outputArea.setText("📂 Recipe-Category Associations:\n" + String.join("\n", recipeCategories));
        }
    }

    private void deleteRecipeCategory() {
        String selectedRecipeItem = (String) recipeIdComboBox.getSelectedItem();
        String selectedCategoryItem = (String) categoryIdComboBox.getSelectedItem();

        if (selectedRecipeItem == null || selectedCategoryItem == null ||
            selectedRecipeItem.contains("No Recipes") || selectedCategoryItem.contains("No Categories")) {
            outputArea.setText("❗ Please select both a recipe and a category to remove the association.");
            return;
        }

        int recipeId = parseIdFromSelectedItem(selectedRecipeItem, "Recipe");
        int categoryId = parseIdFromSelectedItem(selectedCategoryItem, "Category");

        if (recipeId == -1 || categoryId == -1) {
            outputArea.setText("❗ Invalid recipe or category selection for deletion.");
            return;
        }

        try {
            if (recipeCategoryDAO == null) {
                 outputArea.setText("❌ RecipeCategoryDAO not initialized!"); return;
            }
            boolean success = recipeCategoryDAO.deleteRecipeCategory(recipeId, categoryId);
            if (success) {
                outputArea.setText("🗑️ Recipe removed from category!");
                readRecipeCategories(); // Refresh
            } else {
                outputArea.setText("❌ Failed to remove recipe from category. Association may not exist or an error occurred.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ Error deleting recipe-category link: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}