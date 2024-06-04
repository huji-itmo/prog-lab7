package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class SumOfAverageMarkCommandData extends CommandData {
    public SumOfAverageMarkCommandData() {
        super("sum_of_average_mark",
                "Sums up all the average marks in database.",
                new CommandArgument[] {});
    }
}
