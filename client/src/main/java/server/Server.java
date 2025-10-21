package server;

import Service.UserService;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin server;
    private UserService userService;
    private UserDataAccess dataAccess;


    public Server() {
        dataAccess = new UserDataAccess();
        userService = new UserService(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("/db", ctx -> {dataAccess.clear(); ctx.status(200); ctx.result("{}");});
        server.post("user", ctx -> register(ctx));


        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) throws DataAccessException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), UserData.class);
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
