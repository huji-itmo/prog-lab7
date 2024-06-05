package dataStructs.communication;

import dataStructs.StudyGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
public class CommandExecutionResult implements Serializable {
    int code;
    Object object;

    ResponseType responseType;

    @Serial
    private static final long serialVersionUID=1L;

    public static CommandExecutionResult badRequest(String err) {
        return CommandExecutionResult.builder().code(400).object(err).responseType(ResponseType.TEXT).build();
    }
    public static CommandExecutionResult success(String msg) {
        return CommandExecutionResult.builder().code(200).object(msg).responseType(ResponseType.TEXT).build();
    }
    public static CommandExecutionResult success(List<StudyGroup> list) {
        return CommandExecutionResult.builder().code(200).object(list).responseType(ResponseType.LIST_OF_STUDY_GROUP).build();
    }
    public static CommandExecutionResult success(Long num) {
        return CommandExecutionResult.builder().code(200).object(num).responseType(ResponseType.NUMBER_LONG).build();
    }
    public static CommandExecutionResult success(Boolean bool) {
        return CommandExecutionResult.builder().code(200).object(bool).responseType(ResponseType.BOOLEAN).build();
    }

    public String getText() {
        if (responseType != ResponseType.TEXT) {
            throw new WrongResponseTypeAccess("Wrong method used to access response! Expected: " + responseType.name());
        }

        return (String)object;
    }

    public List<StudyGroup> getStudyGroupList() {
        if (responseType != ResponseType.LIST_OF_STUDY_GROUP) {
            throw new WrongResponseTypeAccess("Wrong method used to access response! Expected: " + responseType.name());
        }

        return (List<StudyGroup>) object;
    }

    public Boolean getBoolean() {
        if (responseType != ResponseType.BOOLEAN) {
            throw new WrongResponseTypeAccess("Wrong method used to access response! Expected: " + responseType.name());
        }

        return (Boolean) object;
    }

    public Long getLong() {
        if (responseType != ResponseType.BOOLEAN) {
            throw new WrongResponseTypeAccess("Wrong method used to access response! Expected: " + responseType.name());
        }

        return (Long) object;
    }

    private CommandExecutionResult () {}
}
