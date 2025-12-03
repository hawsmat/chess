package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocketmessages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    public final Map<Session, List<Integer>> connections = new HashMap<Session, List<Integer>>();

    public void add(Session session, int gameID) {
        connections.put(session, new ArrayList<>(gameID));
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, Notification notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.keySet()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
