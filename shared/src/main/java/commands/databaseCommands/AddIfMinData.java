package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;
import commands.ElementCommandArgument;

public class AddIfMinData extends CommandData {
    public AddIfMinData() {
        super("add_if_min",
                "Compares the elements by Students Count and add if the element you created has student count lower than any other element.",
                new CommandArgument[] {
                        new ElementCommandArgument()
                });
    }
}
