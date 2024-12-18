package connection;

import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ConnectionHandler {

    protected final Socket currentSocket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream inputStream;

    public ConnectionHandler(Socket socket) {

        currentSocket = socket;

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendResponseBlocking(CommandExecutionResult message) throws IOException {
        objectOutputStream.writeObject(message);
    }


    public Request readRequestBlocking() throws IOException {
        try {
            Object obj = inputStream.readObject();

            if (!(obj instanceof Request message)) {
                throw new ClassNotFoundException("Wrong class!");
            }

            return message;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
