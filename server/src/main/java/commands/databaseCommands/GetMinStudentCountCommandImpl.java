package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import database.StudyGroupDatabase;

import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class GetMinStudentCountCommandImpl extends DatabaseCommandImpl {

    private final StudyGroupDatabase collectionDatabase;

    public GetMinStudentCountCommandImpl(StudyGroupDatabase collectionDatabase) {
        this.collectionDatabase = collectionDatabase;
        setCommandData(new GetMinStudentCountCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, String session) throws CommandException {

        OptionalLong res = collectionDatabase.getMinStudentCount();

        return CommandExecutionResult.success(res.orElse(0L));
    }
}