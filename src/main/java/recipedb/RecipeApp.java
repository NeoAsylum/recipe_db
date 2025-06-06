package recipedb;

import javax.swing.SwingUtilities;

import recipedb.dao.DatabaseUtil;
import recipedb.ui.RecipeAppGUI;

public class RecipeApp {

    public static void main(String[] args) {
        DatabaseUtil.configureConnection("db.properties");
        SwingUtilities.invokeLater(() -> new RecipeAppGUI().setVisible(true));
    }
}
