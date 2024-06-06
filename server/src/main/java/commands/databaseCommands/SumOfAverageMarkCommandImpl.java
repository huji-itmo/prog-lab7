package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.StudyGroupDatabase;

import java.util.List;

public class SumOfAverageMarkCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabase database;

    public SumOfAverageMarkCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new SumOfAverageMarkCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {

        return CommandExecutionResult.success(database.getSumOfAverageMark());
    }
}
