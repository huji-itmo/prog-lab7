package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class RemoveByIdCommandData extends CommandData {
    public RemoveByIdCommandData() {
        super("remove_by_id",
                "Finds an element with specified id and removes it.",
                new CommandArgument[] {
                    new CommandArgument(
                            Long.class,
                            "id",
                            "The id of an element that should be removed.",
                            false)
        });
    }
}
