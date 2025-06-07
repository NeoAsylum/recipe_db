DELIMITER $$

CREATE TRIGGER IF NOT EXISTS a_Recipe_Log_Insert
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

CREATE TRIGGER IF NOT EXISTS  a_Recipe_Log_Update
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

CREATE TRIGGER IF NOT EXISTS  a_Recipe_Log_Delete
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
