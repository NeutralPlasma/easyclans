package eu.virtusdevelops.easyclans.dao;

import java.util.List;

public interface DaoCrud<T, ID>{
    /**
     * Used for initializing database or required stuff
     */
    void init();

    /**
     * Gets item by id from database
     * @param id
     * @return
     */
    T getById(ID id);
    List<T> getAll();

    /**
     * Inserts new or updates if it already exists
     * @param t data to save
     * @return saved model or null if failure
     */
    T save(T t);
    boolean delete(T t);
}
