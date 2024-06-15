package dataStructs.undo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class TransactionLog<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<ElementChange<T>> changesList;

    public static <T> TransactionLog<T> addedElements(T... elements) {
        List<ElementChange<T>> changes = new ArrayList<>();

        for (T element : elements) {
            changes.add(new ElementChange<>(true, element));
        }

        return new TransactionLog<>(changes);
    }

    public static <T> TransactionLog<T> deletedElements(T... elements) {
        List<ElementChange<T>> changes = new ArrayList<>();

        for (T element : elements) {
            changes.add(new ElementChange<>(false, element));
        }

        return new TransactionLog<>(changes);
    }

    public static <T> TransactionLog<T> deletedElements(Iterable<T> elements) {
        List<ElementChange<T>> changes = new ArrayList<>();

        for (T element : elements) {
            changes.add(new ElementChange<>(false, element));
        }

        return new TransactionLog<>(changes);
    }

    public static <T> TransactionLog<T> changedElement(T newElement, T oldElement) {
        List<ElementChange<T>> changes = new ArrayList<>();

        changes.add(new ElementChange<>(true, newElement));
        changes.add(new ElementChange<>(false, oldElement));

        return new TransactionLog<>(changes);
    }
}