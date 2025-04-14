package websocket;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String name, Session session){
        var connection = new Connection(name, session);
        connections.put(name, connection);
    }

    public void remove(String visitorName) { connections.remove(visitorName); }

    public void broadcast(String nameToExclude, ServerMessage message) throws IOException {
        var deadConnections = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.name.equals(nameToExclude)) {
                    c.send(message.toString());
                }
            }
            else {
                deadConnections.add(c);
            }
        }

        cleanDeadConnections(deadConnections);
    }

    private void cleanDeadConnections(ArrayList<Connection> deadConnections) {
        //clean up connections left open
        for (var c: deadConnections){
            connections.remove(c.name);
        }
    }

    public void broadcastAll(ServerMessage message) throws IOException {
        var deadConnections = new ArrayList<Connection>();
        for (var c : connections.values()){
            if (c.session.isOpen()){
                c.send(message.toString());
            } else{
                deadConnections.add(c);
            }
        }

        cleanDeadConnections(deadConnections);
    }
}
