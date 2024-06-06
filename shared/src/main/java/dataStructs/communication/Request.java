package dataStructs.communication;

import commands.CommandData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class Request implements Serializable {
    CommandData commandData;

    List<Object> params;

    @Setter
    SessionByteArray sessionByteArray;

    @Serial
    private static final long serialVersionUID = 1L;

    public Request(CommandData commandData, List<Object> params) {
        this.commandData = commandData;
        this.params = params;
    }
}
