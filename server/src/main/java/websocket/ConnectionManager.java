package websocket;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Set<Connection>> gameConnections = new ConcurrentHashMap<>();

    public void add(String name, Session session, int gameID){
        var connection = new Connection(name, session);
        connections.put(name, connection);

        gameConnections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(connection);
        System.out.println("GAME CONNECTIONS: " + gameConnections);
    }

//    public void addGameID(Stringint gameID, Session session){
//        var
//    }

    public void remove(String visitorName, int gameID) {
        connections.remove(visitorName);

        var sessionsInGame = gameConnections.get(gameID);

        if (sessionsInGame != null){
            sessionsInGame.removeIf(connection -> connection.getName().equals(visitorName));
        }
    }

    public void broadcast(String nameToExclude, ServerMessage message, int gameID) throws IOException {
        var deadConnections = new ArrayList<Connection>();
        var sessionsInGame = gameConnections.get(gameID);

        for (var c : gameConnections.get(gameID)) {
            if (c.session.isOpen()) {
                if (!c.name.equals(nameToExclude) && findUserName(c.name, sessionsInGame)) {
                    c.send(message.toString());
                }
            }
            else {
                deadConnections.add(c);
            }
        }

        cleanDeadConnections(deadConnections);
    }

    private boolean findUserName(String username, Set<Connection> usersInGame) {
        for (Connection connection : usersInGame){
            if (connection.getName().equals(username)){
                return true;
            }
        }
        return false;
    }

    private void cleanDeadConnections(ArrayList<Connection> deadConnections) {
        //clean up connections left open
        for (var c: deadConnections){
            connections.remove(c.name);
        }
    }

    public void broadcastAll(ServerMessage message, int gameID) throws IOException {
        var deadConnections = new ArrayList<Connection>();
        var sessionsInGame = gameConnections.get(gameID);

        for (var c : gameConnections.get(gameID)){
            if (c.session.isOpen() && findUserName(c.name, sessionsInGame)){
                System.out.println("BROADCASTING TO: " + c.name);
                c.send(message.toString());
            } else{
                deadConnections.add(c);
            }
        }

        cleanDeadConnections(deadConnections);
    }
}
