package dataStructs;

import dataStructs.exceptions.NumberOutOfBoundsException;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Scanner;
import java.util.function.Consumer;

@Data
@Entity
@Table(name = "coordinates")
@NoArgsConstructor
public class Coordinates implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinates_generator")
    @SequenceGenerator(name = "coordinates_generator", sequenceName = "coordinates_id_seq", allocationSize = 1)
    private long id;
    @Serial
    private static final long serialVersionUID=1L;

    private double x; //Значение поля должно быть больше -313
    private double y;

    public void setX(double x) throws NumberOutOfBoundsException {
        Fields.checkX(x);
        this.x = x;
    }

    public Coordinates(double x, double y){
        setX(x);
        setY(y);
    }

    public static Coordinates createFromInput(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
        standardOut.accept("Creating coordinates...");

        double x = Fields.enterX(scanner, standardOut, errorOut);
        double y = Fields.enterY(scanner, standardOut, errorOut);

        return new Coordinates(x,y);
    }

    public String getValues(String separator) {
        return getX() + separator +
                getY();
    }

    @Override
    public void checkValues() throws RuntimeException {
        Fields.checkX(getX());
    }

    public static class Fields {
        private final static String X_OUT_OF_BOUNDS_MESSAGE = "Value of X should equal or be greater than -313!";

        public static double enterX(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter x coordinate: (double)");

            try {
                double x = Double.parseDouble(scanner.nextLine());

                checkX(x);

                return x;
            }
            catch (NumberFormatException e) {
                errorOut.accept(e.getMessage());
            }
            catch ( NumberOutOfBoundsException e) {
                errorOut.accept(X_OUT_OF_BOUNDS_MESSAGE);
            }

            return enterX(scanner, standardOut, errorOut);
        }
        public static void checkX(double x) throws NumberOutOfBoundsException {
            if (x <= -313) {
                throw new NumberOutOfBoundsException(X_OUT_OF_BOUNDS_MESSAGE);
            }
        }

        public static double enterY(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter y coordinate: (double)");

            try {
                return Double.parseDouble(scanner.nextLine());
            }
            catch (NumberFormatException e) {
                errorOut.accept(e.getMessage());
            }
            catch ( NumberOutOfBoundsException e) {
                errorOut.accept(X_OUT_OF_BOUNDS_MESSAGE);
            }

            return enterY(scanner, standardOut, errorOut);
        }
    }
}