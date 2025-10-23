package server;

import Service.AdminService;
import Service.GameService;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;

import javax.xml.crypto.Data;
import java.util.Map;

public class Server {

    private final Javalin server;
    private UserService userService;
    private GameService gameService;
    private AdminService adminService;
    private MemoryDataAccess memoryDataAccess;


    public Server() {
        memoryDataAccess = new MemoryDataAccess();
        userService = new UserService(memoryDataAccess);
        gameService = new GameService(memoryDataAccess);
        adminService = new AdminService(memoryDataAccess);
        Service.AdminService adminService = new AdminService(memoryDataAccess);
        Service.GameService gameService = new GameService(memoryDataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("/db", this::clear);
        server.post("user", this::register);
        server.post("/session", this::login);
        server.delete("/session", this::logout);
        server.get("/game", this::listGames);
        server.post("/game", this::createGame);
        server.put("/game", this::joinGame);

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), UserData.class);
        try {
            var res = userService.register(req);
            ctx.result(serializer.toJson(res));
        } catch (DataAccessException e) {
            ctx.status(403);
        }
    }

    private void login(Context ctx) {
    }

    private void logout(Context ctx) {
        String authToken = ctx.header("authorization");
        if (authToken == null || authToken.isEmpty()) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        }
        try {
            userService.logout(authToken);
            ctx.status(200).json(Map.of());
        } catch (DataAccessException e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        }
    }

    private void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        try {
            ctx.json(gameService.listGames(authToken));
        } catch (DataAccessException e) {
            ctx.status(401);
        }

    }

    private void createGame(Context ctx) {
        String authToken = ctx.header("authorization");
        try {
            ctx.json(gameService.listGames(authToken).toString());
        } catch (DataAccessException e) {
            ctx.status(401);
        }
    }

    private void joinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        try {
            ctx.json(gameService.listGames(authToken).toString());
        } catch (DataAccessException e) {
            ctx.status(401);
        }
    }

    private void clear(Context ctx) {
        adminService.clear();
        ctx.status(200);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
