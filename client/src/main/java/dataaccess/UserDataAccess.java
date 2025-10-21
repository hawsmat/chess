package dataaccess;

import model.UserData;
import model.AuthData

import java.util.HashMap;

public class UserDataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> authData = new HashMap<>();

    public void clear() {
        users.clear();
    }

    public void createUser(UserData user) {
        if (users.get(user.username()) != null) {
            throw new DataAccessException("username already exists");
        }
        else {
            users.put(user.username(), user);
        }
    }

    public UserData getUser(String username) {
        return users.get(username);
    }
}
