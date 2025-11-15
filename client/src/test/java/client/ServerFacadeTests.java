package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
     void clearServer() {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerSuccess() {
        UserData userData = new UserData("user", "pass", "email");
        assertDoesNotThrow(()->serverFacade.register(userData));
    }

    @Test
     void registerFailureAlreadyExists() {
        UserData userData = new UserData("user", "pass", "email");
        assertDoesNotThrow(()->serverFacade.register(userData));
        assertThrows(Exception.class, ()->serverFacade.register(userData));
    }

    @Test
     void loginSuccess() {
        UserData userData = new UserData("user", "pass", "email");
        assertDoesNotThrow(()->serverFacade.register(userData));
        LoginData loginData = new LoginData(userData.username(), userData.password());
        assertDoesNotThrow(()->serverFacade.login(loginData));
    }

    @Test
     void loginFailureDoesNotExist() {
        assertThrows(Exception.class, ()->serverFacade.login(new LoginData("user", "pass")));
    }

    @Test
     void logoutSuccess() {
        UserData userData = new UserData("user", "pass", "email");
        LoginResult loginResult = assertDoesNotThrow(()->serverFacade.register(userData));
        assertDoesNotThrow(()->serverFacade.logout(loginResult.authToken()));
    }

    @Test
     void logoutFailureNotExist() {
        assertThrows(Exception.class, ()-> serverFacade.logout("string"));
    }

    @Test
     void createGameSuccess() {
        UserData userData = new UserData("user", "pass", "email");
        LoginResult loginResult = assertDoesNotThrow(()->serverFacade.register(userData));
        LoginData loginData = new LoginData(userData.username(), userData.password());
        assertDoesNotThrow(()->serverFacade.login(loginData));
        assertDoesNotThrow(()->serverFacade.createGame(loginResult.authToken(), new CreateGameData("a")));
    }

    @Test
     void createGameFailureNotAuthorized() {
        assertThrows(Exception.class, ()->serverFacade.createGame("authtoken", new CreateGameData("name")));
    }

    @Test
     void listGamesSuccess() {
        UserData userData = new UserData("user", "pass", "email");
        LoginResult loginResult = assertDoesNotThrow(()->serverFacade.register(userData));
        LoginData loginData = new LoginData(userData.username(), userData.password());
        assertDoesNotThrow(()->serverFacade.login(loginData));
        assertDoesNotThrow(()->serverFacade.createGame(loginResult.authToken(), new CreateGameData("name")));
        assertDoesNotThrow(()->serverFacade.createGame(loginResult.authToken(), new CreateGameData("game")));
        assertDoesNotThrow(()->serverFacade.listGames(loginResult.authToken()));

    }

    @Test
     void listGamesFailureNotAuthorized() {
        assertThrows(Exception.class, ()->serverFacade.listGames("auth"));
    }

    @Test
     void joinGameSuccess() {
        UserData userData = new UserData("user", "pass", "email");
        LoginResult loginResult = assertDoesNotThrow(()->serverFacade.register(userData));
        LoginData loginData = new LoginData(userData.username(), userData.password());
        LoginResult newLoginResult = assertDoesNotThrow(()->serverFacade.login(loginData));
        CreateGameData createGameData = new CreateGameData("name");
        Integer gameID = (assertDoesNotThrow(()->serverFacade.createGame(newLoginResult.authToken(), createGameData)));
        assertDoesNotThrow(()->serverFacade.join(newLoginResult.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }

    @Test
     void joinGameFailureNotExist() {
        UserData userData = new UserData("user", "pass", "email");
        LoginResult loginResult = assertDoesNotThrow(()->serverFacade.register(userData));
        LoginData loginData = new LoginData(userData.username(), userData.password());
        assertDoesNotThrow(()->serverFacade.login(loginData));
        assertThrows(Exception.class, ()->serverFacade.join(loginResult.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, 1)));
    }

}

