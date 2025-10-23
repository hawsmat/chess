package server;

import Service.AdminService;
import Service.GameService;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin server;
    private UserService userService;
    private MemoryDataAccess memoryDataAccess;


    public Server() {
        memoryDataAccess = new MemoryDataAccess();
        userService = new UserService(memoryDataAccess);
        Service.AdminService adminService = new AdminService(memoryDataAccess);
        Service.GameService gameService = new GameService(memoryDataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("/db", ctx -> {adminService.clear(); ctx.status(200); ctx.result("{}");});
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
