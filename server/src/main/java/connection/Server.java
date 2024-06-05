package connection;

import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger("connection.Server");
    static {
        logger.setLevel(Level.FINEST);
    }
    private final ServerSocket serverSocket;

    @Setter
    private Function<Request, CommandExecutionResult> executeRequest;
    @Getter
    Map<String, Long> sessionToClientIdMap = new HashMap<>();

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

                ClientHandler clientHandler = new ClientHandler(new ConnectionHandlerLogProxy(socket), this);

                clientHandler.setRequestExecuteFunction(executeRequest);
                clientHandler.setOnThreadException((exception, deadClientHandler) -> {
                    logger.severe(exception.getMessage());
                    sessionToClientIdMap.remove(deadClientHandler.getSession());
                });

                logger.info("Client " + socket.getInetAddress() + " connected");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String createNewSessionWithClientId(Long clientId) {
        String session = generateSecureSession();

        sessionToClientIdMap.put(session, clientId);
        return session;
    }

    SecureRandom random = new SecureRandom();
    private String generateSecureSession() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return new String(bytes);
    }
}
