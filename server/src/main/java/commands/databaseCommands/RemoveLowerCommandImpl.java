package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;

public class RemoveLowerCommandImpl extends DatabaseCommandImpl {
    private final Database<?, Long> database;

    public RemoveLowerCommandImpl(Database<?,Long> decorator) {
        this.database = decorator;
        setCommandData(new RemoveLowerCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {

        try {
            long count = database.removeGreaterOrLowerThanPrimaryKey((Long) packedArgs.get(0), false, session).size();

            return CommandExecutionResult.success("Removed " + count + " elements.");
        }
        catch (NumberFormatException e) {
            return CommandExecutionResult.badRequest("id should be a number!");
        }
    }
}
