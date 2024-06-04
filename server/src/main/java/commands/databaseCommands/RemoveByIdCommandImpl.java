package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;
import database.Database;

import java.util.List;

public class RemoveByIdCommandImpl extends DatabaseCommandImpl {
    private final Database<?, Long> database;

    public RemoveByIdCommandImpl(Database<?, Long> database) {
        this.database = database;
        setCommandData(new RemoveByIdCommandData());
    }

    @Override
    public String execute(List<Object> packedArgs) throws CommandException {
        try {
            database.removeElementByPrimaryKey((Long) packedArgs.get(0));

            return "Element is removed!";
        } catch (NumberFormatException | ClassCastException e) {
            throw new IllegalCommandSyntaxException("{id} should be a number!", getCommandData());
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
