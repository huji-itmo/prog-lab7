package commands.clientSideCommands;

import commands.CommandDataProcessor;
import commands.clientSideCommands.commandData.HistoryCommandData;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;

import java.util.stream.Collectors;

public class HistoryCommandImpl extends ClientSideCommand {
    private final CommandDataProcessor commandProcessor;
    public HistoryCommandImpl(CommandDataProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
        setCommandData(new HistoryCommandData());
    }
    @Override
    public String execute(String args) throws CommandException {
        if (!args.isBlank()) {
            throw new IllegalCommandSyntaxException("History command can only except no arguments.", getCommandData());
        }

        return commandProcessor.getHistory().stream()
                .map(key -> key + "\n")
                .collect(Collectors.joining())
                .trim();
    }
}
