package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class ConfirmCommandData extends CommandData {
    public ConfirmCommandData() {
        super("confirm", "not visible to user", new CommandArgument[0]);
    }
}
