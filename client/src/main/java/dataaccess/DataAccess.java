package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Set;

public interface DataAccess {
    void clearUserData();
    void createUser(UserData user);
    UserData getUser(String username);
    void deleteUserData(String username);
    AuthData createAuthData(String username);
    AuthData getAuthData(String username);
    void clearAuthData();
    void deleteAuthData(String username);
    int addGame(String gameName);
    GameData getGame(int gameID);
    Set<Integer> getGames();
    void updateGame(int gameID, ChessGame.TeamColor teamColor, String username);
    String getWhiteUsername(int gameID);
    String getBlackUsername(int GameID);
    String getGameName(int GameID);
    ChessGame getChessGame(int gameID);
    void clearGameData();
    boolean isAuthorized(String authToken);
}
