package application;

import commands.CommandDataProcessor;
import commands.clientSideCommands.*;
import commands.clientSideCommands.commandData.ExitDatabaseCommandData;
import commands.clientSideCommands.commandData.HelpCommandData;
import commands.databaseCommands.*;
import connection.ConnectionWithServer;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import dataStructs.communication.enums.ResponsePurpose;
import studyGroupSpecific.StudyGroupEntityBuilder;
import studyGroupSpecific.StudyGroupValidator;
import validator.Validator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;

public class ClientApplication {
    static ConsoleManager consoleManager;
    public static void run() {
        ConnectionWithServer connection = new ConnectionWithServer();

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

        SessionByteArray session = new CLIAuth(connection).loginOrRegister();
        connection.setSession(session);

        connection.addNewMessageHandler(getNewMessageHandler());

        CommandDataProcessor processor = getCommandDataProcessor();
        addClientSideCommands(processor, connection);

        consoleManager = new ConsoleManager(processor, connection);

        consoleManager.startCommandLoop();

    }

    public static BiConsumer<CommandExecutionResult, ConnectionWithServer> getNewMessageHandler() {
        return (response, client) -> {
            response.printResult();
            if (response.getResponsePurpose() == ResponsePurpose.CONFIRM_DELETE) {
                consoleManager.waitingForConfirmation = true;
            }
        };
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

    private static void addClientSideCommands(CommandDataProcessor processor, ConnectionWithServer connection) {
        processor.addClientCommand(new HelpCommandImpl(processor));
        processor.addClientCommand(new HistoryCommandImpl(processor));
        processor.addClientCommand(new ExitDatabaseCommandImpl());
        processor.addClientCommand(new UpdateIdClientSideCommandImpl(connection, processor.getValidator()));
        processor.addClientCommand(new ExecuteScriptCommand(connection,processor));
        processor.addClientCommand(new AddIfMinCommandImpl(connection,processor.getValidator()));
    }
}
