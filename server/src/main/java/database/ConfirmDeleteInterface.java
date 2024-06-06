package database;

import dataStructs.communication.SessionByteArray;

import java.util.List;

@FunctionalInterface
public interface ConfirmDeleteInterface<T> {
    boolean confirm(List<T> elementsToDelete, SessionByteArray session);

}
