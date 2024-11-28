package commands;

import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.SessionByteArray;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public abstract class DatabaseCommandImpl {
    public abstract CommandExecutionResult execute(List<Object> packedArgs, SessionByteArray session);
    @Setter(AccessLevel.PROTECTED)
    public CommandData commandData;

}
