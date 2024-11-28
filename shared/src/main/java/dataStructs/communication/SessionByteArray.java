package dataStructs.communication;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

@EqualsAndHashCode
public class SessionByteArray implements Serializable {

    private final byte[] bytes;

    public SessionByteArray(byte[] session) {
        this.bytes = Arrays.copyOf(session, 16);
    }

    @Serial
    private static final long serialVersionUID = 1L;
}