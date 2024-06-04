package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.StudyGroupDatabaseInstance;

import java.util.List;


public class AddCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabaseInstance database;


    public AddCommandImpl(StudyGroupDatabaseInstance database) {
        this.database = database;
        setCommandData(new AddCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {
        try {
            StudyGroup element = (StudyGroup) packedArgs.get(0);

            database.addElement(element);

            return "Added element with id: " +  database.getPrimaryKey().apply(element) + " (" + element.getValues(",") + ")";

        } catch (ClassCastException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
