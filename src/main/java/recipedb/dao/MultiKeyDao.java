package recipedb.dao;

public interface MultiKeyDao<T> extends Dao<T> {
    T findByKeys(T object);
    boolean deleteByKeys(T object);
}
