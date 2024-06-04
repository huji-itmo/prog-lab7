package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RegisterCommandImpl extends DatabaseCommandImpl {
    private final Database<?,?> database;

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {

        database.registerNewUser((String)packedArgs.get(0), (String)packedArgs.get(0));

        return null;
    }
}
