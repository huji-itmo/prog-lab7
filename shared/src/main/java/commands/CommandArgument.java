package commands;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CommandArgument implements Serializable {
    Class<?> clazz;
    String name;
    String description;
    boolean isOptional;

    @Serial
    private static final long serialVersionUID=1L;

    @Override
    public String toString() {
        return "{" + (isOptional? "?" : "") + name + "} (type: " + clazz.getSimpleName() + ")";
    }
}
