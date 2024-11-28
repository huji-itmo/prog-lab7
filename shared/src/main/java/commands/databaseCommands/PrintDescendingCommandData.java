package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class PrintDescendingCommandData extends CommandData {
    public PrintDescendingCommandData() {
        super("print_descending",
                "Prints out all database entries in descending order by id.",
                new CommandArgument[] {});
    }
}
