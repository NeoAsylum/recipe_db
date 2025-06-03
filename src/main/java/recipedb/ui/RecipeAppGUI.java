package recipedb.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class RecipeAppGUI extends JFrame {

    public RecipeAppGUI() {
        setTitle("Recipe Database App");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        // Set Modern Look & Feel
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());

        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Recipes", new RecipePanel());
        tabbedPane.addTab("Recipe Categories", new RecipeCategoryPanel());
        tabbedPane.addTab("Ingredients", new IngredientsPanel());
        tabbedPane.addTab("Categories", new CategoryPanel());
        tabbedPane.addTab("Recipe Ingredients", new RecipeIngredientPanel());
        tabbedPane.addTab("Deletion Log", new DeletionLogPanel());
        tabbedPane.addTab("User", new UserPanel());

        SwingUtilities.updateComponentTreeUI(this);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
