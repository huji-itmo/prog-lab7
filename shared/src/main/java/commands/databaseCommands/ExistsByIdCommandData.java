package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class ExistsByIdCommandData extends CommandData {
    public ExistsByIdCommandData() {
        super("exists_by_id",
                "Prints out true if element with specified id exists or false if not.",
                new CommandArgument[] {new CommandArgument(Long.class,"id","The id of an element that should be removed.",false)});
    }
}
