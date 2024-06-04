package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;
import dataStructs.FormOfEducation;

public class CountLessThanFormOfEducationCommandData extends CommandData {
    public CountLessThanFormOfEducationCommandData() {
        super("count_less_than_form_of_education",
                "Counts all element less than ordinal \"DISTANCE_EDUCATION|FULL_TIME_EDUCATION|EVENING_CLASSES\"",
                new CommandArgument[] {
                        new CommandArgument(FormOfEducation.class,
                                "DISTANCE_EDUCATION|FULL_TIME_EDUCATION|EVENING_CLASSES",
                                "Values are placed in ascending order",
                                false)
                });
    }
}
