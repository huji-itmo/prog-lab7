package commands;

import commands.auth.LoginCommandData;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.Database;
import database.StudyGroupDatabaseInstance;

import java.util.List;

public class LoginCommandImpl extends DatabaseCommandImpl {
    public LoginCommandImpl(StudyGroupDatabaseInstance database) {
        setCommandData(new LoginCommandData());
    }
    @Override
    public String execute(List<Object> packedArgs, Database<StudyGroup, Long> database) throws CommandException {
        return Long.toString(database.login((String) packedArgs.get(0), (String) packedArgs.get(1)));
    }
}
