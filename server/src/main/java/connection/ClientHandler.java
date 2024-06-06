package connection;

import Server.Server;
import commands.auth.LoginCommandData;
import commands.auth.RegisterCommandData;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import dataStructs.communication.SessionByteArray;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ClientHandler {

    private final Thread thread;
    @Getter
    private final ConnectionHandler handler;
    private final Server server;
    @Getter
    private SessionByteArray session;
    @Getter
    private String clientName;
    @Setter
    private Function<Request, CommandExecutionResult> requestExecuteFunction;
    @Setter
    private BiConsumer<Exception, ClientHandler> onThreadException;
    @Setter
    private BiConsumer<SessionByteArray, ClientHandler> onNewSession;

    public ClientHandler(ConnectionHandler handler, Server server) {
        this.handler = handler;
        this.server = server;
        thread = new Thread(getRunnable());
        thread.setDaemon(true);
        thread.start();
    }

    private void setSession(SessionByteArray val) {
        session= val;
        onNewSession.accept(val, this);
    }


    public Runnable getRunnable() {
        return () -> {
            try {
                clientName = getClientNameFromLogin().trim();

                setSession(server.createNewSession(this));

                handler.sendResponseBlocking(CommandExecutionResult.success(getSession()));

                while (!thread.isInterrupted()) {
                    Request request = handler.readRequestBlocking();

                    if (!request.getSessionByteArray().equals(getSession())) {
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

    public String getClientNameFromLogin() throws IOException {
        while (!thread.isInterrupted()) {
            Request request = handler.readRequestBlocking();

            if (request.getCommandData().getClass() != RegisterCommandData.class && request.getCommandData().getClass() != LoginCommandData.class) {
                handler.sendResponseBlocking(CommandExecutionResult.badRequest("Bad request! Need to send register or login command."));
                continue;
            }

            CommandExecutionResult result = requestExecuteFunction.apply(request);

            if (result.getCode() == 200) {
                return result.getText();
            }

            handler.sendResponseBlocking(result);

            return getClientNameFromLogin();
        }

        throw new IOException("Thread is interrupted.");
    }

    public void getAndSendResponseFromRequest(Request request) throws IOException {
        handler.sendResponseBlocking(requestExecuteFunction.apply(request));
    }

}
