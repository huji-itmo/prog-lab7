import commands.CommandImplMap;
import commands.exceptions.CommandException;
import connection.ConnectionHandler;
import dataStructs.StudyGroup;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

import database.StudyGroupDatabaseInstance;
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
            StudyGroupDatabaseInstance database = new StudyGroupDatabaseInstance(factory, collection);

            implMap.addDatabaseCommands(database);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        Server server = new Server(5252, ServerApplication::messageHandler);

        server.startClientAcceptingLoop();
    }

    public static String messageHandler(Request request) {
        return implMap.map.get(request.getCommandData()).execute(request.getParams());
    }
}
