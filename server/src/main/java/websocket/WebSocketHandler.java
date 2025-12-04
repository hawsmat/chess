package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import usergamecommands.Connect;
import usergamecommands.Leave;
import usergamecommands.MakeMove;
import usergamecommands.UserGameCommand;
import usergamecommands.*;
import websocketmessages.Error;
import websocketmessages.Notification;
import websocketmessages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    DataAccess dataAccess;
    GameService gameService;
    private final ConnectionManager connections = new ConnectionManager();
    Gson Serializer = new Gson();

    public WebSocketHandler(DataAccess dataAccess, GameService gameService) {
        this.dataAccess = dataAccess;
        this.gameService = gameService;
    }

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
            String username = dataAccess.getAuthData(command.getAuthToken()).username();
            saveSession(gameID, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (Connect) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMove) command);
                case LEAVE -> leaveGame(session, username, (Leave) command);
                case RESIGN -> resign(session, username, (Resign) command);
            }
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: " + e.getMessage()));
        } catch (Exception e) {
            sendError(session, new Error("error: " + e.getMessage()));
        }
    }

    public void saveSession(int gameID, Session session) {
        connections.add(session, gameID);
    }

    public void connect(Session session, String username, Connect command){
        connections.add(session, command.getGameID());
        try {
            if (!dataAccess.getWhiteUsername(command.getGameID()).equals(username) && !dataAccess.getBlackUsername(command.getGameID()).equals(username)) {
                String message = String.format("%s is observing the game", username);
                Notification notification = new Notification(message);
                connections.broadcast(command.getGameID(), session, notification);
                return;
            }
            String message = String.format("%s has joined the game", username);
            Notification notification = new Notification(message);
            connections.broadcast(command.getGameID(), session, notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
            }
    }

    public void makeMove(Session session, String username, MakeMove command) throws Exception {
        ChessMove move = command.move();
        ChessGame game = dataAccess.getChessGame(command.getGameID());
        String authToken = command.getAuthToken();
        ChessGame.TeamColor color;
        if (dataAccess.getBlackUsername(command.getGameID()).equals(username)) {
            color = ChessGame.TeamColor.BLACK;
        }
        else if (dataAccess.getWhiteUsername(command.getGameID()).equals(username)) {
            color = ChessGame.TeamColor.WHITE;
        }
        else {
            sendError(session, new Error("You are observing"));
            return;
        }
        if (!dataAccess.isAuthorized(authToken)) {
            sendError(session, new Error("You are not authorized"));
            return;
        }
        if (move.getStartPosition() == null) {
            sendError(session, new Error("That game is not allowed"));
            return;
        }
        if (game.getTeamTurn() != color) {
            sendError(session, new Error("It is not your turn"));
            return;
        }
        if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != color) {
            sendError(session, new Error("That is not your piece"));
            return;
        }
        for (ChessMove moves : game.validMoves(move.getStartPosition())) {
            if (move.equals(moves)) {
                game.makeMove(move);
                dataAccess.updateGame(command.getGameID(), game);
                String message = String.format("%s made move %s %s", username, move.getStartPosition().toString(), move.getEndPosition().toString());
                connections.broadcast(command.getGameID(), session, new Notification(message));
                return;
            }
        }
        sendError(session, new Error("That move is not valid, or you did not include a promotion"));
    }

    public void leaveGame(Session session, String username, Leave command) throws Exception {
        String authToken = command.getAuthToken();
        if (dataAccess.isAuthorized(authToken)) {
            connections.remove(command.getGameID(), session);
        }
        else {
            sendError(session, new Error("You are not authorized"));
        }
    }

    public void resign(Session session, String username, Resign command) throws Exception {}

    public void sendError(Session session, Error error) throws Exception {
        if (session.isOpen()) {
            session.getRemote().sendString(error.message());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
