package dataStructs.undo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class ElementChange<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    boolean isAdded;
    T element;
}