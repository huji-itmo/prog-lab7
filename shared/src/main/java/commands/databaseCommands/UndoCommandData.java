package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class UndoCommandData extends CommandData {
    public UndoCommandData() {
        super("undo",
                "Undoes last changes",
                new CommandArgument[] {});
    }
}
