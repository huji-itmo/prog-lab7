package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;

import java.util.List;


public class ExistsIdCommandImpl extends DatabaseCommandImpl {
    private final Database<?,Long> database;

    public ExistsIdCommandImpl(Database<?, Long> database) {
        this.database = database;
        setCommandData(new ExistsByIdCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {
        return Boolean.toString(database.existsById((Long) packedArgs.get(0)));
    }
}
