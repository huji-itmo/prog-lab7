package commands;

import commands.exceptions.CommandException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public abstract class DatabaseCommandImpl {
    public abstract String execute(List<Object> packedArgs, String session) throws CommandException;
    @Setter(AccessLevel.PROTECTED)
    public CommandData commandData;

}
