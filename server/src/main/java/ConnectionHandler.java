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

    private final Socket currentSocket;
    private final BiConsumer<Request, ConnectionHandler> onNewMessageLambdaDefault;

    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream inputStream;

    @Setter
    public BiConsumer<ConnectionHandler, String> onClientDisconnected;
    public BiConsumer<ConnectionHandler, ServerResponse> onSendResponse;


    public ConnectionHandler(Socket socket, BiConsumer<Request, ConnectionHandler> onNewMessage) {

        onNewMessageLambdaDefault = onNewMessage;
        currentSocket = socket;

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(getCurrentSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(ServerResponse message) {
        try {
            objectOutputStream.writeObject(message);
            onSendResponse.accept(this, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Request readRequestBlocking() {
        try {
            Object obj = inputStream.readObject();

            if (!(obj instanceof Request message)) {
                throw new ClassNotFoundException("Wrong class!");
            }

            return message;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
