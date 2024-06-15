import Server.Server;
import commands.CommandImplMap;
import commands.databaseCommands.ConfirmCommandData;
import connection.ClientHandler;
import connection.ConnectionHandler;
import dataStructs.StudyGroup;
import dataStructs.communication.CommandExecutionResult;
import dataStructs.communication.Request;
import dataStructs.communication.SessionByteArray;
import dataStructs.communication.enums.ResponsePurpose;
import dataStructs.communication.enums.ResponseType;
import dataStructs.undo.TransactionLog;
import database.ConfirmDeleteInterface;
import database.StudyGroupDatabase;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;


public class ServerApplication {
    public CommandImplMap implMap = new CommandImplMap();
    private SessionFactory factory = null;
    private Server server = null;

    public void run(String[] args) {

        if (args == null || args.length == 0 || args[0].isBlank()) {
            System.err.println("Please add path to arguments of this program!");
            System.exit(-1);
        }

        try {
            factory = HibernateSessionFactory.setupFactory(args[0]);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        StudyGroupDatabase database = createDatabase(factory);

        server = createServer(5252, database);

        implMap.addDatabaseCommands(database);

        server.startClientAcceptingLoop();
    }

    public Function<Request, CommandExecutionResult> getMessageHandler() {
        return (request) -> implMap.map.get(request.getCommandData()).execute(request.getParams(), request.getSessionByteArray());
    }

    public Function<SessionByteArray, String> getSessionToUserNameFunction() {
        return (session) -> server.getSessionToClientHandlerMap().get(session).getClientName().strip();
    }

    public ConfirmDeleteInterface<StudyGroup> getConfirmDeleteFunction() {
        return (list, session) -> {
            CommandExecutionResult confirmMessage = CommandExecutionResult.confirm(list);

            ClientHandler clientHandler = server.getSessionToClientHandlerMap().get(session);
            try {
                clientHandler.getHandler().sendResponseBlocking(confirmMessage);

                Request request = clientHandler.getHandler().readRequestBlocking();

                if (request.getCommandData().getClass() == ConfirmCommandData.class) {
                    return true;
                }

            } catch (IOException e) {
                System.err.println("Error when confirming: " + e.getMessage());
            }

            return false;
        };
    }

    public Consumer<TransactionLog<StudyGroup>> getTransactionLogConsumer() {
        return (transactionLog) -> {
            CommandExecutionResult res = CommandExecutionResult
                    .builder()
                    .code(200)
                    .object(transactionLog)
                    .responsePurpose(ResponsePurpose.UPDATE)
                    .responseType(ResponseType.TRANSACTION_LOG).build();

            server.getSessionToClientHandlerMap().forEach((session, client) -> {
                try {
                    ConnectionHandler connectionHandler = client.getHandler();
                    connectionHandler.sendResponseBlocking(res);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }

    public StudyGroupDatabase createDatabase(SessionFactory factory) {
        StudyGroupDatabase database = new StudyGroupDatabase(factory, getSessionToUserNameFunction(), getTransactionLogConsumer());
        database.confirmDelete = getConfirmDeleteFunction();

        return database;
    }

    public Server createServer(int port, StudyGroupDatabase database) {
        Server server = new Server(port);
        server.setExecuteRequest(getMessageHandler());

        server.setOnNewSession((sessionString -> {
            database.getUndoLogStacksBySession().put(sessionString, new Stack<>());
            System.out.println("Current sessions:");
            database.getUndoLogStacksBySession().keySet().forEach(System.out::println);
        }));

        server.setOnSessionEnd((sessionString -> {
            database.getUndoLogStacksBySession().remove(sessionString);
            System.out.println("Current sessions:");
            database.getUndoLogStacksBySession().keySet().forEach(System.out::println);
        }));

        return server;
    }
}
