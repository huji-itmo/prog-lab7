package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;
import commands.ElementCommandArgument;

public class AddCommandData extends CommandData {
    public AddCommandData() {
        super("add",
            "Adds a new element based on user's input.",
            new CommandArgument[] {
                    new ElementCommandArgument()
            });
    }
}
