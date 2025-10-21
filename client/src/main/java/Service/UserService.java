package Service;

import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private UserDataAccess dataAccess;
    public UserService(UserDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(user.username(), authToken);
    }
}
