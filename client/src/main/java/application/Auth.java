package application;

import commands.auth.LoginCommandData;
import commands.auth.RegisterCommandData;
import commands.exceptions.CommandException;
import connection.ConnectionWithServer;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import dataStructs.communication.SessionByteArray;
import lombok.AllArgsConstructor;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

@AllArgsConstructor
public class Auth {

    private final ConnectionWithServer connection;

    public SessionByteArray loginOrRegister() {

        Scanner scanner = new Scanner(System.in);

        return chooseLoginOrRegister(scanner);
    }

    public SessionByteArray chooseLoginOrRegister(Scanner scanner) {
        System.out.println("Hi there! Choose one of the available commands");
        System.out.println("- login");
        System.out.println("- register");
        System.out.println("- exit");

        try {
            switch (scanner.nextLine()) {
                case "login":
                    return login(scanner);
                case "register":
                    return register(scanner);
                case "exit":
                    System.out.println("Bye bye!");
                    System.exit(0);
                    return null;
                default:
                    System.out.println("Try again...");
                    return chooseLoginOrRegister(scanner);
            }
        } catch (CommandException e) {
            System.err.println(e.getMessage());
            return chooseLoginOrRegister(scanner);
        }
    }

    private SessionByteArray register(Scanner scanner) {

        CommandExecutionResult response = connection.sendOneShot(new Request(new RegisterCommandData(), readUsernameAndPassword(scanner)));

        if (response.getCode() != 200) {
            throw new CommandException(response.getText());
        }

        System.out.println("Created new user!");

        return response.getSessionByteArray();
    }

    private SessionByteArray login(Scanner scanner) {
        List<Object> params = readUsernameAndPassword(scanner);

        CommandExecutionResult response = connection.sendOneShot(new Request(new LoginCommandData(), params));

        if (response.getCode() != 200) {
            throw new CommandException(response.getText());
        }

        System.out.println("Hi, " + params.get(0) + "!");

        return response.getSessionByteArray();
    }

    private List<Object> readUsernameAndPassword(Scanner scanner) {

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        String password = "";

        System.out.print("Enter password: ");
        Console console = System.console();
        if (console == null) {
            password = scanner.nextLine();
        }
        else {
            password = new String(console.readPassword());
        }

        return List.of(username, password);
    }
}
