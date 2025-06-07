package recipedb.model;

public class Review {
	private int id;
    private int recipe;
    private int user;
    private String message;

    public Review(int id, int recipe, int user, String message) {
        this.id = id;
        this.recipe = recipe;
        this.user = user;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipe() {
		return recipe;
	}
    
    public void setRecipe(int recipe) {
		this.recipe = recipe;
	}
    
    public int getUser() {
		return user;
	}
    
    public void setUser(int user) {
		this.user = user;
	}
    
    public String getMessage() {
		return message;
	}
    
    public void setMessage(String message) {
		this.message = message;
	}

    @Override
    public String toString() {
        return "ID: " + id + " | " + user + " | " + recipe + " | " + message;
    }

}
