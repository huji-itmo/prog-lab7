package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import javax.persistence.EntityExistsException;
import java.util.List;

public class UndoCommandImpl extends DatabaseCommandImpl {
    private final Database<?, ?> database;

    public UndoCommandImpl(Database<?, ?> database) {
        this.database = database;
        setCommandData(new UndoCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {

        try {
            boolean res = database.popUndoStackWithSession(session);

            if (!res) {
                return CommandExecutionResult.success("Nothing to undo.");
            }
            return CommandExecutionResult.success("Success.");
        } catch (EntityExistsException e) {
            return CommandExecutionResult.badRequest("Can't delete entity because it already exsists.");
        }

    }
}
