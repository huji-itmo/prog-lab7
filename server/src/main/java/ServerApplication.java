import commands.CommandImplMap;
import commands.exceptions.CommandException;
import dataStructs.StudyGroup;
import dataStructs.communication.Request;
import dataStructs.communication.ServerResponse;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

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

    public static void messageHandler(Request request, ConnectionHandler handler) {
        try {
//            database.setCurrentClient(handler.getClientId());

            String out = implMap.map.get(request.getCommandData()).execute(request.getParams());
            handler.send( new ServerResponse(200, out));
        } catch (CommandException e) {
            handler.send(new ServerResponse(500, e.getMessage()));
        }
    }
}
