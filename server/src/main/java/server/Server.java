package server;

import dataaccess.*;
import service.AdminService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;
import websocket.WebSocketHandler;

import java.util.List;
import java.util.Map;

public class Server {
    private final Javalin server;
    private UserService userService;
    private GameService gameService;
    private AdminService adminService;
    private DataAccess dataAccess;

    public Server() {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        adminService = new AdminService(dataAccess);

        server = Javalin.create(config ->
            config.staticFiles.add("web"));
        WebSocketHandler webSocketHandler = new WebSocketHandler(dataAccess, gameService);
        server.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(ctx -> {
                System.out.println("🔴 RAW MESSAGE RECEIVED: " + ctx.message());  // ← ADD THIS
                webSocketHandler.handleMessage(ctx);  // Then call your handler
            });
            ws.onClose(webSocketHandler);
        });
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
                LoginResult registerResult = userService.register(userData);
                ctx.json(serializer.toJson(registerResult));
                ctx.status(200);
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).json(str);
            } catch (AlreadyTakenException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(403).json(str);
            }
        }

        private void login (Context ctx){
            var serializer = new Gson();
            LoginData loginData;
            try {
                loginData = serializer.fromJson(ctx.body(), LoginData.class);
            } catch (Exception e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(400).json(str);
                return;
            }
            if (loginData.username() == null || loginData.username().isEmpty() ||
                    loginData.password() == null || loginData.password().isEmpty()) {
                ctx.status(400).json("{\"message\": \"Error: bad request\"}");
                return;
            }

            try {
                LoginResult registerResult = userService.login(loginData);
                String str = String.format("{\"username\":\"%s\", \"authToken\":\"%s\"}",
                        registerResult.username(), registerResult.authToken());
                ctx.status(200).result(str);
            } catch (UnauthorizedException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(401).result(str);
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
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(401).result(str);
            }
        }

        private void listGames (Context ctx){
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).result("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            try {
                List<ListGameResult> games = gameService.listGames(authToken);
                String str = String.format("{\"games\": %s}", new Gson().toJson(games));
                ctx.status(200).result(str);
            } catch (UnauthorizedException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(401).result(str);
            } catch (DataAccessException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(500).result(str);
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
                String str = String.format("{\"message\": \"Error: %s\"}", e);
                ctx.status(400).json(str);
                return;
            }
            if (gameName == null || gameName.isEmpty()) {
                String str = "{\"message\": \"Error: bad request\"}";
                ctx.status(400).json(str);
                return;
            }
            CreateGameData createGameData = new CreateGameData(gameName);
            try {
                int gameID = gameService.createGame(authToken, createGameData);
                String str = String.format("{\"gameID\": %d}", gameID);
                ctx.status(200).json(str);
            } catch (UnauthorizedException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(401).result(str);
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
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(400).json(str);
                return;
            }
            if (joinGameData == null || joinGameData.gameID() <=0 || joinGameData.playerColor() == null) {
                String str = "{\"message\": \"Error: bad request\"}";
                ctx.status(400).json(str);
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
            } catch (AlreadyTakenException e) {
                String str = String.format("{\"message\": \"Error: (%s)\"}", e);
                ctx.status(403).json(str);
            } catch (Exception e) {
                String str = "{\"message\": \"Error: internal server error\"}";
                ctx.status(500).json(str);
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
