package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;


public class ExistsIdCommandImpl extends DatabaseCommandImpl {
    private final Database<?,Long> database;

    public ExistsIdCommandImpl(Database<?, Long> database) {
        this.database = database;
        setCommandData(new ExistsByIdCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        return CommandExecutionResult.success(database.existsById((Long) packedArgs.get(0)));
    }
}
