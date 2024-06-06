package commands.clientSideCommands;

import commands.databaseCommands.AddCommandData;
import commands.databaseCommands.AddIfMinData;
import commands.databaseCommands.GetMinStudentCountCommandData;
import commands.exceptions.CommandException;
import connection.ConnectionWithServer;
import dataStructs.StudyGroup;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import validator.Validator;

import java.util.List;

public class AddIfMinCommandImpl extends ClientSideCommand{

    private final ConnectionWithServer connection;

    private final Validator validator;

    public AddIfMinCommandImpl(ConnectionWithServer connection, Validator validator) {
        this.connection = connection;
        this.validator = validator;
        setCommandData(new AddIfMinData());
    }

    @Override
    public String execute(String args) {
        CommandExecutionResult responseGetMin = connection.sendOneShot(new Request(new GetMinStudentCountCommandData(), List.of()));

        if (responseGetMin.getCode() != 200) {
            throw new CommandException(responseGetMin.getText());
        }

        System.out.println("Current min student count: " + responseGetMin.getLong());

        List<Object> packedArguments = validator.checkSyntax(new AddIfMinData(), args);

        if (!(packedArguments.get(0) instanceof StudyGroup newGroup)) {
            throw new CommandException("wtf");
        }

        if (Integer.toUnsignedLong(newGroup.getStudentsCount()) >= responseGetMin.getLong()) {
            return "Student count is not min.";
        }

        CommandExecutionResult responseAddIfMin = connection.sendOneShot(new Request(new AddCommandData(), packedArguments));

        if (responseAddIfMin.getCode() != 200) {
            throw new CommandException(responseAddIfMin.getText());
        }

        return responseAddIfMin.getText();
    }
}
