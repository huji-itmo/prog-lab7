package commands.clientSideCommands;


import commands.databaseCommands.ExistsAndBelongsToMeCommandData;
import commands.databaseCommands.UpdateByIdCommandData;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;
import connection.ConnectionWithServer;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import validator.Validator;

import java.util.List;

public class UpdateIdClientSideCommandImpl extends ClientSideCommand {
    private final ConnectionWithServer connection;
    private final Validator validator;

    public UpdateIdClientSideCommandImpl(ConnectionWithServer connection, Validator validator) {
        this.connection = connection;
        this.validator = validator;
        setCommandData(new UpdateByIdCommandData());
    }

    @Override
    public String execute(String args) {

        if (args.isBlank()) {
            throw new IllegalCommandSyntaxException("Args can't be empty!", getCommandData());
        }

        String id = args.split(" ", 2)[0];

        try {
            Long idValue = Long.parseLong(id);

            CommandExecutionResult responseExists = connection.sendOneShot(new Request(new ExistsAndBelongsToMeCommandData(), List.of(idValue)));

            if (responseExists.getCode() != 200) {
                throw new CommandException(responseExists.getText());
            }

            List<Object> packedArguments = validator.checkSyntax(new UpdateByIdCommandData(), args);

            CommandExecutionResult responseUpdateById = connection.sendOneShot(new Request(new UpdateByIdCommandData(), packedArguments));

            if (responseUpdateById.getCode() != 200) {
                throw new CommandException(responseUpdateById.getText());
            }

            return responseUpdateById.getText();

        } catch (NumberFormatException e) {
            throw new IllegalCommandSyntaxException("Id should be a number!", getCommandData());
        }
    }
}
