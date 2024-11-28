package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.StudyGroupDatabase;

import java.util.List;

public class ExistsAndBelongsToMeCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabase database;

    public ExistsAndBelongsToMeCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new ExistsAndBelongsToMeCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) {
        Long id = (Long) packedArgs.get(0);
        if (!database.existsById(id)) {
            return CommandExecutionResult.badRequest("Element doesn't exists at id: " + id + "1");
        }

        if (!database.doesBelongToUserUsingSession(id, session)) {
            return CommandExecutionResult.badRequest("Element doesn't belong to you!");
        }

        return CommandExecutionResult.success(true);
    }
}
