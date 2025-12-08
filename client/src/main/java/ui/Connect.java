package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import serverfacade.ServerFacade;
import commands.*;
import websocket.WebSocketFacade;
import websocketmessages.ErrorMessage;
import websocketmessages.LoadGame;
import websocketmessages.Notification;
import websocketmessages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

public class Connect implements WebSocketFacade.MessageListener {
    private ChessGame game;
    ChessGame.TeamColor color;
    String authToken;
    int gameID;
    String url;
    WebSocketFacade webSocketFacade;

    public Connect(String url, ChessGame game, ChessGame.TeamColor color, String authToken, int gameID){
        this.url = url;
        this.game = game;
        this.color = color;
        this.authToken = authToken;
        this.gameID = gameID;
        try {
            this.webSocketFacade = new WebSocketFacade(url, this);
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            String command = new Gson().toJson(connect);
            System.out.println("sending:" + command);
            webSocketFacade.sendCommand(command);
        } catch (Exception e) {
            throw new RuntimeException("Connection failed");
        }
    }

    public void run() {
        commands();
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (true) {
            System.out.print(EscapeSequences.RESET_TEXT_COLOR + "[IN GAME] "  + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + ">>> ");
            String line = scanner.nextLine();
            try {
                result = evaluateInput(line);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (result.equals("help")) {
                commands();
            } else if (result.isEmpty()) {
                System.out.println();
            } else if (result.equals("leave")){
                break;
            } else {
                System.out.println(result);
                commands();
            }
        }
        System.out.println("you left");
    }

    public String evaluateInput(String line) {
        try {
            String[] tokens = line.split(" ");
            String command;
            String[] params;
            if (tokens.length > 0) {
                command = tokens[0];
                params = Arrays.copyOfRange(tokens, 1, tokens.length);
            } else {
                command = "help";
                params = new String[0];
            }
            return switch (command) {
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "move" -> move(params);
                default -> "help";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void commands() {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "help" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list commands");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "redraw" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - create a chess game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "leave" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - leave game without resignation");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "move {start position} {end position}" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - move a piece");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "resign" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - End the game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "highlight {position}" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - show possible moves for given piece");
    }

    public String move(String[] params) throws Exception {
        if (params.length == 2) {
            ChessPosition startPosition = convertToChessPosition(params[0]);
            ChessPosition endPosition = convertToChessPosition(params[1]);
            ChessMove chessMove = new ChessMove(startPosition, endPosition, null);
            MakeMove makeMove = new MakeMove(authToken, gameID, chessMove);
            webSocketFacade.sendCommand(new Gson().toJson(makeMove));
            return "";
        }
        else if (params.length == 3){
            ChessPosition startPosition = convertToChessPosition(params[0]);
            ChessPosition endPosition = convertToChessPosition(params[1]);
            ChessPiece.PieceType type = convertToChessPiece(params[2]);
            ChessMove chessMove = new ChessMove(startPosition, endPosition, type);
            MakeMove makeMove = new MakeMove(authToken, gameID, chessMove);
            webSocketFacade.sendCommand(new Gson().toJson(makeMove));
            return "";
        }
        else {
            throw new Exception("Expected: {start position} {end position}");
        }
    }

    public String redraw() {
        new PrintBoard(game, color, null);
        return "";
    }

    public String resign() {
        System.out.println("Are you sure? Enter yes or anything else");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] tokens = line.split(" ");
        String input = tokens[0];
        if (input.equals("yes")) {
            String command = new Gson().toJson(new Resign(authToken, gameID));
            webSocketFacade.sendCommand(command);
        }
        return "";
    }

    public String highlight(String[] params) throws Exception {
        if (params.length == 1) {
            ChessPosition chessPosition = convertToChessPosition(params[0]);
            if (game.getBoard().getPiece(chessPosition) == null) {
                throw new Exception("That is not a piece");
            }
            else if (game.getBoard().getPiece(chessPosition).getTeamColor() != color) {
                    throw new Exception("That is not your piece");
            }
            new PrintBoard(game, color, chessPosition);
            return "";
        } else {
            throw new Exception("Expected: {position}");
        }
    }

    public ChessPosition convertToChessPosition(String string) throws Exception {
        int col = switch (string.charAt(0)) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new Exception("column does not exist");
        };

        int row;
        try {
            row = Integer.parseInt(String.valueOf(string.charAt(1)));
        } catch (Exception e) {
            throw new Exception("row needs to be a number");
        }

        if (row > 8 || row < 1) {
            throw new Exception("row does not exist");
        }

        return new ChessPosition(row, col);
    }

    public ChessPiece.PieceType convertToChessPiece(String string) throws Exception {
        return switch (string) {
            case "king" -> ChessPiece.PieceType.KING;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "pawn" -> ChessPiece.PieceType.PAWN;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            default -> throw new Exception("Expected: king | queen | rook | pawn | knight | bishop");
        };
    }

    public String leave() {
        String leave = new Gson().toJson(new Leave(authToken, gameID));
        webSocketFacade.sendCommand(leave);
        return "leave";
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage error = (ErrorMessage) message;
            System.out.println("Error: " + error.message());
        }
        else if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            Notification notification = (Notification) message;
            System.out.println(notification.message());
        }
        else if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGame loadGame = (LoadGame) message;
            this.game = loadGame.getGame();
            redraw();
        }
        else {
            System.out.println("Could not parse the message");
        }
    }
}
