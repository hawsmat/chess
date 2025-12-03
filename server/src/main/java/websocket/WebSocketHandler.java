package server.websocket;

import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.Action;
import webSocketMessages.Notification;
import websocket.ConnectionManager;
import websocket.commands.*;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        int gameID = -1;
        Session session = ctx.session;
        try {
            UserGameCommand command = Serializer.fromJson(
                    ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameID, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (Connect) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMove) command);
                case LEAVE -> leaveGame(session, username, (Leave) command);
                case RESIGN -> resign(session, username, (Resign) command);
            }
        } catch (UnauthorizedException e) {
            sendMessage(session, gameID, new ErrorMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, gameID, new ErrorMessage("error: " + e.getMessage()));
        }

    }

    public void connect(Session session, String username, Connect command){}
    public void makeMove(Session session, String username, MakeMove command){}
    public void leaveGame(Session session, String username, Leave command){}
    public void resign(Session session, String username, Resign command){}

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
