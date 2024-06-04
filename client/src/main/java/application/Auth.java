package application;

import commands.auth.LoginCommandData;
import commands.auth.RegisterCommandData;
import connection.DatabaseConnection;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import lombok.AllArgsConstructor;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

@AllArgsConstructor
public class Auth {

    private final DatabaseConnection connection;

    public long loginOrRegister() {

        Scanner scanner = new Scanner(System.in);

        return chooseLoginOrRegister(scanner);
    }

    public long chooseLoginOrRegister(Scanner scanner) {
        System.out.println("Hi there!");
        System.out.println("---> login");
        System.out.println("---> register");

        switch (scanner.nextLine()) {
            case "login":
                return login(scanner);
            case "register":
                return register(scanner);
        }

        System.out.println("Try again...");
        return chooseLoginOrRegister(scanner);
    }

    private long register(Scanner scanner) {

        ServerResponse response = connection.sendOneShot(new Request(new RegisterCommandData(),readUsernameAndPassword(scanner)));

        if (response.getCode() == 200) {
            System.out.println("Created new user!");

            return Long.parseLong(response.getText());
        }
        else {
            return -1;
        }
    }

    private long login(Scanner scanner) {
        List<Object> params = readUsernameAndPassword(scanner);

        ServerResponse response = connection.sendOneShot(new Request(new LoginCommandData(),params));

        if (response.getCode() == 200) {
            System.out.println("Hi! " + params.get(0));

            return Long.parseLong(response.getText());
        }
        else {
            return -1;
        }
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
