package commands;

import commands.auth.RegisterCommandData;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.Database;
import database.StudyGroupDatabaseInstance;

import java.util.List;

public class RegisterCommandImpl extends DatabaseCommandImpl {
    public RegisterCommandImpl(StudyGroupDatabaseInstance database) {
        setCommandData(new RegisterCommandData());
    }
    @Override
    public String execute(List<Object> packedArgs, Database<StudyGroup, Long> database) throws CommandException {

        return Long.toString(database.registerNewUser((String) packedArgs.get(0), (String) packedArgs.get(1)));
    }
}
