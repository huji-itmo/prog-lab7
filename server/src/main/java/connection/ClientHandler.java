package connection;

import commands.auth.LoginCommandData;
import commands.auth.RegisterCommandData;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ClientHandler {

    private final Thread thread;
    private final ConnectionHandler handler;
    private final Server server;
    @Getter
    private String session;
    @Getter
    private Long clientId;
    @Setter
    Function<Request, CommandExecutionResult> requestExecuteFunction;
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
                clientId = getClientIdFromLogin();

                setSession(server.createNewSessionWithClientId(clientId));

                handler.sendResponseBlocking(CommandExecutionResult.success(getSession())));

                while (!thread.isInterrupted()) {
                    Request request = handler.readRequestBlocking();

                    if (!request.getSession().equals(getSession())) {
                        handler.sendResponseBlocking(CommandExecutionResult.badRequest("Wrong session parameter. Sussy baka impostor"));
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

    public Long getClientIdFromLogin() throws IOException {
        while (!thread.isInterrupted()) {
            Request request = handler.readRequestBlocking();

            if (request.getCommandData().getClass() != RegisterCommandData.class && request.getCommandData().getClass() != LoginCommandData.class) {
                handler.sendResponseBlocking(CommandExecutionResult.badRequest("Bad request! Need to send register or login command."));
                continue;
            }

            CommandExecutionResult result = requestExecuteFunction.apply(request);

            if (result.getCode() != 200) {
                handler.sendResponseBlocking(result);

                return getClientIdFromLogin();
            }

            return result.getLong();
        }

        throw new IOException("Thread is interrupted.");
    }

    public void getAndSendResponseFromRequest(Request request) throws IOException {
        handler.sendResponseBlocking(requestExecuteFunction.apply(request));
    }
}
