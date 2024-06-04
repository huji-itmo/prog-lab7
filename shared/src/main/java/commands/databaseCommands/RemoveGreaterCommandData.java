package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class RemoveGreaterCommandData extends CommandData {
    public RemoveGreaterCommandData() {
        super("remove_greater", "Removes an elements with id greater than specified.",
                new CommandArgument[] {
                    new CommandArgument(Long.class, "id", "the id that all elements with greater id will be removed", false)
        });
    }
}
