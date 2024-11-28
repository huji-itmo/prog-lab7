package commands.clientSideCommands;

import commands.CommandDataProcessor;
import commands.clientSideCommands.commandData.ExecuteScriptCommandData;
import commands.exceptions.CommandException;
import commands.exceptions.RecursionIsNotSupportedException;
import connection.ConnectionWithServer;
import dataStructs.communication.CommandExecutionResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;

public class ExecuteScriptCommand extends ClientSideCommand {

    private final ConnectionWithServer connection;
    private final CommandDataProcessor dataProcessor;
    private final Stack<Path> scriptPathCallStack = new Stack<>();

    public ExecuteScriptCommand(ConnectionWithServer connection, CommandDataProcessor dataProcessor) {
        this.connection = connection;
        this.dataProcessor = dataProcessor;
        setCommandData(new ExecuteScriptCommandData());
    }

    @Override
    public String execute(String args) throws CommandException {
        Path pathToNewScript = Path.of(args);

        try (BufferedReader reader = Files.newBufferedReader(pathToNewScript)){

            if (scriptPathCallStack.contains(pathToNewScript)) {
                scriptPathCallStack.add(pathToNewScript);
                throw new RecursionIsNotSupportedException("execute command script doesn't support recursion!", scriptPathCallStack);
            }

            scriptPathCallStack.add(pathToNewScript);
            reader.lines()
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .forEach(line -> {
                        if (dataProcessor.checkAndExecuteClientSide(line)) {
                            return;
                        }
                        CommandExecutionResult response = connection.sendOneShot(dataProcessor.checkCommandAndCreateRequest(line));
                        response.printResult();
                    });

        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        }

        scriptPathCallStack.remove(pathToNewScript);
        return "Done!";
    }
}
