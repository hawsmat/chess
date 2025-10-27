package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.LoginData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {
    private MemoryDataAccess memoryDataAccess;
    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public AuthData register(UserData user) throws AlreadyTakenException, DataAccessException {
        if (memoryDataAccess.getUser(user.username()) == null) {
            UserData newUser = new UserData(user.username(),BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
            memoryDataAccess.createUser(newUser);
            return memoryDataAccess.createAuthData(newUser.username());
        } else {
            throw new AlreadyTakenException("username already exists");
        }
    }

    public AuthData login(LoginData loginData) throws UnauthorizedException, DataAccessException {
        if (memoryDataAccess.getUser(loginData.username()) == null) {
            throw new UnauthorizedException("username does not exist");
        }
        if (!BCrypt.checkpw(loginData.password(), memoryDataAccess.getUser(loginData.username()).password())) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserService that = (UserService) o;
        return Objects.equals(memoryDataAccess, that.memoryDataAccess);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(memoryDataAccess);
    }
}
