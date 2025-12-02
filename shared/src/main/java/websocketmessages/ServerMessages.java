package websocketmessages;

public class ServerMessages {
    public enum ServerMessageType{
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }
    ServerMessageType serverMessageType;
}
