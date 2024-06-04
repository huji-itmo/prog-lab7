package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.StudyGroupDatabaseInstance;

import java.util.List;

public class UpdateByIdCommandImpl extends DatabaseCommandImpl {

    private final StudyGroupDatabaseInstance database;

    public UpdateByIdCommandImpl(StudyGroupDatabaseInstance database) {
        this.database = database;
        setCommandData(new UpdateByIdCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {
        try {
            Long id = (Long) packedArgs.get(0);

            return "Updated id: " + id + " with value (" + database.updateElementByPrimaryKey(id, (StudyGroup)packedArgs.get(1)).getValues(", ") + ")";
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }

    }
}
