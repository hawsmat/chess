package Service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.LoginData;
import model.UserData;

public class UserService {
    private MemoryDataAccess memoryDataAccess;
    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public AuthData register(UserData user) throws AlreadyTakenException, DataAccessException {
        if (memoryDataAccess.getUser(user.username()) == null) {
            memoryDataAccess.createUser(user);
            return memoryDataAccess.createAuthData(user.username());
        }
        else {
            throw new AlreadyTakenException("username already exists");
        }
    }
    public AuthData login(LoginData loginData) throws UnauthorizedException, DataAccessException {
        if (memoryDataAccess.getUser(loginData.username()) == null) {
            throw new DataAccessException("username does not exist");
        }
        if (!loginData.password().equals(memoryDataAccess.getUser(loginData.username()).password())) {
            throw new UnauthorizedException("incorrect password");
        }
        return memoryDataAccess.createAuthData(loginData.username());
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        if (!memoryDataAccess.isAuthorized(authToken)) {
            throw new UnauthorizedException("incorrect authToken");
        }
        memoryDataAccess.deleteAuthData(authToken);
    }
}
