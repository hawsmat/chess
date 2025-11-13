

public class Main {
    public static void main(String[] args){
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[1];
        }
        try {
            new client(serverUrl).run();
        } catch (Throwable ex ){
            System.out.printf("unable to start server: %s", ex.getMessage());
        }

    }
}