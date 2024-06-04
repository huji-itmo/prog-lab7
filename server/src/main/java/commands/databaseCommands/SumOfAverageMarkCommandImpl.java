package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.StudyGroupDatabase;

import java.util.List;

public class SumOfAverageMarkCommandImpl extends DatabaseCommandImpl {
    private final StudyGroupDatabase database;

    public SumOfAverageMarkCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new SumOfAverageMarkCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {

        return Double.toString(database.getSumOfAverageMark());
    }
}
