import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger("Server");

    static {
        logger.setLevel(Level.FINEST);
    }

    private final ServerSocket serverSocket;

    private final BiConsumer<Request, ConnectionHandler> onNewMessageHandler;

    @Setter
    private Consumer<ConnectionHandler> onConnected = (delete) -> {};

    public Server(int port, BiConsumer<Request, ConnectionHandler> onNewMessageHandler) {
        this.onNewMessageHandler = onNewMessageHandler;

        try {
            System.out.println("Server address: " + InetAddress.getLocalHost().toString());

            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startClientAcceptingLoop() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                ConnectionHandler handler = new ConnectionHandlerLogProxy(socket);

                logger.info("Client " + socket.getInetAddress() + " connected");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class ConnectionHandlerLogProxy extends ConnectionHandler {

        public ConnectionHandlerLogProxy(Socket socket) {
            super(socket, (req, han) ->  {
                logger.info("Request from " + socket.getInetAddress() + ": " + req);
                onNewMessageHandler.accept(req, han);
            });

            onSendResponse = ConnectionHandlerLogProxy::logResponse;
            onClientDisconnected = ConnectionHandlerLogProxy::onClientDisconnected;
        }

        public static void logResponse(ConnectionHandler handler, ServerResponse response) {
            logger.info("Send response to " + handler.getCurrentSocket().getInetAddress() + ": " + response);
        }

        public static void onClientDisconnected(ConnectionHandler handler, String cause) {
            logger.info("Client " + handler.getCurrentSocket().getInetAddress() + " disconnected " + cause);
        }
    }
}
