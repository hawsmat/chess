package Service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void createGame() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(authData.authToken(), "game"));
    }

    @Test
    void createGameNotAuthorized() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        assertThrows(DataAccessException.class, ()-> gameService.createGame("a", "game"));
    }

    @Test
    void listGames() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(authData.authToken(), "game"));
        assertDoesNotThrow(()->gameService.listGames(authData.authToken()));
    }

    @Test
    void listGamesNoGames() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()->gameService.listGames(authData.authToken()));
    }

    @Test
    void joinGameSuccess() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(authData.authToken(), "game"));
        assertDoesNotThrow(()-> gameService.joinGame(authData.authToken(), ChessGame.TeamColor.WHITE, gameID));
    }

    @Test
    void joinGameGameDoesntExist() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertThrows(DataAccessException.class, ()-> gameService.joinGame(authData.authToken(), ChessGame.TeamColor.WHITE, 1));
    }

    @Test
    void joinGameColorAlreadyTaken() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(authData.authToken(), "game"));
        assertDoesNotThrow(()-> gameService.joinGame(authData.authToken(), ChessGame.TeamColor.WHITE, gameID));
        assertThrows(DataAccessException.class, ()-> gameService.joinGame(authData.authToken(), ChessGame.TeamColor.WHITE, gameID));
    }
}