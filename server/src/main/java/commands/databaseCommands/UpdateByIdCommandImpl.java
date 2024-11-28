package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.StudyGroupDatabase;

import java.util.List;

public class UpdateByIdCommandImpl extends DatabaseCommandImpl {

    private final StudyGroupDatabase database;

    public UpdateByIdCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new UpdateByIdCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        try {
            Long id = (Long) packedArgs.get(0);

            StudyGroup updatedElement = database.updateElementByPrimaryKey(id, (StudyGroup)packedArgs.get(1), session);

            return CommandExecutionResult.success("Updated id: " + id + " with value (" + updatedElement.getValues(", ") + ")");
        } catch (ClassCastException | IllegalArgumentException e) {
            return CommandExecutionResult.badRequest(e.getMessage());
        }

    }
}
