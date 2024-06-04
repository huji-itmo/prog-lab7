package commands.clientSideCommands;

import commands.databaseCommands.ExistsByIdCommandData;
import commands.databaseCommands.UpdateByIdCommandData;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;
import connection.DatabaseConnection;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import validator.Validator;

import java.util.List;

public class UpdateIdClientSideCommandImpl extends ClientSideCommand {
    private final DatabaseConnection connection;
    private final Validator validator;

    public UpdateIdClientSideCommandImpl(DatabaseConnection connection, Validator validator) {
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

            ServerResponse responseExists = connection.sendOneShot(new Request(new ExistsByIdCommandData(), List.of(idValue)));

            if (responseExists.getText().equals("false")) {
                throw new CommandException("Element with id " + idValue + " doesn't exists!");
            }

            List<Object> packedArguments = validator.checkSyntax(new UpdateByIdCommandData(), args);

            ServerResponse responseUpdateById = connection.sendOneShot(new Request(new UpdateByIdCommandData(), packedArguments));

            if (responseUpdateById.getCode() < 200 || responseUpdateById.getCode() >= 300) {
                throw new CommandException(responseUpdateById.getText());
            }

            return responseUpdateById.getText();

        } catch (NumberFormatException e) {
            throw new IllegalCommandSyntaxException("Id should be a number!", getCommandData());
        }
    }
}
