package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import database.Database;

import java.util.List;

public class InfoCommandImpl extends DatabaseCommandImpl {
    Database<?, ?> database;
    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, String session) throws CommandException {
        return CommandExecutionResult.success(database.getInfo());
    }

    public InfoCommandImpl(Database<?, ?> database) {
        this.database = database;
        setCommandData(new InfoCommandData());
    }
}
