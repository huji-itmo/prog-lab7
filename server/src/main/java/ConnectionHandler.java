import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.BiConsumer;


@Getter
public class ConnectionHandler {

    private Thread connectionThread;
    private final Socket currentSocket;
    private final BiConsumer<Request, ConnectionHandler> onNewMessageLambdaDefault;

    private final ObjectOutputStream objectOutputStream;

    @Setter
    public BiConsumer<ConnectionHandler, String> onClientDisconnected;
    public BiConsumer<ConnectionHandler, ServerResponse> onSendResponse;


    public ConnectionHandler(Socket socket, BiConsumer<Request, ConnectionHandler> onNewMessage) {

        onNewMessageLambdaDefault = onNewMessage;
        currentSocket = socket;

        connectionThread = new Thread(runnableAcceptingLoop());
        connectionThread.setDaemon(true);
        connectionThread.start();

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        connectionThread.interrupt();
    }

    public void send(ServerResponse message) {
        try {
            objectOutputStream.writeObject(message);
            onSendResponse.accept(this, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Runnable runnableAcceptingLoop() {
        return () -> {
            String disconnectCause = "Thread stopped.";

            try (ObjectInputStream inputStream = new ObjectInputStream(getCurrentSocket().getInputStream())) {

                while (!connectionThread.isInterrupted()) {
                    Object obj = inputStream.readObject();

                    if (obj instanceof Request message) {
                        onNewMessageLambdaDefault.accept(message, this);
                    }
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                disconnectCause = e.getMessage();
            } finally {
                onClientDisconnected.accept(this, disconnectCause);
            }
        };
    }
}
