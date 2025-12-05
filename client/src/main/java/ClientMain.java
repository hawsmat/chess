import ui.Client;

public class ClientMain {
    public static void main(String[] args){
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            new Client(serverUrl).run();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}