DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_CalculateCaloriesForIngredient (IN ingredient_id_param INT)
BEGIN
  UPDATE Ingredient
  SET calories = (protein * 4)
    + (fat * 9)
    + ((carbohydrates - fiber) * 4)
  WHERE id = ingredient_id_param;
END //

CREATE PROCEDURE IF NOT EXISTS sp_TotalRecipeNutrition(IN recipe_id_param INT)
BEGIN
  DECLARE total_calories DECIMAL(10, 2) DEFAULT 0.0;
  DECLARE total_protein DECIMAL(10, 2) DEFAULT 0.0;
  DECLARE total_fat DECIMAL(10, 2) DEFAULT 0.0;
  DECLARE total_carbohydrates DECIMAL(10, 2) DEFAULT 0.0;
  DECLARE total_fiber DECIMAL(10, 2) DEFAULT 0.0;

  DECLARE done INT DEFAULT FALSE;
  DECLARE quantity_temp VARCHAR(50);
  DECLARE calories_temp INT;
  DECLARE protein_temp FLOAT;
  DECLARE fat_temp FLOAT;
  DECLARE carbohydrates_temp FLOAT;
  DECLARE fiber_temp FLOAT;

  -- Cursor for iteration
  DECLARE ingredient_cursor CURSOR FOR
    SELECT ri.quantity, i.calories, i.protein, i.fat, i.carbohydrates, i.fiber
    FROM RecipeIngredient ri
           JOIN Ingredient i ON ri.ingredient_id = i.id
    WHERE ri.recipe_id = recipe_id_param;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  -- Iterating part
  OPEN ingredient_cursor;
  read_loop: LOOP
    FETCH ingredient_cursor INTO quantity_temp, calories_temp, protein_temp, fat_temp, carbohydrates_temp, fiber_temp;
    IF done THEN
      LEAVE read_loop;
    END IF;

    -- Extract the numerical value from string
    SET quantity_temp = REGEXP_SUBSTR(quantity_temp, '^[0-9]+');

    -- Calculate based on the quantity
    SET total_calories = total_calories + (calories_temp * quantity_temp / 100);
    SET total_protein = total_protein + (protein_temp * quantity_temp / 100);
    SET total_fat = total_fat + (fat_temp * quantity_temp / 100);
    SET total_carbohydrates = total_carbohydrates + (carbohydrates_temp * quantity_temp / 100);
    SET total_fiber = total_fiber + (fiber_temp * quantity_temp / 100);
  END LOOP;
  CLOSE ingredient_cursor;
  -- End of iterating

  -- Select the calculated total nutrition facts
  SELECT
    total_calories,
    total_protein,
    total_fat,
    total_carbohydrates,
    total_fiber;
END //

DELIMITER ;
