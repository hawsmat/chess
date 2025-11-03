import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class client {
        private boolean LoggedIn = false;
    public client() {
        System.out.println("hello world");
        commands();
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            result = evaluateInput(line);
            System.out.println(line);
        }
        System.out.println("you quit");
    }

    public String evaluateInput(String line) {
        String[] tokens = line.toLowerCase().split(" ");
        String command;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (tokens.length > 0) {
           command = tokens[0];
        }
        else {
            command = "help";
        }
        return switch (command) {
            case "login" -> "quit";
            case "register" -> register(params);
            case "create" -> create(params);
            case "list" -> list(params);
            case "join" -> join(params);
            case "observe" -> observe(params);
            default -> "help";
        }
    }

    public String register(String[] params) {}

    public String create(String[] params) {}

    public String list(String[] params) {}

    public String join(String[] params) {}

    public String observe(String[] params) {}

    public void commands() {
        if (!LoggedIn) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "help");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list commands");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "login <username> <password>");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - log in to chess server");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "register <username> <email> <password>");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - register account for access to chess server");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "quit");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - exit");
        }
        else {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "help");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - list commands");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "create");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - register account for access to chess server");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "list");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - exit");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "join");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - register account for access to chess server");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "observe");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - exit");
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "logout");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " - log in to chess server");
        }
    }
}
