package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.StudyGroupDatabase;

import java.util.List;


public class AddCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabase database;


    public AddCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new AddCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {
        try {
            StudyGroup element = (StudyGroup) packedArgs.get(0);

            database.addElement(element, session);

            return "Added element with id: " +  element.getId() + " (" + element.getValues(",") + ")";

        } catch (ClassCastException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
