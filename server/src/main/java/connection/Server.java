package connection;

import connection.Client;
import connection.ConnectionHandler;
import connection.ConnectionHandlerLogProxy;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import lombok.Setter;
import org.hibernate.Session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
    private Function<Request, String> executeRequest = (delete) -> "";
    @Setter
    private Consumer<Session> onNewSession = (delete) -> {};

    Map<String, Long> sessionToClientIdMap = new HashMap<>();

    List<Client> clientList = new ArrayList<>();

    public Server(int port) {
        try {
            System.out.println("connection.Server address: " + InetAddress.getLocalHost().toString());

            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startClientAcceptingLoop() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                Client client = new Client(new ConnectionHandlerLogProxy(socket), this);

                client.setRequestExecuteFunction(executeRequest);
                client.setOnThreadException(exception -> logger.severe(exception.getMessage()));

                clientList.add(client);

                logger.info("connection.Client " + socket.getInetAddress() + " connected");

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

    SecureRandom random;
    private String generateSecureSession() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return new String(bytes);
    }
}
