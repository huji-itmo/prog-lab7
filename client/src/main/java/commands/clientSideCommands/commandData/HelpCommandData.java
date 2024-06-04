package commands.clientSideCommands.commandData;

import commands.CommandArgument;
import commands.CommandData;

public class HelpCommandData extends CommandData {
    public HelpCommandData() {
        super("help",
                "Shows all commands if no arguments provided. Displays info about command you provided.",
                 new CommandArgument[] {
                        new CommandArgument(String.class, "command_name", "Name of the command you want to know more about.", true)
        });
    }
}
