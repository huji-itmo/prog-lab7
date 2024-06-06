package Server;

import connection.ClientHandler;
import connection.ConnectionHandlerLogProxy;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import dataStructs.communication.SessionByteArray;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger("Server.Server");
    static {
        logger.setLevel(Level.FINEST);
    }
    private final ServerSocket serverSocket;
    @Setter
    private Function<Request, CommandExecutionResult> executeRequest;
    @Getter
    Map<SessionByteArray, ClientHandler> sessionToClientHandlerMap = Collections.synchronizedMap(new HashMap<>());
    @Setter
    private Consumer<SessionByteArray> onNewSession;
    @Setter
    private Consumer<SessionByteArray> onSessionEnd;

    public Server(int port) {
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

                ClientHandler clientHandler = createClient(socket);

                logger.info("Client " + socket.getInetAddress() + " connected");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public SessionByteArray createNewSession(ClientHandler clientHandler) {
        SessionByteArray session = generateSecureSession();

        sessionToClientHandlerMap.put(session, clientHandler);
        return session;
    }

    SecureRandom random = new SecureRandom();

    private SessionByteArray generateSecureSession() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return new SessionByteArray(bytes);
    }

    public ClientHandler createClient(Socket socket) {
        ClientHandler clientHandler = new ClientHandler(new ConnectionHandlerLogProxy(socket), this);

        clientHandler.setOnNewSession((session, handler) -> {
            sessionToClientHandlerMap.put(session, handler);
            onNewSession.accept(session);
        });
        clientHandler.setRequestExecuteFunction(executeRequest);
        clientHandler.setOnThreadException((exception, deadClientHandler) -> {
            logger.severe(exception.getMessage());
            sessionToClientHandlerMap.remove(deadClientHandler.getSession());
            onSessionEnd.accept(deadClientHandler.getSession());
        });

        return clientHandler;
    }
}
