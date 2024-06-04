package dataStructs;

public interface DatabaseEntity {
    String getValues(String separator);
    void checkValues() throws RuntimeException;
}
