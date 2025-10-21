package Service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.LoginData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private UserDataAccess userDataAccess;
    private AuthDataAccess authDataAccess;
    public UserService(UserDataAccess dataAccess) {
        this.userDataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (userDataAccess.getUser(user.username()) == null) {
            userDataAccess.createUser(user);
            return authDataAccess.createAuthData(user.username());
        }
        else {
            throw new DataAccessException("username already exists");
        }
    }
    public AuthData login(LoginData) {

    }
}
