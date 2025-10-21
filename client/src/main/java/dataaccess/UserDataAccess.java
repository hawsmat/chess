package dataaccess;

import model.UserData;
import model.AuthData;

import java.util.HashMap;

public class UserDataAccess {
    private HashMap<String, UserData> users = new HashMap<>();

    public void clear() {
        users.clear();
    }

    public void createUser(UserData user){
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void delete(String username) {
        users.remove(username);
    }
}
