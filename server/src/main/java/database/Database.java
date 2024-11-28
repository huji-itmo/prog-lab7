package database;

import dataStructs.communication.SessionByteArray;

import javax.persistence.EntityExistsException;
import java.util.List;

/**
 * interface for general-purpose database. Should be created with decorator.
 *
 * @param <T> - class that database handles
 * @param <K> - class of primary key
 */
public interface Database<T, K> {
    List<T> removeGreaterOrLowerThanPrimaryKey(K id, boolean greater, SessionByteArray sessionStr);

    List<T> getElementsDescendingByPrimaryKey();

    String getInfo();

    void addElement(T element, SessionByteArray sessionStr);
    List<T> getElements();

    T updateElementByPrimaryKey(K id, T new_element, SessionByteArray sessionStr) throws IllegalArgumentException;

    T removeElementByPrimaryKey(K id, SessionByteArray sessionStr) throws IllegalArgumentException;
    /**
     * Clears the collection and resets the last id counter.
     */
    void clear(SessionByteArray sessionStr);

    boolean existsById(K id);

    String registerNewUser(String userName, String password);

    boolean popUndoStackWithSession(SessionByteArray session) throws EntityExistsException;
}
