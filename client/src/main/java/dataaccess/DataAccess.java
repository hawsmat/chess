package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {
    void clearUserData();
    void createUser(UserData user);
    UserData getUser(String username);
    void deleteUserData(String username);
    AuthData createAuthData(String username);
    AuthData getAuthData(String username);
    void clearAuthData();
    void deleteAuthData(String username);
    int createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame();
    void clearGameData();
    void clear();
}
