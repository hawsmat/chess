package dataaccess;

import chess.ChessGame;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {
    MySqlDataAccess mySqlDataAccess;
    @BeforeEach
    public void createDataBase(){
        try {
            this.mySqlDataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    public void clearDataBase(){
        try {
            mySqlDataAccess.clearUserData();
            mySqlDataAccess.clearGameData();
            mySqlDataAccess.clearAuthData();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void clearUserDataSuccess() {
        assertDoesNotThrow(()->mySqlDataAccess.clearUserData());
    }

    @Test
    void createUserSuccess() {
        assertDoesNotThrow(()->mySqlDataAccess.createUser(new UserData("matt", "password", "email")));
    }

    @Test
    void createUserFailSameUser() {
        UserData user = new UserData("matt", "pass", "email");
        assertDoesNotThrow(()->mySqlDataAccess.createUser(user));
        assertThrows(DataAccessException.class, ()-> mySqlDataAccess.createUser(user));
    }

    @Test
    void createUserFailNullUser() {
        UserData user = new UserData(null, null, "email");
        assertThrows(DataAccessException.class, ()-> mySqlDataAccess.createUser(user));
    }


    @Test
    void getUserSuccess() {
        UserData user = new UserData("matt", "password", "email");
        assertDoesNotThrow(()->mySqlDataAccess.createUser(user));
        assertEquals(user.password(), assertDoesNotThrow(()->mySqlDataAccess.getUser(user.username()).password()));
    }

    @Test
    void getUserFailureNotExists() {
        UserData user = new UserData("matt", "password", "email");
        assertDoesNotThrow(()->mySqlDataAccess.createUser(user));
        assertNull(assertDoesNotThrow(()->mySqlDataAccess.getAuthData("joe")));
    }

    @Test
    void addAuthDataSuccess() {
        RegisterResult registerResult = new RegisterResult("token", "username");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(registerResult));
    }

    @Test
    void addAuthDataFailureDuplicate() {
        RegisterResult registerResult = new RegisterResult("token", "username");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(registerResult));
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.addAuthData(registerResult));
    }

    @Test
    void addAuthDataFailureNull() {
        RegisterResult registerResult = new RegisterResult(null, null);
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.addAuthData(registerResult));
    }

    @Test
    void getAuthDataSuccess() {
        RegisterResult registerResult = new RegisterResult("token", "username");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(registerResult));
        assertEquals(registerResult, assertDoesNotThrow(()->mySqlDataAccess.getAuthData(registerResult.authToken())));
    }

    @Test
    void getAuthDataFailureNotExist() {
        assertNull(assertDoesNotThrow(()->mySqlDataAccess.getAuthData("token")));
    }

    @Test
    void clearAuthData() {
        assertDoesNotThrow(()->mySqlDataAccess.clearAuthData());
    }

    @Test
    void deleteAuthDataSuccess() {
        RegisterResult registerResult = new RegisterResult("token", "user");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(registerResult));
        assertDoesNotThrow(()->mySqlDataAccess.deleteAuthData(registerResult.authToken()));
    }

    @Test
    void deleteAuthDataFailureNotExist() {
        assertDoesNotThrow(()->mySqlDataAccess.deleteAuthData("token"));
    }

    @Test
    void addGameSuccess() {
        assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
    }

    @Test
    void addGameFailureNull() {
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.addGame(null));
    }

    @Test
    void getGameSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.getGame(id));
    }

    @Test
    void getGameFailureNotExist() {
        assertNull(assertDoesNotThrow(()->mySqlDataAccess.getGame(2)));
    }

    @Test
    void getGamesSuccess() {
        assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        Set<Integer> games = assertDoesNotThrow(()->mySqlDataAccess.getGames());
        assertNotEquals(0, games.size());
    }

    @Test
    void getGamesFailureNotExist() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE gamedata";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (Exception e) {}
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getGames());
    }

    @Test
    void updateUsernamesSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.WHITE, "matt"));
    }

    @Test
    void updateUsernamesGameNotExist() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE gamedata";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (Exception e) {}
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.updateUsernames(1, ChessGame.TeamColor.WHITE, "matt"));
    }

    @Test
    void getGameDatasSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.WHITE, "matt"));
        assertDoesNotThrow(()->mySqlDataAccess.getWhiteUsername(id));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.BLACK, "joe"));
        assertDoesNotThrow(()->mySqlDataAccess.getBlackUsername(id));
        assertDoesNotThrow(()->mySqlDataAccess.getGameName(id));
        assertDoesNotThrow(()->mySqlDataAccess.getChessGame(id));
    }

    @Test
    void getGameDatasFailureNotExist() {
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getWhiteUsername(1));
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getBlackUsername(1));
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getGameName(1));
        assertNull(assertDoesNotThrow(()->mySqlDataAccess.getChessGame(1)));
    }

    @Test
    void clearGameData() {
        assertDoesNotThrow(()->mySqlDataAccess.clearGameData());
    }

    @Test
    void isAuthorizedSuccess() {
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(new RegisterResult("token", "matt")));
        assertTrue(assertDoesNotThrow(()->mySqlDataAccess.isAuthorized("token")));
    }

    @Test
    void isAuthorizedFailureNotExist() {
        assertFalse(assertDoesNotThrow(()->mySqlDataAccess.isAuthorized("token")));
    }
}