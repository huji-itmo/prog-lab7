package commands.clientSideCommands.commandData;

import commands.CommandArgument;
import commands.CommandData;

import java.nio.file.Path;

public class ExecuteScriptCommandData extends CommandData {
    public ExecuteScriptCommandData() {
        super("execute_script", "Executes given file line by line.", new CommandArgument[] {
                        new CommandArgument(Path.class, "file_path", "Path to script file", false)
                });
    }
}
