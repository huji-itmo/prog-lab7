package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;

import java.util.List;

public class InfoCommandImpl extends DatabaseCommandImpl {
    Database<?, ?> database;
    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {
        return database.getInfo();
    }

    public InfoCommandImpl(Database<?, ?> database) {
        this.database = database;
        setCommandData(new InfoCommandData());
    }
}
