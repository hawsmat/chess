package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
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
        AuthData authData = new AuthData("token", "username");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(authData));
    }

    @Test
    void addAuthDataFailureDuplicate() {
        AuthData authData = new AuthData("token", "username");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(authData));
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.addAuthData(authData));
    }

    @Test
    void addAuthDataFailureNull() {
        AuthData authData = new AuthData(null, null);
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.addAuthData(authData));
    }

    @Test
    void getAuthDataSuccess() {
        AuthData authData = new AuthData("token", "username");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(authData));
        assertEquals(authData, assertDoesNotThrow(()->mySqlDataAccess.getAuthData(authData.authToken())));
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
        AuthData authData = new AuthData("token", "user");
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(authData));
        assertDoesNotThrow(()->mySqlDataAccess.deleteAuthData(authData.authToken()));
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
    void getWhiteUsernameSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.WHITE, "matt"));
        assertDoesNotThrow(()->mySqlDataAccess.getWhiteUsername(id));
    }

    @Test
    void getWhiteUsernameFailureNotExist() {
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getWhiteUsername(1));
    }

    @Test
    void getBlackUsernameSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.WHITE, "matt"));
        assertDoesNotThrow(()->mySqlDataAccess.getWhiteUsername(id));
    }

    @Test
    void getBlackUsernameFailureNotExist() {
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getWhiteUsername(1));
    }

    @Test
    void getGameNameSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.WHITE, "matt"));
        assertDoesNotThrow(()->mySqlDataAccess.getWhiteUsername(id));
    }

    @Test
    void getGameNameFailureNotExist() {
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getWhiteUsername(1));
    }

    @Test
    void getChessGameSuccess() {
        int id = assertDoesNotThrow(()->mySqlDataAccess.addGame("name"));
        assertDoesNotThrow(()->mySqlDataAccess.updateUsernames(id, ChessGame.TeamColor.WHITE, "matt"));
        assertDoesNotThrow(()->mySqlDataAccess.getWhiteUsername(id));
    }

    @Test
    void getChessGameFailureNotExist() {
        assertThrows(DataAccessException.class, ()->mySqlDataAccess.getWhiteUsername(1));
    }

    @Test
    void clearGameData() {
        assertDoesNotThrow(()->mySqlDataAccess.clearGameData());
    }

    @Test
    void isAuthorizedSuccess() {
        assertDoesNotThrow(()->mySqlDataAccess.addAuthData(new AuthData("token", "matt")));
        assertTrue(assertDoesNotThrow(()->mySqlDataAccess.isAuthorized("token")));
    }

    @Test
    void isAuthorizedFailureNotExist() {
        assertFalse(assertDoesNotThrow(()->mySqlDataAccess.isAuthorized("token")));
    }
}