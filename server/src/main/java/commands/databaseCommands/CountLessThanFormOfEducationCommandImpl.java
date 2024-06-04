package commands.databaseCommands;

import commands.DatabaseCommandImpl;
import commands.exceptions.CommandException;
import commands.exceptions.IllegalCommandSyntaxException;
import dataStructs.FormOfEducation;
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
    public String execute(List<Object> packedArgs, String session) throws CommandException {
        try {

            FormOfEducation formOfEducation = Optional.ofNullable(packedArgs.get(0))
                    .filter(FormOfEducation.class::isInstance)
                    .map(FormOfEducation.class::cast)
                    .orElseThrow(() -> new CommandException("Bad data! class: FormOfEducation"));

            long count = database.getCountLessThanFormOfEducation(formOfEducation);

            return "The count is " + count + "!";
        } catch (IllegalArgumentException e) {
            throw new IllegalCommandSyntaxException(e.getMessage(), getCommandData());
        }

    }
}
