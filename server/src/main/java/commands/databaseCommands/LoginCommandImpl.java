package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.auth.LoginCommandData;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.Database;
import database.StudyGroupDatabaseInstance;

import java.util.List;

public class LoginCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabaseInstance database;
    public LoginCommandImpl(StudyGroupDatabaseInstance database) {
        this.database = database;
        setCommandData(new LoginCommandData());
    }
    @Override
    public String execute(List<Object> packedArgs) throws CommandException {
        return Long.toString(database.login((String) packedArgs.get(0), (String) packedArgs.get(1)));
    }
}
