DROP DATABASE IF EXISTS FoodRecipesDB;
CREATE DATABASE FoodRecipesDB;
USE FoodRecipesDB;

CREATE TABLE Recipe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    instructions TEXT,
    prep_time INT,
    cook_time INT
);

CREATE TABLE Ingredient (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    calories INT NOT NULL,
    protein FLOAT NOT NULL,
    fat FLOAT NOT NULL,
    carbohydrates FLOAT NOT NULL,
    fiber FLOAT NOT NULL
);

CREATE TABLE RecipeIngredient (
    recipe_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity VARCHAR(50),
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipe(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id) ON DELETE CASCADE
);

CREATE TABLE Category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE RecipeCategory (
    recipe_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (recipe_id, category_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipe(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE CASCADE
);

CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username varchar(50) UNIQUE NOT NULL,
    password varchar(255) NOT NULL
);


CREATE TABLE ChangeLog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    record_id VARCHAR(255) NOT NULL,
    operation_type VARCHAR(10) NOT NULL, -- 'INSERT', 'UPDATE', or 'DELETE'
    old_values JSON,
    new_values JSON,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255)
);
 

-- FROM HERE ON THE TRIGGERS ARE DEFINED
-- THIS SECTION IS FOR TRIGGERS

DELIMITER $$

CREATE TRIGGER a_Recipe_Log_Insert
AFTER INSERT ON Recipe
FOR EACH ROW
BEGIN
    INSERT INTO ChangeLog (table_name, record_id, operation_type, old_values, new_values, changed_by)
    VALUES (
        'Recipe',
        NEW.id,
        'INSERT',
        NULL, -- No old values on insert
        JSON_OBJECT(
            'id', NEW.id,
            'name', NEW.name,
            'description', NEW.description,
            'instructions', NEW.instructions,
            'prep_time', NEW.prep_time,
            'cook_time', NEW.cook_time
        ),
        USER()
    );
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER a_Recipe_Log_Update
AFTER UPDATE ON Recipe
FOR EACH ROW
BEGIN
    INSERT INTO ChangeLog (table_name, record_id, operation_type, old_values, new_values, changed_by)
    VALUES (
        'Recipe',
        NEW.id,
        'UPDATE',
        JSON_OBJECT(
            'id', OLD.id,
            'name', OLD.name,
            'description', OLD.description,
            'instructions', OLD.instructions,
            'prep_time', OLD.prep_time,
            'cook_time', OLD.cook_time
        ),
        JSON_OBJECT(
            'id', NEW.id,
            'name', NEW.name,
            'description', NEW.description,
            'instructions', NEW.instructions,
            'prep_time', NEW.prep_time,
            'cook_time', NEW.cook_time
        ),
        USER()
    );
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER a_Recipe_Log_Delete
AFTER DELETE ON Recipe
FOR EACH ROW
BEGIN
    INSERT INTO ChangeLog (table_name, record_id, operation_type, old_values, new_values, changed_by)
    VALUES (
        'Recipe',
        OLD.id,
        'DELETE',
        JSON_OBJECT(
            'id', OLD.id,
            'name', OLD.name,
            'description', OLD.description,
            'instructions', OLD.instructions,
            'prep_time', OLD.prep_time,
            'cook_time', OLD.cook_time
        ),
        NULL,
        USER()
    );
END$$

DELIMITER ;

-- Stored procedure section

DELIMITER //
CREATE PROCEDURE sp_RecipeIngredient_FindAll ()
BEGIN
    SELECT
        ri.recipe_id,
        r.name AS RecipeName,
        ri.ingredient_id,
        i.name AS IngredientName,
        ri.quantity
    FROM RecipeIngredient ri
    JOIN Recipe r ON ri.recipe_id = r.id
    JOIN Ingredient i ON ri.ingredient_id = i.id
    ORDER BY r.name;
END //
DELIMITER ;
