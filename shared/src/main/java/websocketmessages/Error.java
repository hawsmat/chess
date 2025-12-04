package websocketmessages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Error extends ServerMessage {
    String message;
    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String message() {
        return this.message;
    }


    @Override
    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }
}
