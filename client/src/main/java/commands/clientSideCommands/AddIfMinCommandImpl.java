package commands.clientSideCommands;

import application.ClientApplication;
import commands.CommandDataProcessor;
import commands.databaseCommands.AddCommandData;
import commands.databaseCommands.AddIfMinData;
import commands.databaseCommands.GetMinStudentCountCommandData;
import commands.exceptions.CommandException;
import connection.DatabaseConnection;
import dataStructs.StudyGroup;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import validator.Validator;

import java.util.List;

public class AddIfMinCommandImpl extends ClientSideCommand{

    private final DatabaseConnection connection;

    private final Validator validator;

    public AddIfMinCommandImpl(DatabaseConnection connection, Validator validator) {
        this.connection = connection;
        this.validator = validator;
        setCommandData(new AddIfMinData());
    }

    @Override
    public String execute(String args) {
        ServerResponse responseGetMin = connection.sendOneShot(new Request(new GetMinStudentCountCommandData(), List.of()));

        ClientApplication.newMessageHandler(new ServerResponse(responseGetMin.getCode(), "Current min student count: " + responseGetMin.getText()));

        List<Object> packedArguments = validator.checkSyntax(new AddIfMinData(), args);

        if (!(packedArguments.get(0) instanceof StudyGroup newGroup)) {
            throw new CommandException("wtf");
        }

        if (newGroup.getStudentsCount() >= Integer.parseInt(responseGetMin.getText())) {
            return "Student count is not min.";
        }

        ServerResponse responseAddIfMin = connection.sendOneShot(new Request(new AddCommandData(), packedArguments));

        if (responseAddIfMin.getCode() < 200 || responseAddIfMin.getCode() >= 300) {
            throw new CommandException(responseAddIfMin.getText());
        }

        return responseAddIfMin.getText();
    }
}
