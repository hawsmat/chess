import chess.ChessGame;
import serverfacade.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;

public class Connect {
    public boolean inGame = true;
    public ServerFacade serverFacade;
    public ChessGame game;
    ChessGame.TeamColor color;

    public Connect(ServerFacade serverFacade, ChessGame game, ChessGame.TeamColor color) {
        this.serverFacade = serverFacade;
        this.game = game;
        this.color = color;
    }

    public void run() {
        commands();
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("leave")) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + ">>> ");
            String line = scanner.nextLine();
            try {
                result = evaluateInput(line);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (result.equals("help")) {
                commands();
            } else if (result.isEmpty()){
                System.out.println();
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
                case "leave" -> "leave";
                case "redraw" -> redraw(params);
                case "resign" -> resign(params);
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
            return "";
        } else {
            throw new Exception("Expected: {start position} {end position}");
        }
    }

    public String redraw(String[] params) throws Exception {
        return "";
    }

    public String resign(String[] params) throws Exception {
        return "";
    }

    public String highlight(String[] params) throws Exception {
        if (params.length == 1) {
            return "";
        } else {
            throw new Exception("Expected: {position}");
        }
    }

}
