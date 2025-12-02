package client.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Server;
import webSocketMessages.Action;
import webSocketMessages.Notification;

import jakarta.websocket.*;
import websocket.NotificationHandler;
import websocket.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

    //need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception("There was an exception");
        }
    }

    private void handleMessage(String message) {
        try {
            ServerMessage message = Serializer.fromJson(messageString, ServerMessage.class);
            listener.notify(message);
        } catch (Exception e) {
            listener.notify(new ErrorMessage(ex.getMessage()));
        }
    }

        //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen (Session session, EndpointConfig endpointConfig){
    }
}


