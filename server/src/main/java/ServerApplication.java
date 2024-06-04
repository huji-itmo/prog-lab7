import commands.CommandImplMap;
import connection.Server;
import dataStructs.StudyGroup;
import dataStructs.communication.Request;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import database.StudyGroupDatabase;
import org.hibernate.*;


public class ServerApplication {
    public static CommandImplMap implMap = new CommandImplMap();
    private static SessionFactory factory = null;
    private static final Collection<StudyGroup> collection = Collections.synchronizedCollection(new ArrayDeque<>());

    public static void run(String[] args) {

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


        Server server = new Server(5252);
        server.setExecuteRequest(ServerApplication::messageHandler);

        Function<String, Long> sessionToLongFunction = (session) -> server.getSessionToClientIdMap().get(session);

        StudyGroupDatabase database = new StudyGroupDatabase(factory, collection, sessionToLongFunction);

        implMap.addDatabaseCommands(database);


        server.startClientAcceptingLoop();
    }

    public static String messageHandler(Request request) {
        return implMap.map.get(request.getCommandData()).execute(request.getParams(), request.getSession());
    }
}
