package database.undo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UndoLog<T> {
    private List<ElementChange<T>> changesList;

    public static <T> UndoLog<T> addedElements(T ...elements) {
        List<ElementChange<T>> changes = new ArrayList<>();

        for (T element : elements) {
            changes.add(new ElementChange<>(true, element));
        }

        return new UndoLog<>(changes);
    }

    public static <T> UndoLog<T> deletedElements(T ...elements) {
        List<ElementChange<T>> changes = new ArrayList<>();

        for (T element : elements) {
            changes.add(new ElementChange<>(false, element));
        }

        return new UndoLog<>(changes);
    }

    public static <T> UndoLog<T> deletedElements(Iterable<T> elements) {
        List<ElementChange<T>> changes = new ArrayList<>();

        for (T element : elements) {
            changes.add(new ElementChange<>(false, element));
        }

        return new UndoLog<>(changes);
    }

    public static <T> UndoLog<T> changedElement(T newElement, T oldElement) {
        List<ElementChange<T>> changes = new ArrayList<>();

        changes.add(new ElementChange<>(true, newElement));
        changes.add(new ElementChange<>(false, oldElement));

        return new UndoLog<>(changes);
    }
}