package connection;

import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Getter
public class DatabaseConnection {
    @Setter
    long clientId;

    private Socket socket;

    private Thread thread;

    private final List<Consumer<ServerResponse>> messageAcceptedDelegate = new ArrayList<>();

    private ObjectOutputStream objectOutputStream;

    private boolean oneShotRequest;
    private ServerResponse oneShotResponse;

    public void addNewMessageHandler(Consumer<ServerResponse> requestSupplier) {
        messageAcceptedDelegate.add(requestSupplier);
    }

    public boolean connect(InetAddress addr, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(addr, port), 10000);

            if (!socket.isConnected()) {
                return false;
            }

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public void stop() {
        thread.interrupt();
    }


    public void startMessageAcceptingThread() {
        thread = new Thread(() -> {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                while (!thread.isInterrupted()) {
                    Object obj = objectInputStream.readObject();

                    if (!(obj instanceof ServerResponse serverResponse)) {
                        continue;
                    }

                    if (oneShotRequest) {
                        oneShotResponse = serverResponse;
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                        continue;
                    }

                    for (Consumer<ServerResponse> consumer : messageAcceptedDelegate) {
                        consumer.accept(serverResponse);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Server disconnected... " + e.getMessage());
                stop();
            }
        });

        thread.start();

    }

    public void send(Request ok) {
        try {
            ok.setClientId(clientId);
            objectOutputStream.writeObject(ok);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    final Object lock = new Object();
    public ServerResponse sendOneShot(Request ok) {

        oneShotRequest = true;
        try {
            synchronized (lock) {
                send(ok);
                lock.wait();
                //notified when response came
                oneShotRequest = false;

                return oneShotResponse;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
