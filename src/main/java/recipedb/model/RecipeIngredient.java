package recipedb.model;

public class RecipeIngredient {
    private int recipeId;
    private int ingredientId;
    private String quantity;
    private String recipeName;
    private String ingredientName;

    public RecipeIngredient(int recipeId, int ingredientId, String quantity) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    public RecipeIngredient(int recipeId, String recipeName, int ingredientId, String ingredientName, String quantity) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    @Override
    public String toString() {
        return String.format("Recipe: %s (RID:%d) --- Ing: %s (IID:%d)",
            recipeName, recipeId, ingredientName, ingredientId);
    }

    public String toDetailedString() {
        return String.format("Recipe: %s (RID:%d) --- Ing: %s (IID:%d) --- Qty: %s",
            recipeName, recipeId, ingredientName, ingredientId, quantity);
    }
}
