package Service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.LoginData;
import model.UserData;

import javax.xml.crypto.Data;
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
    public AuthData login(LoginData loginData) throws DataAccessException {
        if (userDataAccess.getUser(loginData.username()) == null) {
            throw new DataAccessException("username does not exist");
        }
        if (!loginData.password().equals(userDataAccess.getUser(loginData.username()).password())) {
            throw new DataAccessException("incorrect password");
        }
        return authDataAccess.createAuthData(loginData.username());
    }

    public void logout(AuthData authData) throws DataAccessException {
        if (authDataAccess.getAuthData(authData.username()) == null) {
            throw new DataAccessException("incorrect authData");
        }
        authDataAccess.delete(authData.username());
    }
}
