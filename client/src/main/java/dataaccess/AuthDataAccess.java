package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDataAccess {
    private HashMap<String, AuthData> authDatas = new HashMap<>();

    public AuthData createAuthData(String username){
        String authToken = UUID.randomUUID().toString();
        while (true) {
            if (!authDatas.containsValue(authToken)) {
                break;
            }
        }
        AuthData authData = new AuthData(username, authToken);
        authDatas.put(username, authData);
        return authData;
    }
    public AuthData getAuthData(String username) {
        return authDatas.get(username);
    }

    public void clear() {
        authDatas.clear();
    }
}
