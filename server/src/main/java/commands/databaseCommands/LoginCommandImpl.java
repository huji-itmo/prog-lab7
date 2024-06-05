package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.auth.LoginCommandData;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import database.StudyGroupDatabase;

import java.util.List;

public class LoginCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabase database;
    public LoginCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new LoginCommandData());
    }
    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, String session) throws CommandException {
        return CommandExecutionResult.success(database.login((String) packedArgs.get(0), (String) packedArgs.get(1)));
    }
}
