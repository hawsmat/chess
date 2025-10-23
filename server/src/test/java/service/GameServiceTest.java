package service;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.CreateGameData;
import model.JoinGameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameServiceTest {

    @Test
    void createGame() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
    }

    @Test
    void createGameNotAuthorized() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        assertThrows(UnauthorizedException.class, ()-> gameService.createGame(new CreateGameData("a", "game")));
    }

    @Test
    void listGames() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
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
        int gameID = assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
        assertDoesNotThrow(()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }

    @Test
    void joinGameGameDoesntExist() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertThrows(UnauthorizedException.class, ()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, 1)));
    }

    @Test
    void joinGameColorAlreadyTaken() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
        assertDoesNotThrow(()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
        assertThrows(AlreadyTakenException.class, ()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }
}