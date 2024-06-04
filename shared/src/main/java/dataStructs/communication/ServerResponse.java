package dataStructs.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
public class ServerResponse implements Serializable {
    int code;
    String text;

    @Serial
    private static final long serialVersionUID=1L;
}
