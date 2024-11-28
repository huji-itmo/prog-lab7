package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.auth.RegisterCommandData;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;


public class RegisterCommandImpl extends DatabaseCommandImpl {
    private final Database<?,?> database;
    public RegisterCommandImpl(Database<?,?> database) {
        setCommandData(new RegisterCommandData());
        this.database = database;
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        try {
            String newUserName = database.registerNewUser((String) packedArgs.get(0), (String) packedArgs.get(1));

            return CommandExecutionResult.success(newUserName);
        } catch (CommandException e) {
            return CommandExecutionResult.badRequest(e.getMessage());
        }

    }
}
