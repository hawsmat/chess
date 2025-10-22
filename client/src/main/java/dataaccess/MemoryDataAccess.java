package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<Integer, GameData> gameIDs = new HashMap<>();
    int gameID = 0;
    private HashMap<String, AuthData> authDatas = new HashMap<>();

    @Override
    public void clearUserData() {users.clear();}

    @Override
    public void createUser(UserData user) {users.put(user.username(), user);}

    @Override
    public UserData getUser(String username) {return users.get(username);}

    @Override
    public void deleteUserData(String username) {users.remove(username);}

    @Override
    public AuthData createAuthData(String username) {
        String authToken = UUID.randomUUID().toString();
        while (true) {
            if (!authDatas.containsKey(authToken)) {
                break;
            }
        }
        AuthData authData = new AuthData(username, authToken);
        authDatas.put(authToken, authData);
        return authData;
    }

    @Override
    public AuthData getAuthData(String authToken) {return authDatas.get(authToken);}

    @Override
    public void clearAuthData() {authDatas.clear();}

    @Override
    public void deleteAuthData(String authToken) {authDatas.remove(authToken);}

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {return gameIDs.get(gameID);}

    @Override
    public void updateGame() {}

    @Override
    public void clearGameData() {gameIDs.clear();}

    @Override
    public void clear(){
        clearAuthData();
        clearGameData();
        clearUserData();
    }
}
