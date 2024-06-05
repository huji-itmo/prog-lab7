package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import database.Database;

import java.util.List;

public class ClearCommandImpl extends DatabaseCommandImpl {
    private final Database<?,?> database;

    public ClearCommandImpl(Database<?,?> database) {
        this.database = database;
        setCommandData(new ClearCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, String session) throws CommandException {
        database.clear(session);
        return CommandExecutionResult.success("Cleared.");
    }
}
