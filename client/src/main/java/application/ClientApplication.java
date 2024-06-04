package application;

import commands.CommandDataProcessor;
import commands.clientSideCommands.*;
import commands.clientSideCommands.commandData.*;
import commands.clientSideCommands.commandData.HelpCommandData;
import commands.databaseCommands.*;
import commands.exceptions.CommandException;
import connection.DatabaseConnection;
import dataStructs.communication.ServerResponse;
import studyGroupSpecific.StudyGroupEntityBuilder;
import studyGroupSpecific.StudyGroupValidator;
import validator.Validator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientApplication {
    public static void run() {
        DatabaseConnection connection = new DatabaseConnection();

        try {
            System.out.println("Waiting for connection...");
            boolean res = connection.connect(InetAddress.getLocalHost(), 5252);
            if (res) {
                System.out.println("Connected!");
            } else {
                System.err.println("Server is down...");
                System.exit(0);
            }

        } catch (UnknownHostException e) {
            System.err.println("Not connected!");
            System.exit(0);
        }
        connection.startMessageAcceptingThread();

        String session = new Auth(connection).loginOrRegister();
        connection.setSession(session);

        System.out.println(session);

        connection.addNewMessageHandler(ClientApplication::newMessageHandler);

        Scanner scanner = new Scanner(System.in);

        CommandDataProcessor processor = getCommandDataProcessor();
        addClientSideCommands(processor, connection);
        try {
            while (true) {
                try {

                    String line = scanner.nextLine();

                    if (processor.checkClientSide(line)) {
                        continue;
                    }

                    connection.sendRequest(processor.checkCommandAndCreateRequest(line));
                } catch (CommandException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println("Interactive mode ended.");
        }
    }

    public static void newMessageHandler(ServerResponse response) {
        if (response.getCode() >= 200 && response.getCode() < 400) {
            System.out.println(response.getText());
        } else {
            System.err.println(response.getText());
        }
    }

    private static CommandDataProcessor getCommandDataProcessor() {
        Validator validator = new StudyGroupValidator(new StudyGroupEntityBuilder());
        CommandDataProcessor processor = new CommandDataProcessor(validator);

        processor.addCommandData(new HelpCommandData());
        processor.addCommandData(new ExitDatabaseCommandData());
        processor.addCommandData(new AddCommandData());
        processor.addCommandData(new AddIfMinData());
        processor.addCommandData(new ClearCommandData());
        processor.addCommandData(new CountLessThanFormOfEducationCommandData());
        processor.addCommandData(new InfoCommandData());
        processor.addCommandData(new PrintDescendingCommandData());
        processor.addCommandData(new RemoveByIdCommandData());
        processor.addCommandData(new RemoveGreaterCommandData());
        processor.addCommandData(new RemoveLowerCommandData());
        processor.addCommandData(new ShowCommandData());
        processor.addCommandData(new SumOfAverageMarkCommandData());
        processor.addCommandData(new UndoCommandData());
        processor.addCommandData(new UpdateByIdCommandData());
        processor.addCommandData(new GetMinStudentCountCommandData());

        return processor;
    }

    private static void addClientSideCommands(CommandDataProcessor processor, DatabaseConnection connection) {
        processor.addClientCommand(new HelpCommandImpl(processor));
        processor.addClientCommand(new HistoryCommandImpl(processor));
        processor.addClientCommand(new ExitDatabaseCommandImpl());
        processor.addClientCommand(new UpdateIdClientSideCommandImpl(connection, processor.getValidator()));
        processor.addClientCommand(new ExecuteScriptCommand(connection,processor));
        processor.addClientCommand(new AddIfMinCommandImpl(connection,processor.getValidator()));
    }
}
