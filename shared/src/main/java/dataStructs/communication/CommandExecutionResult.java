package dataStructs.communication;

import dataStructs.StudyGroup;
import dataStructs.communication.enums.ResponsePurpose;
import dataStructs.communication.enums.ResponseType;
import dataStructs.exceptions.WrongResponseTypeAccess;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

@Data
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandExecutionResult implements Serializable {
    int code;
    Object object;

    ResponseType responseType;
    @Builder.Default
    ResponsePurpose responsePurpose = ResponsePurpose.INFO;

    public static final String CONFIRM_LINE = "Do you wish to continue? (Y/N)";

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

    public static CommandExecutionResult success(SessionByteArray sessionByteArray) {
        return CommandExecutionResult.builder().code(200).object(sessionByteArray).responseType(ResponseType.SESSION_BYTE_ARRAY).build();
    }

    public static CommandExecutionResult confirm(List<StudyGroup> list) {
        return CommandExecutionResult.builder().code(200).responsePurpose(ResponsePurpose.CONFIRM_DELETE).object(list).responseType(ResponseType.LIST_OF_STUDY_GROUP).build();
    }

    public void printResult() {
        if (responsePurpose == ResponsePurpose.CONFIRM_DELETE) {
            if (responseType != ResponseType.LIST_OF_STUDY_GROUP) {
                System.err.println("Not implemented confirm type" + responseType.name());
            }

            System.out.println("Following elements will be deleted:");
            List<StudyGroup> list = ((List<StudyGroup>) object);

            if (list.isEmpty()) {
                System.out.println("Result list is empty.");
            } else {
                list.forEach(System.out::println);
            }
            System.out.println(CONFIRM_LINE);
            return;
        }

        Consumer<Object> printConsumer = System.out::println;

        if (getCode() != 200) {
            printConsumer = System.err::println;
        }

        if (responseType == ResponseType.LIST_OF_STUDY_GROUP) {
            List<StudyGroup> list = ((List<StudyGroup>) object);

            if (list.isEmpty()) {
                printConsumer.accept("Result list is empty.");
            } else {
                list.forEach(printConsumer);
            }
        } else {
            printConsumer.accept(object);
        }
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

    public SessionByteArray getSessionByteArray() {
        if (responseType != ResponseType.SESSION_BYTE_ARRAY) {
            throw new WrongResponseTypeAccess("Wrong method used to access response! Expected: " + responseType.name());
        }

        return (SessionByteArray) object;
    }

    private CommandExecutionResult () {}
}
