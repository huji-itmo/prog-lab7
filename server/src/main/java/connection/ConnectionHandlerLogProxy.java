package connection;

import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandlerLogProxy extends ConnectionHandler {
    private final Logger logger;
    public ConnectionHandlerLogProxy(Socket socket) {
        super(socket);

        logger = Logger.getLogger("Client " + socket.getInetAddress().toString());
        logger.setLevel(Level.FINEST);
    }

    @Override
    public void sendResponseBlocking(ServerResponse message) throws IOException {
        super.sendResponseBlocking(message);

        logger.info("Sent message to "+ getCurrentSocket().getInetAddress() + ".\n" + message);

    }

    public Request readRequestBlocking() throws IOException {
        Request request = super.readRequestBlocking();

        logger.info("-------------------------------------------------------------------------");
        logger.info("Got request from"+ getCurrentSocket().getInetAddress() + ".\n" + request);

        return request;
    }
}
