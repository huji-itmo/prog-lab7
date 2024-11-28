package application;

import commands.CommandData;
import commands.CommandDataProcessor;
import commands.databaseCommands.ConfirmCommandData;
import commands.databaseCommands.DeclineCommandData;
import commands.exceptions.CommandException;
import connection.ConnectionWithServer;
import dataStructs.communication.Request;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
public class ConsoleManager {

    Scanner scanner = new Scanner(System.in);

    private final CommandDataProcessor processor;
    private final ConnectionWithServer connection;

    @Setter
    Boolean waitingForConfirmation = false;

    public void startCommandLoop() {
        try {
            while (true) {
                try {

                    String line = scanner.nextLine();

                    if (!waitingForConfirmation) {
                        if (!processor.checkAndExecuteClientSide(line)) {
                            connection.sendRequest(processor.checkCommandAndCreateRequest(line));
                        }

                        continue;
                    }

                    Optional<Boolean> accept = getConfirmOrDecline(line.trim().toLowerCase());
                    if (accept.isEmpty()) {
                        System.out.println("Please write (Y/N)");
                        continue;
                    }
                    CommandData data = null;
                    if (accept.get()) {
                        data = new ConfirmCommandData();
                    } else {
                        data = new DeclineCommandData();

                    }

                    Request request = new Request(data, List.of());
                    connection.sendRequest(request);

                    waitingForConfirmation = false;

                } catch (CommandException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println("Interactive mode ended.");
        }
    }

    public static Optional<Boolean> getConfirmOrDecline(String input) {

        switch (input) {
            case "y" -> {
                return Optional.of(true);
            }
            case "n" -> {
                return Optional.of(false);
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
