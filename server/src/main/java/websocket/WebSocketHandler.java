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
import model.GameData;
import model.LoginResult;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import commands.Connect;
import commands.Leave;
import commands.MakeMove;
import commands.UserGameCommand;
import commands.*;
import websocketmessages.ErrorMessage;
import websocketmessages.LoadGame;
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
                case CONNECT -> {
                        Connect connect = Serializer.fromJson(ctx.message(), Connect.class);
                        connect(session, username, connect);
                }
                case MAKE_MOVE -> {
                    MakeMove move = Serializer.fromJson(ctx.message(), MakeMove.class);
                    makeMove(session, username, move);
                }
                case LEAVE -> {
                    Leave leave = Serializer.fromJson(ctx.message(), Leave.class);
                    leaveGame(session, username, leave);
                }
                case RESIGN -> {
                    Resign resign = Serializer.fromJson(ctx.message(), Resign.class);
                    resign(session, username, resign);
                }
            }
        } catch (UnauthorizedException e) {
            sendMessage(session, new ErrorMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            sendMessage(session, new ErrorMessage("error: " + e.getMessage()));
        }
    }

    public void saveSession(int gameID, Session session) {
        connections.add(session, gameID);
    }

    public void connect(Session session, String username, Connect command) {
        connections.add(session, command.getGameID());
        try {
            LoginResult loginResult = dataAccess.getAuthData(command.getAuthToken());
            GameData gameData = dataAccess.getGame(command.getGameID());
            if (!dataAccess.isAuthorized(command.getAuthToken())) {
                sendMessage(session, new ErrorMessage("You are not authorized"));
                return;
            }
            if (gameData.blackUsername().equals(username)) {
                sendMessage(session, new LoadGame(dataAccess.getChessGame(command.getGameID())));
                String message = String.format("%s is observing the game as Black", username);
                Notification notification = new Notification(message);
                connections.broadcast(command.getGameID(), session, notification);
            }
            else if (gameData.whiteUsername().equals(username)){
                sendMessage(session, new LoadGame(dataAccess.getChessGame(command.getGameID())));
                String message = String.format("%s is observing the game as White", username);
                Notification notification = new Notification(message);
                connections.broadcast(command.getGameID(), session, notification);
            }
            else {
                sendMessage(session, new LoadGame(dataAccess.getChessGame(command.getGameID())));
                String message = String.format("%s has joined the game", username);
                Notification notification = new Notification(message);
                connections.broadcast(command.getGameID(), session, notification);
            }
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
            sendMessage(session, new ErrorMessage("You are observing"));
            return;
        }
        if (!dataAccess.isAuthorized(authToken)) {
            sendMessage(session, new ErrorMessage("You are not authorized"));
            return;
        }
        if (game.isGameOver()) {
            sendMessage(session, new ErrorMessage("The game is over"));
            return;
        }
        if (move.getStartPosition() == null) {
            sendMessage(session, new ErrorMessage("That move is not allowed"));
            return;
        }
        if (game.getTeamTurn() != color) {
            sendMessage(session, new ErrorMessage("It is not your turn"));
            return;
        }
        if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != color) {
            sendMessage(session, new ErrorMessage("That is not your piece"));
            return;
        }
        for (ChessMove moves : game.validMoves(move.getStartPosition())) {
            if (move.equals(moves)) {
                game.makeMove(move);
                dataAccess.updateGame(command.getGameID(), game);
                String message = String.format("%s made move %s %s", username, move.getStartPosition().toString(), move.getEndPosition().toString());
                sendMessage(session, new LoadGame(dataAccess.getChessGame(command.getGameID())));
                connections.broadcast(command.getGameID(), session, new Notification(message));
                connections.broadcast(command.getGameID(), session, new LoadGame(dataAccess.getChessGame(command.getGameID())));
                return;
            }
        }
        sendMessage(session, new ErrorMessage("That move is not valid, or you did not include a promotion"));
    }

    public void leaveGame(Session session, String username, Leave command) throws Exception {
        String authToken = command.getAuthToken();
        if (!dataAccess.isAuthorized(authToken)) {
            sendMessage(session, new ErrorMessage("You are not authorized"));
            return;
        }
        if (dataAccess.getWhiteUsername(command.getGameID()) != null && dataAccess.getWhiteUsername(command.getGameID()).equals(username)) {
            dataAccess.updateUsernames(command.getGameID(), ChessGame.TeamColor.WHITE, null);
            connections.remove(command.getGameID(), session);
            String message = String.format("%s left!", username);
            connections.broadcast(command.getGameID(), session, new Notification(message));
            return;
        }
        else if (dataAccess.getBlackUsername(command.getGameID()) != null && dataAccess.getBlackUsername(command.getGameID()).equals(username)) {
            dataAccess.updateUsernames(command.getGameID(), ChessGame.TeamColor.BLACK, null);
            connections.remove(command.getGameID(), session);
            String message = String.format("%s left!", username);
            connections.broadcast(command.getGameID(), session, new Notification(message));
            return;
        }
        connections.remove(command.getGameID(), session);
        String message = String.format("%s left!", username);
        connections.broadcast(command.getGameID(), session, new Notification(message));
    }

    public void resign(Session session, String username, Resign command) throws Exception {
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
            sendMessage(session, new ErrorMessage("You are observing"));
            return;
        }
        if (!dataAccess.isAuthorized(authToken)) {
            sendMessage(session, new ErrorMessage("You are not authorized"));
            return;
        }
        if (game.isGameOver()) {
            sendMessage(session, new ErrorMessage("The game is over"));
            return;
        }
        if (color == ChessGame.TeamColor.WHITE) {
            sendMessage(session, new Notification("You Resigned! Black Won"));
            game.endGame();
            dataAccess.updateGame(command.getGameID(), game);
            connections.remove(command.getGameID(), session);
            String message = String.format("%s resigned! Black won!", username);
            connections.broadcast(command.getGameID(), session, new Notification(message));
        }
        else {
            sendMessage(session, new Notification("You Resigned! White Won"));
            game.endGame();
            dataAccess.updateGame(command.getGameID(), game);
            connections.remove(command.getGameID(), session);
            String message = String.format("%s resigned! White won!", username);
            connections.broadcast(command.getGameID(), session, new Notification(message));
        }
    }

    public void sendMessage(Session session, ServerMessage serverMessage) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(Serializer.toJson(serverMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
