package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.util.Set;

public interface DataAccess {
    void clearUserData() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    LoginData getUser(String username) throws DataAccessException;
    void addAuthData(AuthData authData) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    void clearAuthData() throws DataAccessException;
    void deleteAuthData(String username) throws DataAccessException;
    int addGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Set<Integer> getGames() throws DataAccessException;
    void updateUsernames(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException;
    String getWhiteUsername(int gameID) throws DataAccessException;
    String getBlackUsername(int gameID) throws DataAccessException;
    String getGameName(int gameID) throws DataAccessException;
    ChessGame getChessGame(int gameID) throws DataAccessException;
    void clearGameData() throws DataAccessException;
    boolean isAuthorized(String authToken) throws DataAccessException;
}
