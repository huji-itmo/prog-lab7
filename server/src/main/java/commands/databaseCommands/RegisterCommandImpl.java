package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.auth.RegisterCommandData;
import commands.exceptions.CommandException;
import database.Database;
import lombok.AllArgsConstructor;

import java.security.SecureRandom;
import java.util.List;


public class RegisterCommandImpl extends DatabaseCommandImpl {
    private final Database<?,?> database;

    public RegisterCommandImpl(Database<?,?> database) {
        setCommandData(new RegisterCommandData());
        this.database = database;
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {

        return Long.toString(database.registerNewUser((String)packedArgs.get(0), (String)packedArgs.get(0)));
    }
}
