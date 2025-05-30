package recipedb.dao;

import java.util.List;

public interface Dao<T> {
    List<T> findAll();
    boolean create(T object);
    boolean update(T object);
}
