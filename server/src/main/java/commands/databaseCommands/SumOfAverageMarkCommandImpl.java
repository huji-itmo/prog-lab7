package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.StudyGroupDatabaseInstance;

import java.util.List;

public class SumOfAverageMarkCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabaseInstance database;

    public SumOfAverageMarkCommandImpl(StudyGroupDatabaseInstance database) {
        this.database = database;
        setCommandData(new SumOfAverageMarkCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {

        return Double.toString(database.getSumOfAverageMark());
    }
}
