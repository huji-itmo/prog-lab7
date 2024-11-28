package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;

public class ShowCommandImpl extends DatabaseCommandImpl {
    Database<StudyGroup, ?> database;
    public ShowCommandImpl(Database<StudyGroup, ?> database) {
        this.database = database;
        setCommandData(new ShowCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {

        return CommandExecutionResult.success(database.getElements());
    }
}
