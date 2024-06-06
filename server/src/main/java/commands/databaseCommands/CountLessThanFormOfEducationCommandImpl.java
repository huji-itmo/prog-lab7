package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;
import dataStructs.FormOfEducation;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import database.StudyGroupDatabase;

import java.util.List;
import java.util.Optional;

public class CountLessThanFormOfEducationCommandImpl extends DatabaseCommandImpl {

    private final StudyGroupDatabase database;

    public CountLessThanFormOfEducationCommandImpl(StudyGroupDatabase database) {
        this.database = database;
        setCommandData(new CountLessThanFormOfEducationCommandData());
    }

    @Override
    public CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session) throws CommandException {
        try {

            FormOfEducation formOfEducation = Optional.ofNullable(packedArgs.get(0))
                    .filter(FormOfEducation.class::isInstance)
                    .map(FormOfEducation.class::cast)
                    .orElseThrow(() -> new CommandException("Bad data! class: FormOfEducation"));

            long count = database.getCountLessThanFormOfEducation(formOfEducation);

            return CommandExecutionResult.success("The count is " + count + "!");
        } catch (IllegalArgumentException | CommandException e) {
            return CommandExecutionResult.badRequest(new IllegalCommandSyntaxException(e.getMessage(), getCommandData()).getMessage());

        }

    }
}
