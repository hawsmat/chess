package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocketmessages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    Gson Serializer = new Gson();

    public interface MessageListener {
        void onMessage(ServerMessage message);
    }

    private MessageListener listener;

    public WebSocketFacade(String url, MessageListener listener) throws Exception {
        this.listener = listener;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception();
        }
    }

    private void handleMessage(String messageString) {
        try {
            ServerMessage message = Serializer.fromJson(messageString, ServerMessage.class);
            listener.onMessage(message);
        } catch (Exception e) {
            System.out.println("Could not parse message");
        }
    }
        //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }

    public void sendCommand(String command) {
        session.getAsyncRemote().sendText(command);
    }
}


