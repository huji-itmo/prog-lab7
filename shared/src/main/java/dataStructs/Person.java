package dataStructs;

import dataStructs.exceptions.IllegalValueException;
import dataStructs.exceptions.NumberOutOfBoundsException;
import dataStructs.exceptions.StringIsEmptyException;
import jdk.jfr.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.Consumer;

@Data
@Entity
@Table(name = "people")
@NoArgsConstructor
public class Person implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "people_generator")
    @SequenceGenerator(name = "people_generator", sequenceName = "people_id_seq", allocationSize = 1)
    long id;

    @Serial
    private static final long serialVersionUID=1L;

    private String name; //Поле не может быть null, Строка не может быть пустой
    private LocalDate birthday; //Поле может быть null
    private double weight; //Значение поля должно быть больше 0
    @Column(name = "passport_id")
    private String passportID; //Длина строки не должна быть больше 40, Длина строки должна быть не меньше 7, Поле не может быть null

    @Column(name = "nationality")
    @Enumerated(value = EnumType.STRING)
    @Type(type = "enum_postgressql")
    private Country nationality; //Поле может быть null

    private final static String NAME_IS_EMPTY_MESSAGE = "Name of the group admin can't be empty!";
    private final static String WIGHT_IS_OUT_OF_BOUNDS = "Weight can't be less than zero!";

    public void setName(String name) {
        Fields.checkName(name);

        this.name = name;
    }

    public void setWeight(double weight) {
        Fields.checkWeight(weight);
        this.weight = weight;
    }
    public void setPassportID(String passportID) {
        Fields.checkPassportID(passportID);
        this.passportID = passportID;
    }

    public Person(String name, LocalDate birthday, double weight, String passportID, Country nationality) throws StringIsEmptyException {
        setName(name);
        setBirthday(birthday);
        setWeight(weight);
        setPassportID(passportID);
        setNationality(nationality);
    }

    public static Person createFromInput(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
        standardOut.accept("Creating group admin...");
        standardOut.accept("Type name of a person or type \"null\" for person to be null.");

        String personName = Fields.enterName(scanner, standardOut, errorOut);

        if (personName.equals("null")) {
            return null;
        }

        return new Person(
                personName,
                Fields.enterBirthday(scanner, standardOut, errorOut),
                Fields.enterWeight(scanner, standardOut, errorOut),
                Fields.enterPassportId(scanner, standardOut, errorOut),
                Fields.enterCounty(scanner, standardOut, errorOut));
    }

    public String getValues(String separator) {
        return getName() + separator +
                getBirthday() + separator +
                getWeight() + separator +
                getPassportID() + separator +
                getNationality();
    }

    @Override
    public void checkValues() throws RuntimeException {
        Fields.checkName(getName());
        Fields.checkWeight(getWeight());
        Fields.checkPassportID(getPassportID());
    }

    public static class Fields {

        public static String enterName(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter group admin name: (String)");

            try {
                String in = scanner.nextLine();
                checkName(in);

                return in;
            } catch (StringIsEmptyException e) {
                errorOut.accept(e.getMessage());
                return enterName(scanner, standardOut, errorOut);
            }
        }

        public static void checkName(String name) throws StringIsEmptyException {
            if (name == null || name.isBlank()) {
                throw new StringIsEmptyException(NAME_IS_EMPTY_MESSAGE);
            }
        }


        public static String enterPassportId(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter group admin passportID: (String)");

            try {
                String in = scanner.nextLine();
                checkPassportID(in);

                return in;

            } catch (IllegalValueException e) {
                errorOut.accept(e.getMessage());
                return enterPassportId(scanner, standardOut, errorOut);
            }
        }

        public static void checkPassportID(String passportID) {
            if (passportID == null) {
                throw new StringIsEmptyException("PassportID can't be null!");
            }
            if (passportID.length() < 7) {
                throw new IllegalValueException("PassportID should be longer than 6 characters!");
            } else if (passportID.length() > 40) {
                throw new IllegalValueException("PassportID should be smaller than 41 characters!");

            }
        }

        public static LocalDate enterBirthday(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter group admin birthday (yyyy-mm-dd or null): ");

            String nextLine = scanner.nextLine().trim();

            if (nextLine.equals("null")) {
                return null;
            }

            try {

                LocalDate date = LocalDate.parse(nextLine);
                if (date.getYear() <= 0) {
                    errorOut.accept("Please use current era :(");
                    return enterBirthday(scanner, standardOut, errorOut);
                }

                return date;

            } catch (DateTimeException | NumberOutOfBoundsException | NumberFormatException e) {
                errorOut.accept(e.getMessage());
            }
            return enterBirthday(scanner, standardOut, errorOut);
        }

        public static Country enterCounty(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter nationality of group admin (Country): ");
            standardOut.accept("Choose one of following: " + StudyGroup.Fields.getEnumOptions(Country.class));

            try {
                String in = scanner.nextLine();
                return Country.valueOf(in);
            } catch (IllegalArgumentException e) {
                errorOut.accept("Country: " + e.getMessage());
                return enterCounty(scanner, standardOut, errorOut);
            }
        }

        public static double enterWeight(Scanner scanner, Consumer<String> standardOut, Consumer<String> errorOut) {
            standardOut.accept("Enter weight of group admin: (double)");
            try {
                double in = Double.parseDouble(scanner.nextLine());

                checkWeight(in);

                return in;
            } catch (NumberOutOfBoundsException e) {
                errorOut.accept(WIGHT_IS_OUT_OF_BOUNDS);
            } catch (NumberFormatException e) {
                errorOut.accept(e.getMessage());
            }

            return enterWeight(scanner, standardOut, errorOut);
        }

        private static void checkWeight(double weight) {
            if (weight <= 0) {
                throw new NumberOutOfBoundsException(WIGHT_IS_OUT_OF_BOUNDS);
            }
        }
    }
}
