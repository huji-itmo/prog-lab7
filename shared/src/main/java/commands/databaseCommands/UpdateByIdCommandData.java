package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;
import commands.ElementCommandArgument;

public class UpdateByIdCommandData extends CommandData {
    public UpdateByIdCommandData() {
        super("update_by_id",
                "Updates the element based on it's ID, by creating a new element and replacing with an old one.\"",
                new CommandArgument[] {
                        new CommandArgument(Long.class,"id", "The id of an element that will be replaced.", false),
                        new ElementCommandArgument()
                });
    }
}
