package recipedb.model;

public class RecipeCategory {
    private int recipeId;
    private int categoryId;
    private String recipeName;
    private String categoryName;

    public RecipeCategory(int recipeId, int categoryId) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
    }

    public RecipeCategory(int recipeId, String recipeName, int categoryId, String categoryName) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return String.format("RecID: %d (%s) --- CatID: %d (%s)",
            recipeId, recipeName, categoryId, categoryName);
    }
}
