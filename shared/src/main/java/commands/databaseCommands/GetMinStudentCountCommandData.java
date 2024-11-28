package commands.databaseCommands;

import commands.CommandArgument;
import commands.CommandData;

public class GetMinStudentCountCommandData extends CommandData {
    public GetMinStudentCountCommandData() {
        super("get_min_student_count", "Prints out minimum student count from all entries, ot null if database is empty", new CommandArgument[]{});
    }
}
