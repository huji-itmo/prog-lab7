package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class RemoveLowerCommandData extends CommandData {
    public RemoveLowerCommandData() {
        super("remove_lower", "Removes an elements with id lower than specified.",
                new CommandArgument[] {
                        new CommandArgument(Long.class,"id", "the id that all elements with lower id will be removed", false)
                });
    }
}
