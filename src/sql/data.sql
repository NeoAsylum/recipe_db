-- Populate Ingredient Table
INSERT INTO Ingredient (name, calories, protein, fat, carbohydrates, fiber) VALUES
('Chicken Breast', 165, 31, 3.6, 0, 0),
('Broccoli', 55, 3.7, 0.6, 11.2, 5.2),
('Olive Oil', 884, 0, 100, 0, 0),
('Garlic', 149, 6.4, 0.5, 33.1, 2.1),
('Spaghetti', 371, 13, 1.5, 75, 3),
('Tomato', 18, 0.9, 0.2, 3.9, 1.2),
('Ground Beef', 250, 26, 15, 0, 0),
('Onion', 40, 1.1, 0.1, 9.3, 1.7),
('Cheddar Cheese', 404, 23, 33, 3.1, 0),
('White Bread', 265, 9, 3.2, 49, 2.7);

-- Populate Recipe Table
INSERT INTO Recipe (name, description, instructions, prep_time, cook_time) VALUES
('Grilled Chicken with Broccoli', 'A healthy and simple grilled chicken breast served with steamed broccoli.', '1. Season chicken breast with salt, pepper, and herbs. 2. Preheat grill to medium-high. 3. Grill chicken for 6-8 minutes per side, or until cooked through. 4. Steam broccoli until tender-crisp. 5. Serve chicken with a side of broccoli and a drizzle of olive oil if desired.', 10, 20),
('Spaghetti Bolognese', 'Classic Italian pasta dish with a rich meat sauce.', '1. Heat olive oil in a large pan. Sauté chopped onion and minced garlic until softened. 2. Add ground beef and cook until browned, breaking it apart. Drain excess fat. 3. Stir in crushed tomatoes, tomato paste (optional), and Italian herbs. Bring to a simmer, then reduce heat and cook for at least 30 minutes, stirring occasionally. 4. Meanwhile, cook spaghetti according to package directions. 5. Serve bolognese sauce over spaghetti, topped with grated cheddar cheese.', 15, 45),
('Quick Cheesy Garlic Bread', 'A simple and fast cheesy garlic bread, perfect as a side or snack.', '1. Preheat oven to 180°C (350°F). 2. Slice white bread or baguette. 3. In a small bowl, mix melted olive oil (or butter) with minced garlic and a pinch of dried parsley. 4. Spread the garlic mixture evenly on each bread slice. 5. Top generously with shredded cheddar cheese. 6. Place on a baking sheet and bake for 8-10 minutes, or until the cheese is melted and bubbly and the bread is golden brown.', 5, 10);

-- Populate RecipeIngredient Table
-- Recipe 1: Grilled Chicken with Broccoli (Recipe ID: 1)
INSERT INTO RecipeIngredient (recipe_id, ingredient_id, quantity) VALUES
(1, 1, '150g'), -- Chicken Breast, 150g
(1, 2, '200g'), -- Broccoli, 200g
(1, 3, '10g'),  -- Olive Oil, 10g
(1, 4, '5g');   -- Garlic, 5g

-- Recipe 2: Spaghetti Bolognese (Recipe ID: 2)
INSERT INTO RecipeIngredient (recipe_id, ingredient_id, quantity) VALUES
(2, 5, '100g'), -- Spaghetti, 100g (dry weight)
(2, 7, '150g'), -- Ground Beef, 150g
(2, 6, '300g'), -- Tomato (e.g., canned crushed), 300g
(2, 8, '50g'),  -- Onion, 50g
(2, 3, '15g'),  -- Olive Oil, 15g
(2, 4, '10g'),  -- Garlic, 10g
(2, 9, '30g');  -- Cheddar Cheese, 30g (for topping)

-- Recipe 3: Quick Cheesy Garlic Bread (Recipe ID: 3)
INSERT INTO RecipeIngredient (recipe_id, ingredient_id, quantity) VALUES
(3, 10, '100g'), -- White Bread, 100g
(3, 4, '5g'),   -- Garlic, 5g
(3, 3, '20g'),  -- Olive Oil, 20g (can be substituted with butter)
(3, 9, '50g');  -- Cheddar Cheese, 50g

-- Populate Category Table
INSERT INTO Category (name) VALUES
('Main Course'),
('Healthy'),
('Quick Meal'),
('Italian'),
('Comfort Food'),
('Side Dish'),
('Appetizer');

-- Populate RecipeCategory Table
-- Recipe 1: Grilled Chicken with Broccoli (Recipe ID: 1)
INSERT INTO RecipeCategory (recipe_id, category_id) VALUES
(1, 1), -- Main Course
(1, 2); -- Healthy

-- Recipe 2: Spaghetti Bolognese (Recipe ID: 2)
INSERT INTO RecipeCategory (recipe_id, category_id) VALUES
(2, 1), -- Main Course
(2, 4), -- Italian
(2, 5); -- Comfort Food

-- Recipe 3: Quick Cheesy Garlic Bread (Recipe ID: 3)
INSERT INTO RecipeCategory (recipe_id, category_id) VALUES
(3, 3), -- Quick Meal
(3, 5), -- Comfort Food
(3, 6), -- Side Dish
(3, 7)  -- Appetizer