package database;

import database.undo.UndoLog;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * interface for general-purpose database. Should be created with decorator.
 *
 * @param <T> - class that database handles
 * @param <K> - class of primary key
 */
public interface Database<T, K> {
    /**
     * @param id primary key
     * @return the list of deleted elements.
     */
    List<T> removeGreaterOrLowerThanPrimaryKey(K id, boolean greater, String sessionStr);

    String getElementsDescendingByPrimaryKey();

    String getInfo();
    /**
     * Adds a new element and updates the last id.
     *
     * @param element element
     */
    void addElement(T element, String sessionStr);
    String serializeAllElements();
    T updateElementByPrimaryKey(K id, T new_element, String sessionStr) throws IllegalArgumentException;
    T removeElementByPrimaryKey(K id, String sessionStr) throws IllegalArgumentException;
    /**
     * Clears the collection and resets the last id counter.
     */
    void clear(String sessionStr);

    void pushToUndoStack(UndoLog<T> log, String session);

    boolean existsById(K id);

    long registerNewUser(String userName, String password);

    boolean popUndoStackWithSession(String session);
}
