package studyGroupSpecific;

import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import validator.EntityBuilder;

import java.io.StringReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StudyGroupEntityBuilder extends EntityBuilder<StudyGroup> {

    public StudyGroupEntityBuilder() {
        super((line, out, err) -> {
            try {
                String values = line.substring(1,line.length() - 1);

                StudyGroup group = StudyGroup.createFromInput(
                        new Scanner(new StringReader(values.replace(",", "\n"))),
                        (outputDelete)->{},
                        (errLine)-> {
                            throw new CommandException(errLine);
                        });

                if (group == null || group.getValues("\n").equals(values)) {
                    err.accept("Something went wrong when creating new element...");
                    return null;
                }

                out.accept("Success!");
                return group;
            } catch (NoSuchElementException e) {
                throw new CommandException("Bad string!");
            }
        },
        (out, err) ->
            StudyGroup.createFromInput(
                    new Scanner(System.in), System.out::println, System.err::println)
        );
    }
}
