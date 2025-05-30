package recipedb.dao;

public interface IdDao<T> extends Dao<T> {
    T findById(int id);
    boolean deleteById(int id);
}
