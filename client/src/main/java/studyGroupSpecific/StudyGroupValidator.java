package studyGroupSpecific;

import commands.exceptions.IllegalCommandSyntaxException;
import dataStructs.FormOfEducation;
import dataStructs.StudyGroup;
import validator.EntityBuilder;
import validator.Validator;

public class StudyGroupValidator extends Validator {

    private final EntityBuilder<?> builder;

    public StudyGroupValidator(EntityBuilder<?> entityBuilder) {
        this.builder = entityBuilder;

        addAdapter(Long.class, (input, argument, data) -> {
            if (input.isEmpty()) {
                throw new IllegalCommandSyntaxException("The " + argument.getName() + " argument excepts only long type.", data);
            }
            try {
                return Long.parseLong(input.get());
            } catch (NumberFormatException e) {
                throw new IllegalCommandSyntaxException("The " + argument.getName() + " argument excepts only long type.", data);
            }
        });

        addAdapter(StudyGroup.class, (input, argument, data) -> {
            if (input.isEmpty()) {
                return builder.buildFromInput(System.out::println, System.err::println);
            } else {
                return builder.buildFromString(input.get(), System.out::println, System.err::println);
            }
        });

        addAdapter(FormOfEducation.class, (input, argument, data) -> {
            if (input.isEmpty()) {
                throw new IllegalCommandSyntaxException("The " + argument.getName() + " argument can't be empty.", data);
            }

            try {
                return FormOfEducation.valueOf(input.get());
            } catch (NumberFormatException e) {
                throw new IllegalCommandSyntaxException(input.get() + " is not a valid FormOfEducation.", data);
            }
        });
    }
}
