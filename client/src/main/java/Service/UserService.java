package Service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.LoginData;
import model.UserData;

public class UserService {
    private MemoryDataAccess memoryDataAccess;
    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (memoryDataAccess.getUser(user.username()) == null) {
            memoryDataAccess.createUser(user);
            return memoryDataAccess.createAuthData(user.username());
        }
        else {
            throw new DataAccessException("username already exists");
        }
    }
    public AuthData login(LoginData loginData) throws DataAccessException {
        if (memoryDataAccess.getUser(loginData.username()) == null) {
            throw new DataAccessException("username does not exist");
        }
        if (!loginData.password().equals(memoryDataAccess.getUser(loginData.username()).password())) {
            throw new DataAccessException("incorrect password");
        }
        return memoryDataAccess.createAuthData(loginData.username());
    }

    public void logout(String authToken) throws DataAccessException {
        if (!isAuthorized(authToken)) {
            throw new DataAccessException("incorrect authToken");
        }
        memoryDataAccess.deleteAuthData(authToken);
    }

    public boolean isAuthorized(String authToken) {
        return (memoryDataAccess.getAuthData(authToken) != null);
    }
}
