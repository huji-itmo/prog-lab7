package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.StudyGroupDatabaseInstance;

import java.util.List;
import java.util.OptionalInt;

public class GetMinStudentCountCommandImpl extends DatabaseCommandImpl {

    private final StudyGroupDatabaseInstance collectionDatabase;

    public GetMinStudentCountCommandImpl(StudyGroupDatabaseInstance collectionDatabase) {
        this.collectionDatabase = collectionDatabase;
        setCommandData(new GetMinStudentCountCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {

        OptionalInt res = collectionDatabase.getMinStudnentCount();

        return res.toString();
    }
}