package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.Database;
import lombok.AllArgsConstructor;

import java.security.SecureRandom;
import java.util.List;

@AllArgsConstructor
public class RegisterCommandImpl extends DatabaseCommandImpl {
    private final Database<?,?> database;

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {

        return Long.toString(database.registerNewUser((String)packedArgs.get(0), (String)packedArgs.get(0)));
    }
}
