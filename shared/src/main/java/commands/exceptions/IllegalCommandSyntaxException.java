package commands.exceptions;

import commands.CommandData;


public class IllegalCommandSyntaxException extends CommandException{
    /**
     * Wrong syntax exception. Generates a message with expected syntax.
     *
     * @param message exception massage.
     */
    public IllegalCommandSyntaxException(String message, CommandData commandImpl) {
        super(message + "\nExpected: " + commandImpl.getArgumentsSyntax());
    }
}
