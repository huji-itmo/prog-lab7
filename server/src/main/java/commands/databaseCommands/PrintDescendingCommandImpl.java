package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.Database;

import java.util.List;

public class PrintDescendingCommandImpl extends DatabaseCommandImpl {

    private final Database<StudyGroup, ?> database;

    public PrintDescendingCommandImpl(Database<StudyGroup, ?> database) {
        this.database = database;
        setCommandData(new PrintDescendingCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        List<StudyGroup> list = database.getElementsDescendingByPrimaryKey();
        if (list.isEmpty()) {
            return CommandExecutionResult.badRequest("The database is empty!");
        }
        return CommandExecutionResult.success(list);
    }
}
