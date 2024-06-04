package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;

import java.util.List;

public class ClearCommandImpl extends DatabaseCommandImpl {
    private final Database<?,?> database;

    public ClearCommandImpl(Database<?,?> database) {
        this.database = database;
        setCommandData(new ClearCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {
        database.clear();
        return "Cleared.";
    }
}
