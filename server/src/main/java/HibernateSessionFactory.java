import dataStructs.Coordinates;
import dataStructs.Person;
import dataStructs.StudyGroup;
import dataStructs.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.*;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@AllArgsConstructor
@Getter
public class HibernateSessionFactory {

    static Configuration config = null;

    public static SessionFactory setupFactory(String pgpassPath) throws IOException {
        String[] userAndPassword = parsePgpass(pgpassPath);

        Properties customProperties = new Properties(2);

        customProperties.setProperty("hibernate.connection.username", userAndPassword[0]);
        customProperties.setProperty("hibernate.connection.password", userAndPassword[1]);


        Configuration configuration = new Configuration()
                .configure()
                .addAnnotatedClass(StudyGroup.class)
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Coordinates.class)
                .addAnnotatedClass(User.class)
                .addProperties(customProperties);

        config = configuration;

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());

        return configuration.buildSessionFactory(builder.build());
    }
    private static String[] parsePgpass (String path) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(Path.of(path))) {

            String[] contents = reader.readLine().split(":");

            if (contents.length != 5) {
                throw new IOException("Bad pgpass file!");
            }

            String userName = contents[3];
            String password = contents[4];

            return new String[]{userName, password};
        }
    }
}
