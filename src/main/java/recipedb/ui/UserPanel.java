package recipedb.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import recipedb.dao.UserDAO;
import recipedb.model.User;

public class UserPanel extends JPanel {

    private final UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea outputArea;
    private JComboBox<User> userIdComboBox; // JComboBox for category selection
    private boolean isPopulatingComboBox = false; // Flag to prevent event recursion

    public UserPanel() {
        setLayout(new BorderLayout());
        userDAO = new UserDAO();
        createUI();
    }

    private void createUI() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Select User:"));
        userIdComboBox = new JComboBox<>();
        inputPanel.add(userIdComboBox);

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField);

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
        userIdComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            	populateUserIdComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        // Load details when an item is selected
        userIdComboBox.addActionListener(e -> {
            if (!isPopulatingComboBox && userIdComboBox.getSelectedItem() != null) {
                loadSelectedUserDetails();
            }
        });

        createBtn.addActionListener(e -> createUser());
        readBtn.addActionListener(e -> readUsers());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());

        // Initial population
        populateUserIdComboBox();
    }

    private void populateUserIdComboBox() {
        isPopulatingComboBox = true;
        User selectedItemBeforeUpdate = (User) userIdComboBox.getSelectedItem();
        userIdComboBox.removeAllItems();

        List<User> userSummaries = userDAO.findAll();
        if (userSummaries.isEmpty()) {
            outputArea.append("\nNo users available to select.");
        } else {
            for (User summary : userSummaries) {
            	userIdComboBox.addItem(summary);
            }
            if (userIdComboBox != null && userSummaries.contains(selectedItemBeforeUpdate)) {
            	userIdComboBox.setSelectedItem(selectedItemBeforeUpdate);
            } else if (!userSummaries.isEmpty()) {
            	userIdComboBox.setSelectedIndex(-1);
            }
        }
        if (userIdComboBox.getSelectedIndex() == -1) {
            clearInputFields(false);
        }
        isPopulatingComboBox = false;
    }

    private void loadSelectedUserDetails() {
        User selectedItem = (User) userIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            clearInputFields(false); // Only clear name field
            return;
        }

        User user = userDAO.findById(selectedItem.getId());
        if (user != null) {
            usernameField.setText(user.getUserName());
            passwordField.setText(user.getPassword());
        } else {
            outputArea.setText("❌ Error: Could not load details for User ID: " + selectedItem.getId());
            clearInputFields(false);
        }
    }

    private void clearInputFields(boolean clearComboBoxAlso) {
        if (clearComboBoxAlso) {
        	userIdComboBox.setSelectedIndex(-1);
        }
        usernameField.setText("");
    }

    private void createUser() {
        String username = usernameField.getText();
        String password = Arrays.toString(passwordField.getPassword());
        if (username.trim().isEmpty()) {
            outputArea.setText("❗ Username cannot be empty.");
            return;
        }
        if (password.trim().isEmpty()) {
            outputArea.setText("❗ Password cannot be empty.");
            return;
        }
        try {
            User user = new User(-1, username, password );
            boolean success = userDAO.create(user);
            if (success) {
                outputArea.setText("✅ User created: " + username);
//                InputFields(true);
            } else {
                outputArea.setText("❌ Failed to create User. It might already exist or another error occurred.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ Error processing request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void readUsers() {
        List<User> users = userDAO.findAll();
        outputArea.setText("📂 Users:\n" + users.stream().map(User::toString).collect(Collectors.joining("\n")));
    }

    private void updateUser() {
        User selectedItem = (User) userIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a user from the dropdown to update.");
            return;
        }

        String name = usernameField.getText();
        if (name.trim().isEmpty()) {
            outputArea.setText("❗ User name cannot be empty for update.");
            return;
        }

        String password = Arrays.toString(passwordField.getPassword());
        if (password.trim().isEmpty()) {
            outputArea.setText("❗ Password cannot be empty for update.");
            return;
        }

        try {
            boolean success = userDAO.update(new User(selectedItem.getId(), name, password));
            if (success) {
                outputArea.setText("✅ User updated: " + name);
                populateUserIdComboBox();

                // Attempt to re-select the updated item
                DefaultComboBoxModel<User> model = (DefaultComboBoxModel<User>) userIdComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(selectedItem)) {
                        userIdComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                outputArea.setText("❌ No User found with ID: " + selectedItem.getId() + " or update failed.");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ Error processing update request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteUser() {
        User selectedItem = (User) userIdComboBox.getSelectedItem();
        if (selectedItem == null) {
            outputArea.setText("❗ Please select a user from the dropdown to delete.");
            return;
        }

        try {
            boolean success = userDAO.deleteById(selectedItem.getId());
            if (success) {
                outputArea.setText("🗑️ User deleted (ID: " + selectedItem.getId() + ")");
                populateUserIdComboBox();
            } else {
                outputArea.setText("❌ No user found with ID: " + selectedItem.getId() + " or delete failed (it might be in use).");
            }
        } catch (Exception ex) {
            outputArea.setText("❗ An error occurred during deletion: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
