package dataStructs.exceptions;

public class KeyNotFoundException extends IllegalValueException {
    public KeyNotFoundException(String key) {
        super("The key \"" + key + "\" is not found.");
    }
}
