package jdbc_recipe_db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map; // Import Map

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel; // Import DefaultComboBoxModel
import javax.swing.JButton;
import javax.swing.JComboBox; // Import JComboBox
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent; // Import PopupMenuEvent
import javax.swing.event.PopupMenuListener; // Import PopupMenuListener

import jdbc_recipe_db.databaseaccess.CategoryDAO;

public class CategoryPanel extends JPanel {

    private CategoryDAO categoryDAO;
    // private JTextField categoryIdField; // Remove this
    private JTextField categoryNameField;
    private JTextArea outputArea;
    private JComboBox<String> categoryIdComboBox; // JComboBox for category selection
    private boolean isPopulatingComboBox = false; // Flag to prevent event recursion

    public CategoryPanel() {
        setLayout(new BorderLayout());
        categoryDAO = new CategoryDAO();
        createUI();
    }

    private void createUI() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Only 2 rows needed now
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Category:")); // Changed label
        categoryIdComboBox = new JComboBox<>(); // Initialize JComboBox
        inputPanel.add(categoryIdComboBox);

        inputPanel.add(new JLabel("Category Name:"));
        categoryNameField = new JTextField(20);
        inputPanel.add(categoryNameField);

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
        categoryIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                populateCategoryIdComboBox();
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        // Load details when an item is selected
        categoryIdComboBox.addActionListener(e -> {
            if (!isPopulatingComboBox && categoryIdComboBox.getSelectedItem() != null) {
                loadSelectedCategoryDetails();
            }
        });

        createBtn.addActionListener(e -> createCategory());
        readBtn.addActionListener(e -> readCategories());
        updateBtn.addActionListener(e -> updateCategory());
        deleteBtn.addActionListener(e -> deleteCategory());
        
        // Initial population
        populateCategoryIdComboBox();
    }

    private void populateCategoryIdComboBox() {
        isPopulatingComboBox = true;
        String selectedItemBeforeUpdate = (String) categoryIdComboBox.getSelectedItem();
        categoryIdComboBox.removeAllItems();

        List<String> categorySummaries = categoryDAO.getCategoryIdNameSummaries();
        if (categorySummaries.isEmpty()) {
            outputArea.append("\nNo categories available to select.");
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
        if (categoryIdComboBox.getSelectedIndex() == -1) {
             clearInputFields(false);
        }
        isPopulatingComboBox = false;
    }
    
    private int parseIdFromSelectedItem(String selectedItem) {
        if (selectedItem == null || !selectedItem.startsWith("ID: ")) {
            return -1; 
        }
        try {
            // Example: "ID: 123 - Category Name"
            String idStr = selectedItem.substring(4, selectedItem.indexOf(" - "));
            return Integer.parseInt(idStr.trim());
        } catch (Exception e) {
            outputArea.setText("❗ Error parsing ID from selected item: " + selectedItem);
            e.printStackTrace();
            return -1;
        }
    }

    private void loadSelectedCategoryDetails() {
        String selectedItem = (String) categoryIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            clearInputFields(false); // Only clear name field
            return;
        }
        int categoryId = parseIdFromSelectedItem(selectedItem);
        if (categoryId == -1) {
            clearInputFields(false);
            return;
        }

        Map<String, String> details = categoryDAO.getCategoryDetailsById(categoryId);
        if (details != null) {
            categoryNameField.setText(details.get("name"));
        } else {
            outputArea.setText("❌ Error: Could not load details for Category ID: " + categoryId);
            clearInputFields(false);
        }
    }
    
    private void clearInputFields(boolean clearComboBoxAlso) {
        if (clearComboBoxAlso) {
            categoryIdComboBox.setSelectedIndex(-1);
        }
        categoryNameField.setText("");
    }

    private void createCategory() {
        String name = categoryNameField.getText();
        if (name.trim().isEmpty()) {
            outputArea.setText("❗ Category name cannot be empty.");
            return;
        }
        try {
            boolean success = categoryDAO.createCategory(name);
            if (success) {
                outputArea.setText("✅ Category created: " + name);
                populateCategoryIdComboBox();
                clearInputFields(true);
            } else {
                outputArea.setText("❌ Failed to create category. It might already exist or another error occurred.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ Error processing request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void readCategories() {
        // Uses the DAO's getCategories which returns "ID: id | Name: name"
        List<String> categories = categoryDAO.getCategories();
        outputArea.setText("📂 Categories:\n" + String.join("\n", categories));
    }

    private void updateCategory() {
        String selectedItem = (String) categoryIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a category from the dropdown to update.");
            return;
        }
        int id = parseIdFromSelectedItem(selectedItem);
        if (id == -1) return;

        String name = categoryNameField.getText();
        if (name.trim().isEmpty()) {
            outputArea.setText("❗ Category name cannot be empty for update.");
            return;
        }

        try {
            boolean success = categoryDAO.updateCategory(id, name);
            if (success) {
                outputArea.setText("✅ Category updated: " + name);
                populateCategoryIdComboBox();
                
                // Attempt to re-select the updated item
                String potentiallyUpdatedItemSummary = String.format("ID: %d - %s", id, name);
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) categoryIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(potentiallyUpdatedItemSummary)) {
                        categoryIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No category found with ID: " + id + " or update failed.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ Error processing update request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteCategory() {
        String selectedItem = (String) categoryIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a category from the dropdown to delete.");
            return;
        }
        int id = parseIdFromSelectedItem(selectedItem);
        if (id == -1) return;

        try {
            boolean success = categoryDAO.deleteCategory(id);
            if (success) {
                outputArea.setText("🗑️ Category deleted (ID: " + id + ")");
                populateCategoryIdComboBox();
                clearInputFields(true);
            } else {
                outputArea.setText("❌ No category found with ID: " + id + " or delete failed (it might be in use).");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ An error occurred during deletion: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}