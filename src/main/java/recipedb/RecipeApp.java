package recipedb;

import javax.swing.SwingUtilities;

import recipedb.ui.RecipeAppGUI;

public class RecipeApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecipeAppGUI().setVisible(true));
    }
}
