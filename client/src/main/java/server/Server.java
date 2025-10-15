package server;

import Service.UserService;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.User;
import io.javalin.*;
import io.javalin.http.Context;
import java.util.Map;

public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;


    public Server() {
//        dataAccess = new DataAccess();
        userService = new UserService(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("delete", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));


        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(ctx.body(), User.class);

        var res = userService.register(req);

        ctx.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
