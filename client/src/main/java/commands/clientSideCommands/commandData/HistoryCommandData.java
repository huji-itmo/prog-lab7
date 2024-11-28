package commands.clientSideCommands.commandData;

import commands.CommandArgument;
import commands.CommandData;

public class HistoryCommandData extends CommandData {
    public HistoryCommandData() {
        super("history",
                "Prints out names of the last 11 commands that are executed.",
                new CommandArgument[] {});
    }
}
