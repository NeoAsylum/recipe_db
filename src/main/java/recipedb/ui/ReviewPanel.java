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

import recipedb.dao.ReviewDAO;
import recipedb.dao.UserDAO;
import recipedb.dao.RecipeDAO;
import recipedb.model.Category;
import recipedb.model.Recipe;
import recipedb.model.RecipeCategory;
import recipedb.model.Review;
import recipedb.model.User;


public class ReviewPanel extends JPanel {

    private final ReviewDAO revieweDAO = new ReviewDAO();
    private final RecipeDAO recipeDAO = new RecipeDAO();
    private final UserDAO userDAO = new UserDAO();
    private JTextField messageField;
    private JTextArea outputArea;
    private JComboBox<Review> reviewIdComboBox;
    private JComboBox<Recipe> recipeIdComboBox;
    private JComboBox<User> userIdComboBox;
    private boolean isPopulatingReviewComboBox = false;
    private boolean isPopulatingRecipeComboBox = false;
    private boolean isPopulatingUserComboBox = false; 
    
    private int currentSelectedRecipeID = -1;
    private int currentSelectedUserId = -1;
    
    // Sotring the selection of recepie and user
    private int recipeID;
    private int userID;
    
    public ReviewPanel() {
    	setLayout(new BorderLayout());
        createUI();
	}
    
    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select Review:"));
        reviewIdComboBox = new JComboBox<>();
        inputPanel.add(reviewIdComboBox);
        
        inputPanel.add(new JLabel("Select Recipe:"));
        recipeIdComboBox = new JComboBox<>();
        inputPanel.add(recipeIdComboBox);

        inputPanel.add(new JLabel("Select User:"));
        userIdComboBox = new JComboBox<>();
        inputPanel.add(userIdComboBox);

        inputPanel.add(new JLabel("Message:"));
        messageField = new JTextField(20);
        inputPanel.add(messageField);

//        inputPanel.add(new JLabel("Description:"));
//        recipeDescField = new JTextField(20);
//        inputPanel.add(recipeDescField);
//
//        inputPanel.add(new JLabel("Instructions:"));
//        recipeInstField = new JTextField(20);
//        inputPanel.add(recipeInstField);
//
//        inputPanel.add(new JLabel("Cook time:"));
//        recipeCookTimeField = new JTextField(20);
//        inputPanel.add(recipeCookTimeField);
//
//        inputPanel.add(new JLabel("Prep time:"));
//        recipePrepTimeField = new JTextField(20);
//        inputPanel.add(recipePrepTimeField);

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
        
        // Populate Review ComboBox when it's about to become visible
        reviewIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateReviewIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }
            @Override public void popupMenuCanceled(PopupMenuEvent e) { }
        });
        
	    // Information of the Recipe being reviewed
	    reviewIdComboBox.addActionListener(e -> {
	        // Ensure this doesn't fire due to programmatic changes during population
	        if (!isPopulatingReviewComboBox && reviewIdComboBox.getSelectedItem() != null) { loadSelectedReviewDetails(); }
	    });
        
        // Populate Recipe ComboBox when it's about to become visible
        recipeIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateRecipeIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }
            @Override public void popupMenuCanceled(PopupMenuEvent e) { }
        });
        
        
        // Information of the Recipe being reviewed
        recipeIdComboBox.addActionListener(e -> {
            // Ensure this doesn't fire due to programmatic changes during population
            if (!isPopulatingRecipeComboBox && recipeIdComboBox.getSelectedItem() != null) {
                selectRecipe();
            }
        });       

        
        // Populate user ComboBox when it's about to become visible
        userIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { populateUserIdComboBox(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }
            @Override public void popupMenuCanceled(PopupMenuEvent e) { }
        });
        
        // Information about the user reviewing the recipe
        userIdComboBox.addActionListener(e -> {
            // Ensure this doesn't fire due to programmatic changes during population
            if (!isPopulatingUserComboBox && userIdComboBox.getSelectedItem() != null) {
                selectUser();
            }
        });        

        // Button Actions
        createBtn.addActionListener(e -> createReview());
        readBtn.addActionListener(e -> readReviews());
        updateBtn.addActionListener(e -> updateReview());
        deleteBtn.addActionListener(e -> deleteReview());
    }
    
    private void loadSelectedReviewDetails() {
        Review selectedAssociationSummary = (Review) reviewIdComboBox.getSelectedItem();
        populateRecipeIdComboBox();
        populateUserIdComboBox();
        
        if (selectedAssociationSummary != null) {
            currentSelectedRecipeID = selectedAssociationSummary.getRecipe();
            currentSelectedUserId = selectedAssociationSummary.getUser();

            // Select in targetRecipeIdComboBox
            boolean recipeFound = false;
            
            
            for (int i = 0; i < recipeIdComboBox.getItemCount(); i++) {
                Recipe recipeItem = recipeIdComboBox.getItemAt(i);
                if (recipeItem.getId() == currentSelectedRecipeID) {
                	recipeIdComboBox.setSelectedIndex(i);
                    recipeFound = true;
                    break;
                }
            }
            if (!recipeFound) recipeIdComboBox.setSelectedIndex(0); // Placeholder

            // Select in targetCategoryIdComboBox
            boolean userFound = false;
            for (int i = 0; i < userIdComboBox.getItemCount(); i++) {
                User userItem = userIdComboBox.getItemAt(i);
                if (userItem.getId() == currentSelectedUserId) {
                    userIdComboBox.setSelectedIndex(i);
                    userFound = true;
                    break;
                }
            }            
            if (!userFound) userIdComboBox.setSelectedIndex(0); // Placeholder
            
        } else {
            currentSelectedRecipeID = -1;
            currentSelectedUserId = -1;
            recipeIdComboBox.setSelectedIndex(0); // Reset to placeholder
            userIdComboBox.setSelectedIndex(0); // Reset to placeholder
        }
        
        // Populating the message field
        messageField.setText(selectedAssociationSummary.getMessage());
    }
    
    
    
    private void selectRecipe() {
        Recipe recipe = (Recipe) recipeIdComboBox.getSelectedItem();
        if (recipe == null) {
            clearInputFields(false);
        } else {
            recipeID = recipe.getId();
        }
    }
    
    private void selectUser() {
        User user = (User) userIdComboBox.getSelectedItem();
        if (user == null) {
            clearInputFields(false);
        } else {
            userID = user.getId();
        }
    }

    private void populateReviewIdComboBox() {
    	isPopulatingReviewComboBox = true; // Set flag
        Review selectedItemBeforeUpdate = (Review) reviewIdComboBox.getSelectedItem(); // Preserve selection
        reviewIdComboBox.removeAllItems();
        List<Review> reviewSummaries = revieweDAO.findAll();
        if (reviewSummaries.isEmpty()) {
            outputArea.append("\nNo recipes available to select.");
        } else {
            for (Review summary : reviewSummaries) {
                reviewIdComboBox.addItem(summary);
            }
            // Try to restore previous selection
            if (selectedItemBeforeUpdate != null && reviewSummaries.contains(selectedItemBeforeUpdate)) {
                reviewIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!reviewSummaries.isEmpty()) {
            	reviewIdComboBox.setSelectedIndex(-1); // Or 0 for first item
            }
        }
        if (reviewIdComboBox.getSelectedIndex() == -1) {
            clearInputFields(false); // Clear fields if no selection (or after population)
        }
        isPopulatingReviewComboBox = false; // Reset flag
    }

    private void populateRecipeIdComboBox() {
    	isPopulatingRecipeComboBox = true; // Set flag
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
        isPopulatingRecipeComboBox = false; // Reset flag
    }
    
    private void populateUserIdComboBox() {
    	isPopulatingUserComboBox = true; // Set flag
        User selectedItemBeforeUpdate = (User) userIdComboBox.getSelectedItem(); // Preserve selection
        userIdComboBox.removeAllItems();
        List<User> userSummaries = userDAO.findAll();
        if (userSummaries.isEmpty()) {
            outputArea.append("\nNo recipes available to select.");
        } else {
            for (User summary : userSummaries) {
                userIdComboBox.addItem(summary);
            }
            // Try to restore previous selection
            if (selectedItemBeforeUpdate != null && userSummaries.contains(selectedItemBeforeUpdate)) {
                userIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!userSummaries.isEmpty()) {
            	userIdComboBox.setSelectedIndex(-1); // Or 0 for first item
            }
        }
        if (userIdComboBox.getSelectedIndex() == -1) {
            clearInputFields(false); // Clear fields if no selection (or after population)
        }
        isPopulatingUserComboBox = false; // Reset flag
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
        messageField.setText("");
    }

    private void createReview() {
        int recipe = recipeID;
        int user = userID;
        String message = messageField.getText();

        boolean success = revieweDAO.create(new Review(-1, recipe, user, message));
        outputArea.setText(success ? "✅ Review added: " + message : "❌ Failed to add recipe.");
    }
    
    private void readReviews() {
        List<Review> reviews = revieweDAO.findAll();
        outputArea.setText("📜 Reviews:\n" + reviews.stream().map(Review::toDetailedString).collect(Collectors.joining("\n")));
    }
    
    private void updateReview() {
        Review selectedItem = (Review) reviewIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a review from the dropdown to update.");
            return;
        }

        try {
        	Recipe recipe = (Recipe) recipeIdComboBox.getSelectedItem();
        	if (recipe == null) { outputArea.setText("Recipe cannot be null"); return;} // Catching recipe null error
        	User user = (User) userIdComboBox.getSelectedItem();
        	if (user == null) { outputArea.setText("user cannot be null"); return;} // Catching user null error
        	
        	int recipeId = recipe.getId();
        	int userId = user.getId();
        	String message = messageField.getText();

            if (message.trim().isEmpty()) {
                outputArea.setText("❗ Recipe ID, User ID, and Message cannot be empty!.");
                return;
            }
//            

            // Ensure your DAO's updateRecipe method expects prepTime then cookTime
            boolean success = revieweDAO.update(new Review(selectedItem.getId(), recipeId, userId, message));
            if (success) {
                outputArea.setText("✅ Review updated: " + message);
                // String oldSelection = (String) recipeIdComboBox.getSelectedItem(); // Not needed if we are re-populating and trying to set new
                populateReviewIdComboBox(); // Refresh dropdown

                // Attempt to re-select the item (its name might have changed)
                String potentiallyUpdatedItemSummary = String.format("ID: %d - %s", selectedItem.getId(), message);
                javax.swing.DefaultComboBoxModel<Review> model = (javax.swing.DefaultComboBoxModel<Review>) reviewIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(potentiallyUpdatedItemSummary)) {
                        reviewIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No review found with ID: " + selectedItem.getId() + " or update failed.");
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("❗ Invalid number format for Prep or Cook Time!");
            ex.printStackTrace();
        }
    }
    
    private void deleteReview() {
        Review selectedItem = (Review) reviewIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a review from the dropdown to delete.");
            return;
        }

        try {
            boolean success = revieweDAO.deleteById(selectedItem.getId());
            if (success) {
                outputArea.setText("🗑️ Review deleted (ID: " + selectedItem.getId() + ")");
                populateReviewIdComboBox(); // Refresh dropdown
                clearInputFields(true); // Clear all fields including combo selection
            } else {
                outputArea.setText("❌ No review found with ID: " + selectedItem.getId() + " or delete failed.");
            }
        } catch (Exception ex) { // Broader catch for any unexpected DAO issues
            outputArea.setText("❗ An error occurred during deletion.");
            ex.printStackTrace();
        }
    }
	

}
