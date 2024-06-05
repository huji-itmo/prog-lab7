package commands;

import commands.exceptions.CommandException;
import dataStructs.communication.CommandExecutionResult;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public abstract class DatabaseCommandImpl {
    public abstract CommandExecutionResult execute(List<Object> packedArgs, String session);
    @Setter(AccessLevel.PROTECTED)
    public CommandData commandData;

}
