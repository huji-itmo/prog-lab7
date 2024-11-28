package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class ExistsAndBelongsToMeCommandData extends CommandData {
    public ExistsAndBelongsToMeCommandData() {
        super("exists_and_belongs", "not visible to user", new CommandArgument[]{
                new CommandArgument(Long.class, "id", "", false),
        });
    }
}
