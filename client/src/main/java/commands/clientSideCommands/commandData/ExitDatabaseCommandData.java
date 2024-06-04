package commands.clientSideCommands.commandData;

import commands.CommandArgument;
import commands.CommandData;

public class ExitDatabaseCommandData extends CommandData {
    public ExitDatabaseCommandData() {
        super("exit",
                "Exists interactive mode WITHOUT SAVING.",
                new CommandArgument[] {});
    }
}
