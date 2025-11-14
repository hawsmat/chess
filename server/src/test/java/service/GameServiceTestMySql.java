package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameServiceTestMySql {
    MySqlDataAccess mySqlDataAccess;
    UserService userService;
    GameService gameService;

    @BeforeEach
    void createDatabase(){
        try {
            this.mySqlDataAccess = new MySqlDataAccess();
            this.userService = new UserService(mySqlDataAccess);
            this.gameService = new GameService(mySqlDataAccess);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    void deleteDatabase(){
        AdminService adminService = new AdminService(mySqlDataAccess);
        try {
            adminService.clear();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void createGame() {
        UserData user = new UserData("matt", "joe", "email");
        LoginResult registerResult = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(registerResult.authToken(), new CreateGameData("game")));
    }

    @Test
    void createGameNotAuthorized() {
        assertThrows(UnauthorizedException.class, ()-> gameService.createGame("a", new CreateGameData("game")));
    }

    @Test
    void listGames() {
        UserData user = new UserData("matt", "joe", "email");
        LoginResult registerResult = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(registerResult.authToken(),  new CreateGameData("game")));
        assertDoesNotThrow(()-> gameService.createGame(registerResult.authToken(), new CreateGameData("hello")));
        System.out.println(assertDoesNotThrow(()->gameService.listGames(registerResult.authToken())));
    }

    @Test
    void listGamesNoGames() {
        UserData user = new UserData("matt", "joe", "email");
        LoginResult registerResult = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()->gameService.listGames(registerResult.authToken()));
    }

    @Test
    void joinGameSuccess() {
        UserData user = new UserData("matt", "joe", "email");
        LoginResult loginResult = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(loginResult.authToken(), new CreateGameData("game")));
        assertDoesNotThrow(()-> gameService.joinGame(loginResult.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }

    @Test
    void joinGameGameDoesntExist() {
        UserData user = new UserData("matt", "joe", "email");
        LoginResult registerResult = assertDoesNotThrow(() -> userService.register(user));
        assertThrows(UnauthorizedException.class, ()-> gameService.joinGame(registerResult.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, 1)));
    }

    @Test
    void joinGameColorAlreadyTaken() {
        UserData user = new UserData("matt", "joe", "email");
        LoginResult registerResult = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(registerResult.authToken(), new CreateGameData("game")));
        assertDoesNotThrow(()-> gameService.joinGame(registerResult.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
        assertThrows(AlreadyTakenException.class, ()-> gameService.joinGame(registerResult.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }
}