package recipedb.model;

public class Ingredient {
    private int id;
    private String name;
    private int calories;
    private float protein;
    private float fat;
    private float carbohydrates;
    private float fiber;

    public Ingredient(int id, String name, int calories, float protein, float fat, float carbohydrates, float fiber) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public float getFiber() {
        return fiber;
    }

    public void setFiber(float fiber) {
        this.fiber = fiber;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s", id, name);
    }

    public String toDetailedString() {
        return String.format("ID: %d | Name: %s | Calories: %d | Protein: %.2fg | Fat: %.2fg | Carbs: %.2fg | Fiber: %.2fg",
            id, name, calories, protein, fat, carbohydrates, fiber);
    }
}
