import connection.ConnectionHandler;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger("Server");
    static {
        logger.setLevel(Level.FINEST);
    }

    private final ServerSocket serverSocket;

    private final Function<Request, String> onNewMessageHandler;

    public Server(int port, Function<Request, String> onNewMessageHandler) {
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

                logger.info("connection.Client " + socket.getInetAddress() + " connected");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ConnectionHandlerLogProxy extends ConnectionHandler {
        public ConnectionHandlerLogProxy(Socket socket) {
            super(socket);
        }

        @Override
        public void sendResponseBlocking(ServerResponse message) throws IOException {
            super.sendResponseBlocking(message);

            logger.info("Sent message to "+ getCurrentSocket().getInetAddress() + ".\n" + message);

        }


        public Request readRequestBlocking() throws IOException {
            Request request = super.readRequestBlocking();

            logger.info("Got request from"+ getCurrentSocket().getInetAddress() + ".\n" + request);

            return request;
        }
    }
}
