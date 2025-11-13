import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import server.ServerFacade;

public class Client {
    private boolean loggedIn = false;
    private ServerFacade serverFacade;

    public Client(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    public void run() {
        commands();
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            getStatus();
            System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + ">>> ");
            String line = scanner.nextLine();
            result = evaluateInput(line);
            if (result.equals("help")) {
                commands();
            } else if (result.equals("login")) {
                commands();
            } else if (result.equals("logout")) {
                commands();
            } else {
                System.out.println(result);
            }
        }
        System.out.println("you quit");
    }

    public String evaluateInput(String line) {
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String command;
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (tokens.length > 0) {
                command = tokens[0];
            } else {
                command = "help";
            }
            return switch (command) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                default -> "help";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String login(String[] params) throws Exception {
        if (params.length == 2) {
            loggedIn = true;
//            serverFacade.login(new LoginData(params[0], params[1]));
            return "login";
        }
        throw new Exception("Expected: <username> <password>");
    }

    public String register(String[] params) throws Exception {
        if (params.length == 3) {
//            serverFacade.register(new UserData(params[0], params[1], params[2]));
            return "register";
        }
        throw new Exception("Expected: <username> <email> <password>");
    }

    public String create(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("Not authorized");
        }
        if (params.length == 1) {
            return "create";
        }
        throw new Exception("Expected: <Game name>");
    }

    public String list(String[] params) throws Exception {
        if (loggedIn) {
            return "list";
        } else {
            throw new Exception("Not authorized");
        }
    }

    public String join(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("Not authorized");
        }
        if (params.length == 2) {
            if (params[1].equals("white") || params[1].equals("black")) {
                printBoard(new ChessGame(), params[1]);
                return "join";
            }
            else {
                throw new Exception("Expected: white | black");
            }
        }
        throw new Exception("Expected: <color> <Game ID>");
    }

    public String observe(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("Not authorized");
        }
        if (params.length == 1) {
            printBoard(new ChessGame(), "white");
            return "observe";
        }
        throw new Exception("Expected: <Game ID>");
    }

    public String logout(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("You are not logged in");
        } else {
            loggedIn = false;
            return "logout";
        }
    }

    public void commands() {
        if (!loggedIn) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "help" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list commands");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "login <username> <password>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - log in to chess server");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "register <username> <password> <email>" +
                    EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - register account for access to chess server");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "quit" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - exit");
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "help" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list commands");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "create <game name>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - create a chess game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "list" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list available chess games");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "join <game id> <black | white>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - ");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "observe <game id>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - watch a current chess game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "logout" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - log out of the chess server");
        }

    }

    public void getStatus() {
        if (loggedIn) System.out.print("[LOGGED IN] ");
        else System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED OUT] ");
    }

    public void printBoard(ChessGame game, String color) {
        int initial;
        int direction;
        int end;
        if (color.equals("white")) {
            initial = 1;
            direction = 1;
            end = 9;
        }
        else {
            initial = 8;
            direction = -1;
            end = 0;
        }
        List<String> cols = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        List<String> rows = List.of("8", "7", "6", "5", "4", "3", "2", "1");
        printCols(cols, initial, direction, end);
        System.out.println();
        for (int i = initial; i != end; i += direction) {
            System.out.print(rows.get(i-1) + " ");
            for (int j = initial; j != end; j += direction) {
                if (game.getBoard().getPiece(new ChessPosition(i, j)) == null) {
                    getBlankSpace(i, j);
                } else {
                    printPiece(i, j, game.getBoard().getPiece(new ChessPosition(i, j)));
                }
            }
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_BG_COLOR + " " +rows.get(i-1));
        }
        printCols(cols, initial, direction, end);
    }

    public void getBlankSpace(int i, int j) {
        if ((i + j) % 2 == 0) {
            System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + "   ");
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + "   ");
        }
    }

    public void printPiece(int i, int j, ChessPiece chessPiece) {
        String piece;
        String color;
        if (chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            color = EscapeSequences.SET_TEXT_COLOR_RED;
            piece = switch (chessPiece.getPieceType()) {
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case KING -> EscapeSequences.BLACK_KING;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case PAWN -> EscapeSequences.BLACK_PAWN;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
            };
        } else {
            color = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
            piece = switch (chessPiece.getPieceType()) {
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case KING -> EscapeSequences.WHITE_KING;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case PAWN -> EscapeSequences.WHITE_PAWN;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
            };
        }
        if ((i + j) % 2 == 0) {
            System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + color + piece);
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + color + piece);
        }
    }

    public void printCols(List<String> cols, int initial, int direction, int end) {
        System.out.print("  ");
        for (int i = initial; i != end; i+=direction) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + cols.get(i-1) + " ");
        }
    }
}
