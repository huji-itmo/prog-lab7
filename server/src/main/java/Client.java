import dataStructs.communication.Request;
import database.Database;
import lombok.AllArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
public class Client {

    private final ConnectionHandler handler;
    private final String session;

    BiConsumer<Request, ConnectionHandler> RequestHandler;
}
