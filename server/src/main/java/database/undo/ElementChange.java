package database.undo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ElementChange<T> {
    boolean isAdded;
    T element;
}