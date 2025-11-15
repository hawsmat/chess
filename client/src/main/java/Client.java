import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import serverFacade.ServerFacade;
import model.*;

public class Client {
    private boolean loggedIn = false;
    private String authToken;
    private ServerFacade serverFacade;
    private String serverUrl;

    public Client(String serverUrl) {
        this.serverUrl=serverUrl;
    }

    private ServerFacade facade() {
        if (serverFacade == null) {
            serverFacade = new ServerFacade(serverUrl);
        }
        return serverFacade;
    }

    public void run() {
        commands();
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            getStatus();
            System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + ">>> ");
            String line = scanner.nextLine();
            try {
                result = evaluateInput(line);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (result.equals("help")) {
                commands();
            } else if (result.equals("login")) {
                commands();
            } else if (result.equals("logout")) {
                commands();
            } else if (result.equals("register")) {
                commands();
            } else if (result.isEmpty()){
                System.out.println();
            } else {
                System.out.println(result);
                commands();
            }
        }
        System.out.println("you quit");
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
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> "help";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String login(String[] params) throws Exception {
        if (loggedIn) {
            throw new Exception("Already logged in");
        }
        if (params.length == 2) {
            try {
                LoginResult loginResult = facade().login(new LoginData(params[0], params[1]));
                authToken = loginResult.authToken();
                loggedIn = true;
                System.out.println("Logged in as " + params[0]);
                return "login";
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        throw new Exception("Expected: <username> <password>");
    }

    public String register(String[] params) throws Exception {
        if (loggedIn) {
            throw new Exception("Can't register while logged in.");
        }
        if (params.length == 3) {
            try {
                LoginResult loginResult = facade().register(new UserData(params[0], params[1], params[2]));
                authToken = loginResult.authToken();
                System.out.println("registered " + params[0]);
                loggedIn = true;
                return "register";
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        throw new Exception("Expected: <username> <email> <password>");
    }

    public String create(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("You are not logged in");
        }
        if (params.length == 0) {
            throw new Exception("Expected: <Game name>");
        }
        String gameName = "";
        for (int i = 0; i < params.length; i++) {
            if (i == params.length - 1){
                gameName += params[i];
            }
            else {
                gameName += params[i] + " ";
            }
        }
        try {
            facade().createGame(authToken, new CreateGameData(gameName));
            System.out.println("Created new game: " + gameName);
            return "";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "failed";
    }

    public String list() throws Exception {
        if (loggedIn) {
            try {
                 GameLists gameLists = facade().listGames(authToken);
                 for (int i = 0; i < gameLists.games().size(); i++) {
                     ListGameResult game = gameLists.games().get(i);
                     String blackUser = (game.blackUsername() == null) ? "<Empty>" : game.blackUsername();
                     String whiteUser = (game.whiteUsername() == null) ? "<Empty>" : game.whiteUsername();
                     int num = i + 1;
                     String str =  EscapeSequences.SET_TEXT_COLOR_RED + num + ". Game Name: " +
                         EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + game.gameName() +
                         "," + EscapeSequences.SET_TEXT_COLOR_RED + " White Player: " + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + whiteUser +
                        "," + EscapeSequences.SET_TEXT_COLOR_RED + " Black Player: " + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + blackUser;
                     System.out.println(str);
                 }
                 return "";
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new Exception("You are not logged in ");
        }
        return "failed";
    }

    public String join(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("You are not logged in");
        }
        if (params.length != 2) {
            throw new Exception("Expected: <Game ID> <color>");
        }
        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (Exception e) {
            throw new Exception("Game ID needs to be a number");
        }

        if (gameID <= 0) {
            throw new Exception("That Game ID does not exist");
        }
        if (params[1].equals("white") || params[1].equals("black")) {
            ChessGame.TeamColor playerColor;
            if (params[1].equals("white")) {
                playerColor = ChessGame.TeamColor.WHITE;
            }
            else {
                playerColor = ChessGame.TeamColor.BLACK;
            }
            try {
                facade().join(authToken, new JoinGameData(playerColor, gameID));
                System.out.println("joined a game as " + params[1] + " player.");
                printBoard(new ChessGame(), params[1]);
                return "";
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            throw new Exception("Expected: white | black");
        }
        return "failed";
    }

    public String observe(String[] params) throws Exception {
        if (!loggedIn) {
            throw new Exception("You are not logged in");
        }
        if (params.length == 1) {
            int gameID;
            try {
                gameID = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new Exception("Game ID needs to be a number");
            }
            if (gameID <= 0) {
                throw new Exception("That game ID does not exist");
            }
            try {
                printBoard(new ChessGame(), "white");
                return "";
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        throw new Exception("Expected: <Game ID>");
    }

    public String logout() throws Exception {
        if (!loggedIn) {
            throw new Exception("You are not logged in");
        } else {
            try {
                facade().logout(authToken);
                loggedIn = false;
                System.out.println("Logged out");
                return "logout";
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return "failed";
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
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "join <game ID> <black | white>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - ");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "observe <game ID>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - watch a current chess game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "logout" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - log out of the chess server");
        }

    }

    public void getStatus() {
        if (loggedIn) System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED IN] ");
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
        System.out.println();
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
