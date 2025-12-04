package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
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
            sendMessage(session, gameID, new Error("Error: " + e.getMessage()));
        } catch (Exception e) {
            sendMessage(session, gameID, new Error("error: " + e.getMessage()));
        }
    }

    public void saveSession(int gameID, Session session) {
        connections.add(session, gameID);
    }

    public void connect(Session session, String username, Connect command){
        connections.add(session, command.getGameID());
        String message = String.format("%s has joined the game", username);
        Notification notification = new Notification(message);
        try {
            connections.broadcast(session, notification);
        } catch (IOException e) {
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
            throw new Exception("You are observing");
        }
        if (!dataAccess.isAuthorized(authToken)) {
            throw new Exception("You are not authorized");
        }
        if (move.getStartPosition() == null) {
            throw new Exception("That game is Illegal");
        }
        if (game.getTeamTurn() != color) {
            throw new Exception("It is not your turn");
        }
        if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != color) {
            throw new Exception("That is not your piece");
        }
        for (ChessMove moves : game.validMoves(move.getStartPosition())) {
            if (move.equals(moves)) {
                game.makeMove(move);
                dataAccess.updateGame(command.getGameID(), game);
                String message = String.format("%s made move %s %s", username, move.getStartPosition().toString(), move.getEndPosition().toString());
                sendMessage(session, command.getGameID(), new Notification(message));
                return;
            }
        }
        throw new Exception("That move is not valid, or you did not include a promotion");
    }

    public void leaveGame(Session session, String username, Leave command) throws Exception {
        ChessGame game = dataAccess.getChessGame(command.getGameID());
        String authToken = command.getAuthToken();
        connections.remove(session);
    }
    public void resign(Session session, String username, Resign command) throws Exception {}
    public void sendMessage(Session session, int gameID, ServerMessage serverMessage) throws Exception {}

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
