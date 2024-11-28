package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class ShowCommandData extends CommandData {
    public ShowCommandData() {
        super("show",
                "Prints out all entries in database.",
                new CommandArgument[] {});
    }
}
