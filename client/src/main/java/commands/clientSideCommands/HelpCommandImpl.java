package commands.clientSideCommands;

import commands.*;
import commands.clientSideCommands.commandData.HelpCommandData;
import commands.exceptions.CommandDoesntExistsException;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;

import java.util.Map;

public class HelpCommandImpl extends ClientSideCommand {
    private final CommandDataProcessor commandProcessor;

    public HelpCommandImpl(CommandDataProcessor processor) {
        commandProcessor = processor;
        setCommandData(new HelpCommandData());
    }
    @Override
    public String execute(String args) throws CommandException {
        if (args.isBlank()) {
            return showAllCommands();
        }
        else if (!args.contains(" ")) {
            if (!commandProcessor.getCommands().containsKey(args)) {
                throw new CommandDoesntExistsException(args);
            }

            return showInfoAboutCommand(commandProcessor.getCommands().get(args));
        }
        else {
            throw new IllegalCommandSyntaxException("Help command expects only one or none arguments.", getCommandData());
        }
    }

    public String showInfoAboutCommand(CommandData command) {
        StringBuilder builder = new StringBuilder(command.getDescription());

        if (command.getArguments().length == 0) {
            return builder.toString();
        }

        builder.append("\n\n    Syntax: ")
                .append(command.getName())
                .append(" ")
                .append(command.getArgumentsSyntax())
                .append("\n");

        for (CommandArgument argument : command.getArguments()) {
            builder.append("\n")
                    .append(" ".repeat(4))
                    .append(argument.getName())
                    .append(argument.isOptional()? " (optional)" : "")
                    .append(" - ")
                    .append(argument.getDescription());
        }

        return builder.toString();
    }

    public String showAllCommands() {

        StringBuilder builder = new StringBuilder();

        for(Map.Entry<String, CommandData> entry: commandProcessor.getCommands().entrySet()) {
            builder.append(entry.getKey());

            for (CommandArgument commandArgument : entry.getValue().getArguments()) {
                builder.append(" ").append(commandArgument);
            }

            builder.append("\n");
        }

        return builder.toString().trim();
    }
}
