package recipedb.model;

public class Recipe {
    private int id;
    private String name;
    private String description;
    private String instructions;
    private int prepTime;
    private int cookTime;

    public Recipe(int id, String name, String description, String instructions, int prepTime, int cookTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | %s",
            id, name
        );
    }

    public String toDetailedString() {
        return String.format(
            "ID: %d | Name: %s | Description: %s | Instructions: %s | Prep Time: %d min | Cook Time: %d min",
            id, name, description, instructions, prepTime, cookTime
        );
    }
}
