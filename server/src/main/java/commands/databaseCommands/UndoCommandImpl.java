package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;

import java.util.List;

public class UndoCommandImpl extends DatabaseCommandImpl {
    private final Database<?, ?> database;

    public UndoCommandImpl(Database<?, ?> database) {
        this.database = database;
        setCommandData(new UndoCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {
        boolean res = database.popUndoStackWithSession(session);

        if (!res) {
            throw new CommandException("Nothing to undo.");
        }
        return "Success.";
    }
}
