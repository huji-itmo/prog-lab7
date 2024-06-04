package commands;

import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import database.Database;
import database.StudyGroupDatabaseInstance;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public abstract class DatabaseCommandImpl {
    public abstract String execute(List<Object> packedArgs) throws CommandException;
    @Setter(AccessLevel.PROTECTED)
    public CommandData commandData;

}
