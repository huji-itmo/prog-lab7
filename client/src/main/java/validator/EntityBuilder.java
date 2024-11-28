package validator;

import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class EntityBuilder<T> {

    private builderFromString<T> builderFromString;
    private builderFromInput<T> builderFromInput;

    public T buildFromString(String input, Consumer<String> standardOut, Consumer<String> errorOut) {
        return builderFromString.build(input, standardOut, errorOut);
    }
    public T buildFromInput(Consumer<String> standardOut, Consumer<String> errorOut) {
        return builderFromInput.build(standardOut, errorOut);
    }

    @FunctionalInterface
    public interface builderFromString<T> {
        T build(String input, Consumer<String> standardOut, Consumer<String> errorOut);
    }

    @FunctionalInterface
    public interface builderFromInput<T> {
        T build(Consumer<String> standardOut, Consumer<String> errorOut);
    }
}
