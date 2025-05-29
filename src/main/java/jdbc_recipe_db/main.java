package jdbc_recipe_db;

import javax.swing.SwingUtilities;

import jdbc_recipe_db.ui.RecipeAppGUI;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecipeAppGUI().setVisible(true));
    }
}
