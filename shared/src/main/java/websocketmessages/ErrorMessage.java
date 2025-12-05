package websocketmessages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String message() {
        return this.errorMessage;
    }
}
