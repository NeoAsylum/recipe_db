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

import recipedb.dao.CategoryDAO;
import recipedb.model.Category;

public class CategoryPanel extends JPanel {

    private final CategoryDAO categoryDAO;
    private JTextField categoryNameField;
    private JTextArea outputArea;
    private JComboBox<Category> categoryIdComboBox; // JComboBox for category selection
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
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
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
        Category selectedItemBeforeUpdate = (Category) categoryIdComboBox.getSelectedItem();
        categoryIdComboBox.removeAllItems();

        List<Category> categorySummaries = categoryDAO.findAll();
        if (categorySummaries.isEmpty()) {
            outputArea.append("\nNo categories available to select.");
        } else {
            for (Category summary : categorySummaries) {
                categoryIdComboBox.addItem(summary);
            }
            if (selectedItemBeforeUpdate != null && categorySummaries.contains(selectedItemBeforeUpdate)) {
                categoryIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!categorySummaries.isEmpty()) {
                categoryIdComboBox.setSelectedIndex(-1);
            }
        }
        if (categoryIdComboBox.getSelectedIndex() == -1) {
            clearInputFields(false);
        }
        isPopulatingComboBox = false;
    }

    private void loadSelectedCategoryDetails() {
        Category selectedItem = (Category) categoryIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            clearInputFields(false); // Only clear name field
            return;
        }

        Category category = categoryDAO.findById(selectedItem.getId());
        if (category != null) {
            categoryNameField.setText(category.getName());
        } else {
            outputArea.setText("❌ Error: Could not load details for Category ID: " + selectedItem.getId());
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
            Category category = new Category(-1, name);
            boolean success = categoryDAO.create(category);
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
        List<Category> categories = categoryDAO.findAll();
        outputArea.setText("📂 Categories:\n" + categories.stream().map(Category::toString).collect(Collectors.joining("\n")));
    }

    private void updateCategory() {
        Category selectedItem = (Category) categoryIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a category from the dropdown to update.");
            return;
        }

        String name = categoryNameField.getText();
        if (name.trim().isEmpty()) {
            outputArea.setText("❗ Category name cannot be empty for update.");
            return;
        }

        try {
            boolean success = categoryDAO.update(new Category(selectedItem.getId(), name));
            if (success) {
                outputArea.setText("✅ Category updated: " + name);
                populateCategoryIdComboBox();

                // Attempt to re-select the updated item
                String potentiallyUpdatedItemSummary = String.format("ID: %d - %s", selectedItem.getId(), name);
                DefaultComboBoxModel<Category> model = (DefaultComboBoxModel<Category>) categoryIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(potentiallyUpdatedItemSummary)) {
                        categoryIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No category found with ID: " + selectedItem.getId() + " or update failed.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ Error processing update request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteCategory() {
        Category selectedItem = (Category) categoryIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a category from the dropdown to delete.");
            return;
        }

        try {
            boolean success = categoryDAO.deleteById(selectedItem.getId());
            if (success) {
                outputArea.setText("🗑️ Category deleted (ID: " + selectedItem.getId() + ")");
                populateCategoryIdComboBox();
                clearInputFields(true);
            } else {
                outputArea.setText("❌ No category found with ID: " + selectedItem.getId() + " or delete failed (it might be in use).");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ An error occurred during deletion: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
