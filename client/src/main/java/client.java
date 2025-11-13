import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;
import server.ServerFacade;

public class client {
    private boolean loggedIn = false;
//    ServerFacade server = new ServerFacade();
    public client() {
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
            }
            else if (result.equals("login")) {
                commands();
            }
            else if (result.equals("logout")) {
                commands();
            }
            else {
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

    public String login(String[] params)throws Exception {
        if (params.length == 2) {
            loggedIn = true;
            return "login";
        }
        throw new Exception("Expected: <username> <password>");
    }

    public String register(String[] params) throws Exception {
        if (params.length == 3) {
            return "register";
        }
        throw new Exception("Expected: <username> <email> <password>");
    }

    public String create(String[] params) throws Exception{
        if (loggedIn && params.length == 1) {
            return "create";
        }
        throw new Exception("Expected: <Game name>");
    }

    public String list(String[] params) throws Exception {
        if (loggedIn) {
            return "list";
        }
        else {
            throw new Exception("You are not logged in!");
        }
    }

    public String join(String[] params) throws Exception{
        if (loggedIn && params.length == 2) {
            return "join";
        }
        throw new Exception("Expected: <Color> <Game ID>");
    }

    public String observe(String[] params) throws Exception {
        if (loggedIn && params.length == 1) {
            return "observe";
        }
        throw new Exception("Expected: <Game ID>");
    }

    public String logout(String[] params) throws Exception {
        loggedIn = false;
        return "logout";
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
        }
        else {
            getStatus();
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "help" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list commands");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "create <game name>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - create a chess game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "list" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list available chess games");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "join <color> <game id>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - ");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "observe <game id>" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - watch a current chess game");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "logout" + EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - log out of the chess server");
        }

    }
    public void getStatus() {
        if (loggedIn) System.out.println("[LOGGED IN] ");
        else System.out.print("[LOGGED OUT] ");
    }
}
