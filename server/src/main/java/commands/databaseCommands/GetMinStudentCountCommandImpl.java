package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import database.StudyGroupDatabase;

import java.util.List;
import java.util.OptionalInt;

public class GetMinStudentCountCommandImpl extends DatabaseCommandImpl {

    private final StudyGroupDatabase collectionDatabase;

    public GetMinStudentCountCommandImpl(StudyGroupDatabase collectionDatabase) {
        this.collectionDatabase = collectionDatabase;
        setCommandData(new GetMinStudentCountCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs, String session) throws CommandException {

        OptionalInt res = collectionDatabase.getMinStudentCount();

        return res.toString();
    }
}