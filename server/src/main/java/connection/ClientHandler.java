package connection;

import commands.auth.LoginCommandData;
import commands.auth.RegisterCommandData;
import commands.exceptions.CommandException;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ClientHandler {

    private Thread thread;
    private final ConnectionHandler handler;
    private final Server server;
    @Getter
    private String session;
    @Setter
    Function<Request, String> requestExecuteFunction;
    @Setter
    BiConsumer<Exception, ClientHandler> onThreadException;

    public ClientHandler(ConnectionHandler handler, Server server) {
        this.handler = handler;
        this.server = server;
        thread = new Thread(getRunnable());
        thread.setDaemon(true);
        thread.start();
    }

    private void setSession(String val) {
        session= val;
    }


    public Runnable getRunnable() {
        return () -> {

            try {
                Long clientId = Long.parseLong(getSessionFromClient());

                setSession(server.createNewSessionWithClientId(clientId));

                handler.sendResponseBlocking(new ServerResponse(200, getSession()));

                while (!thread.isInterrupted()) {
                    Request request = handler.readRequestBlocking();

                    if (!request.getSession().equals(getSession())) {
                        handler.sendResponseBlocking(new ServerResponse(400, "Wrong session parameter. Sussy baka impostor"));
                        continue;
                    }

                    getAndSendResponseFromRequest(request);
                }
            } catch (IOException e) {
                thread.interrupt();
                onThreadException.accept(e, this);
            }
        };
    }

    public String getSessionFromClient() throws IOException {
        while (!thread.isInterrupted()) {
            Request request = handler.readRequestBlocking();

            if (request.getCommandData().getClass() != RegisterCommandData.class && request.getCommandData().getClass() != LoginCommandData.class) {
                handler.sendResponseBlocking(new ServerResponse(400, "Bad request! Need to send register or login command."));
                continue;
            }

            try {
                return requestExecuteFunction.apply(request);
            } catch (CommandException e) {
                handler.sendResponseBlocking(new ServerResponse(400, e.getMessage()));
            }
        }

        throw new IOException("Thread is interrupted.");
    }

    public void getAndSendResponseFromRequest(Request request) throws IOException {
        try {
            String result = requestExecuteFunction.apply(request);
            handler.sendResponseBlocking(new ServerResponse(200, result));
        } catch (CommandException e) {
            handler.sendResponseBlocking(new ServerResponse(500, e.getMessage()));
        }
    }
}
