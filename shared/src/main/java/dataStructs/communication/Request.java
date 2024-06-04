package dataStructs.communication;

import commands.CommandData;
import lombok.*;

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
    long clientId;

    public Request(CommandData commandData, List<Object> params) {
        this.commandData = commandData;
        this.params = params;
        clientId = -1;
    }

    @Serial
    private static final long serialVersionUID=1L;
}
