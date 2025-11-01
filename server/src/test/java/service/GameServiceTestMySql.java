package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.CreateGameData;
import model.JoinGameData;
import model.UserData;
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
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
    }

    @Test
    void createGameNotAuthorized() {
        assertThrows(UnauthorizedException.class, ()-> gameService.createGame(new CreateGameData("a", "game")));
    }

    @Test
    void listGames() {
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
        assertDoesNotThrow(()->gameService.listGames(authData.authToken()));
    }

    @Test
    void listGamesNoGames() {
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(()->gameService.listGames(authData.authToken()));
    }

    @Test
    void joinGameSuccess() {
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
        assertDoesNotThrow(()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }

    @Test
    void joinGameGameDoesntExist() {
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertThrows(UnauthorizedException.class, ()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, 1)));
    }

    @Test
    void joinGameColorAlreadyTaken() {
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        int gameID = assertDoesNotThrow(()-> gameService.createGame(new CreateGameData(authData.authToken(), "game")));
        assertDoesNotThrow(()-> gameService.joinGame(authData.authToken(), new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
        assertThrows(AlreadyTakenException.class, ()-> gameService.joinGame(authData.authToken(),
                new JoinGameData(ChessGame.TeamColor.WHITE, gameID)));
    }
}