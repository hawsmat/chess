package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocketmessages.LoadGame;
import websocketmessages.Notification;
import websocketmessages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class ConnectionManager {
    public final Map<Integer, Set<Session>> connections = new HashMap<Integer, Set<Session>>();

    public void add(Session session, int gameID) {
        if (connections.get(gameID) == null) {
            connections.put(gameID, new HashSet<Session>());
        }
        else {
            connections.get(gameID).add(session);
        }
    }

    public void remove(int gameID, Session session) {
        if (connections.get(gameID) != null) {
            connections.get(gameID).remove(session);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        if (connections.get(gameID) != null) {
            Set<Session> sessions = connections.get(gameID);
            for (Session c : sessions) {
                if (c.isOpen()) {
                    if (!c.equals(excludeSession)) {
                        c.getRemote().sendString(new Gson().toJson(serverMessage));
                    }
                }
            }
        }
    }
}
