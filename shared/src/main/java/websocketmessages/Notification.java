package websocketmessages;

public class Notification extends ServerMessage {
    String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String message() {
        return this.message;
    }
}
