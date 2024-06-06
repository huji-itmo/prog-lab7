package commands;

import commands.clientSideCommands.ClientSideCommand;
import commands.exceptions.CommandDoesntExistsException;
import commands.exceptions.CommandException;
import dataStructs.communication.Request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import validator.Validator;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class CommandDataProcessor {

    private final Map<String, CommandData> commands = new HashMap<>();
    private final Map<String, ClientSideCommand> clientCommands = new HashMap<>();
    private final ArrayDeque<CommandData> history = new ArrayDeque<>();

    private final Validator validator;

    public void addCommandData(CommandData data) {
        commands.put(data.getName(), data);
    }
    public void addClientCommand(ClientSideCommand command) {
        clientCommands.put(command.getCommandData().getName(), command);
    }

    public Request checkCommandAndCreateRequest(String input) throws CommandException {
        input = input.trim();

        String[] splitCommand = input.split(" ", 2);

        String commandName = splitCommand[0];

        String args = "";

        if (splitCommand.length == 2) {
            args = input.split(" ", 2)[1];
        }

        if (!commands.containsKey(commandName)) {
            throw new CommandDoesntExistsException(commandName);
        }

        CommandData data = commands.get(commandName);

        List<Object> packedArgs = validator.checkSyntax(data, args);

        appendHistory(data);
        return new Request(data, packedArgs);
    }

    public boolean checkAndExecuteClientSide(String input) {
        input = input.trim();

        String[] splitCommand = input.split(" ", 2);

        String commandName = splitCommand[0];

        String args = "";

        if (splitCommand.length == 2)
            args = input.split(" ", 2)[1];


        if (!clientCommands.containsKey(commandName)) {
            return false;
        }
        ClientSideCommand command = clientCommands.get(commandName);

        String out = command.execute(args);
        System.out.println(out);
        appendHistory(command.getCommandData());

        return true;
    }

    public void appendHistory(CommandData data) {
        history.addLast(data);

        while (history.size() >= 12) {
            history.removeFirst();
        }
    }
}
