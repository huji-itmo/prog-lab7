package commands.exceptions;

public class CommandDoesntExistsException extends CommandException {

    /**
        Creates an exception: "commands.Command "{commandName}" doesn't exists."
     @param commandName user input
    */
    public CommandDoesntExistsException(String commandName) {
        super("Command \""+ commandName + "\" doesn't exists.");
    }
}
