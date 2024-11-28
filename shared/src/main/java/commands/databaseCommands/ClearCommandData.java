package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class ClearCommandData extends CommandData {
    public ClearCommandData() {
        super("clear",
                "Clears out all entries in database.",
                new CommandArgument[] {});
    }
}
