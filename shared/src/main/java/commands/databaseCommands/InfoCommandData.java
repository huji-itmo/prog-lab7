package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class InfoCommandData extends CommandData {
    public InfoCommandData() {
        super("info",
                "Shows info about current collection.",
                new CommandArgument[] {});
    }
}
