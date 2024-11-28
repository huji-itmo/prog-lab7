package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class DeclineCommandData extends CommandData {
    public DeclineCommandData() {
        super("decline", "not visible to user", new CommandArgument[0]);
    }
}
