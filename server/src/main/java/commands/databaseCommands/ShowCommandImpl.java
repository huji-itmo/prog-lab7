package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;

import java.util.List;

public class ShowCommandImpl extends DatabaseCommandImpl {
    Database<?, ?> database;
    public ShowCommandImpl(Database<?, ?> database) {
        this.database = database;
        setCommandData(new ShowCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {

        return database.serializeAllElements();
    }
}
