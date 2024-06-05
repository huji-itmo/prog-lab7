package commands;

import commands.databaseCommands.*;
import database.StudyGroupDatabase;

import java.util.HashMap;
import java.util.Map;

public class CommandImplMap {
    public Map<CommandData, DatabaseCommandImpl> map = new HashMap<>();

    public void addDatabaseCommands(StudyGroupDatabase database) {
        putCommand(new AddCommandImpl(database));
        putCommand(new ClearCommandImpl(database));
        putCommand(new CountLessThanFormOfEducationCommandImpl(database));
        putCommand(new InfoCommandImpl(database));
        putCommand(new PrintDescendingCommandImpl(database));
        putCommand(new RemoveByIdCommandImpl(database));
        putCommand(new RemoveGreaterCommandImpl(database));
        putCommand(new RemoveLowerCommandImpl(database));
        putCommand(new ShowCommandImpl(database));
        putCommand(new SumOfAverageMarkCommandImpl(database));
        putCommand(new UndoCommandImpl(database));
        putCommand(new UpdateByIdCommandImpl(database));
        putCommand(new ExistsIdCommandImpl(database));
        putCommand(new GetMinStudentCountCommandImpl(database));

        putCommand(new RegisterCommandImpl(database));
        putCommand(new LoginCommandImpl(database));
    }

    public void putCommand(DatabaseCommandImpl impl) {
        map.put(impl.getCommandData(), impl);
    }
}
