package server;

import Service.AdminService;
import Service.GameService;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;

import java.awt.desktop.SystemEventListener;
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

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("/db", this::clear);
        server.post("/user", this::register);
        server.post("/session", this::login);
        server.delete("/session", this::logout);
        server.get("/game", this::listGames);
        server.post("/game", this::createGame);
        server.put("/game", this::joinGame);
    }

        private void register (Context ctx) {
            var serializer = new Gson();
            UserData userData = serializer.fromJson(ctx.body(), UserData.class);
            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                ctx.status(400).json("{\"message\": \"Error: bad request\"}");
                return;
            }
            try {
                AuthData authData = userService.register(userData);
                ctx.json(serializer.toJson(authData));
                ctx.status(200);
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            } catch (AlreadyTakenException e) {
                ctx.status(403).json("{\"message\": \"Error: bad request\"}");
            }
        }


        private void login (Context ctx){
            var serializer = new Gson();
            LoginData loginData;
            try {
                loginData = serializer.fromJson(ctx.body(), LoginData.class);
            } catch (Exception e) {
                ctx.status(400).json("{\"message\": \"Error: bad request\"}");
                return;
            }
            if (loginData.username() == null || loginData.username().isEmpty() ||
                    loginData.password() == null || loginData.password().isEmpty()) {
                ctx.status(400).json("{\"message\": \"Error: bad request\"}");
                return;
            }

            try {
                AuthData authData = userService.login(loginData);
                String str = String.format("{\"username\":\"%s\", \"authToken\":\"%s\"}",
                        authData.username(), authData.authToken());
                ctx.status(200).result(str);
            } catch (UnauthorizedException e) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            }
        }

        private void logout (Context ctx){
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            try {
                userService.logout(authToken);
                ctx.status(200);
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            } catch (UnauthorizedException e) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
            }
        }

        private void listGames (Context ctx){
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            try {
                ctx.result(new Gson().toJson(gameService.listGames(authToken)));
                ctx.status(200);
            } catch (UnauthorizedException e) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            }
        }

        private void createGame (Context ctx){
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            String gameName;
            try {
                var map = new Gson().fromJson(ctx.body(), Map.class);
                gameName = (String) map.get("gameName");
            } catch (Exception e) {
                String str = String.format("{\"message\": \"Error: bad request\"}", e);
                ctx.status(400).json(str);
                return;
            }
            if (gameName == null || gameName.isEmpty()) {
                String str = String.format("{\"message\": \"Error: bad request\"}");
                ctx.status(400).json(str);
                return;
            }
            CreateGameData createGameData = new CreateGameData(authToken, gameName);
            try {
                int gameID = gameService.createGame(createGameData);
                String str = String.format("{\"gameID\": %d}", gameID);
                ctx.status(200).json(str);
            } catch (UnauthorizedException e) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            }
        }

        private void joinGame (Context ctx){
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            JoinGameData joinGameData;
            try {
                joinGameData = new Gson().fromJson(ctx.body(), JoinGameData.class);
            } catch (Exception e) {
                ctx.status(400).json("{\"message\": \"Error: bad request\"}");
                return;
            }
            try {
                gameService.joinGame(authToken, joinGameData);
                ctx.status(200);
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            } catch (UnauthorizedException e) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
            }
        }

        private void clear (Context ctx){
            try {
                adminService.clear();
                ctx.status(200);
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            }
        }

        public int run ( int desiredPort){
            server.start(desiredPort);
            return server.port();
        }

        public void stop () {
            server.stop();
        }
    }
