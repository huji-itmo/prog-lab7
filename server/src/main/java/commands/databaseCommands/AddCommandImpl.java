package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.StudyGroupDatabase;

import java.util.List;


public class AddCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabase database;

    public AddCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new AddCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        try {
            StudyGroup element = (StudyGroup) packedArgs.get(0);

            database.addElement(element, session);

            return CommandExecutionResult.success("Added element with id: " +  element.getId() + " (" + element.getValues(",") + ")");

        } catch (ClassCastException e) {
            return CommandExecutionResult.badRequest(e.getMessage());
        }
    }
}
