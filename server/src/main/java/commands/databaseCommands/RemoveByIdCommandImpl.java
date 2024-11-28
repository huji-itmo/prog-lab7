package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;

public class RemoveByIdCommandImpl extends DatabaseCommandImpl {
    private final Database<?, Long> database;

    public RemoveByIdCommandImpl(Database<?, Long> database) {
        this.database = database;
        setCommandData(new RemoveByIdCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        try {
            database.removeElementByPrimaryKey((Long) packedArgs.get(0), session);

            return CommandExecutionResult.success("Element is removed!");
        } catch (NumberFormatException | ClassCastException e) {
            return CommandExecutionResult.badRequest(new IllegalCommandSyntaxException("{id} should be a number!", getCommandData()).getMessage());
        } catch (IllegalArgumentException e) {
            return CommandExecutionResult.badRequest(e.getMessage());
        }
    }
}
