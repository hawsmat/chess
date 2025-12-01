package websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsCloseHandler, WsMessageHandler {

    @Override
    public void handleClose(WsCloseContext ctx) {
        ;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        ;
    }

//    public void
}
