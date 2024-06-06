package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;

public class RemoveGreaterCommandImpl extends DatabaseCommandImpl {

    private final Database<?, Long> database;

    public RemoveGreaterCommandImpl(Database<?, Long> decorator) {
        this.database = decorator;
        setCommandData(new RemoveGreaterCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {

        try {
            long count = database.removeGreaterOrLowerThanPrimaryKey((Long) packedArgs.get(0), true, session).size();

            return CommandExecutionResult.success("Removed " + count + " elements.");

        }
        catch (NumberFormatException e) {
            return CommandExecutionResult.badRequest(e.getMessage());
        }
    }
}
