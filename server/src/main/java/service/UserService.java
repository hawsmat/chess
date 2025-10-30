package service;

import dataaccess.*;
import model.AuthData;
import model.LoginData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws AlreadyTakenException, DataAccessException {
        if (dataAccess.getUser(user.username()) == null) {
            UserData newUser = new UserData(user.username(),BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
            dataAccess.createUser(newUser);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, user.username());
            dataAccess.addAuthData(authData);
            return authData;
        } else {
            throw new AlreadyTakenException("username already exists");
        }
    }

    public AuthData login(LoginData loginData) throws UnauthorizedException, DataAccessException {
        if (dataAccess.getUser(loginData.username()) == null) {
            throw new UnauthorizedException("username does not exist");
        }
        if (!BCrypt.checkpw(loginData.password(), dataAccess.getUser(loginData.username()).password())) {
            throw new UnauthorizedException("incorrect password");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, loginData.username());
        dataAccess.addAuthData(authData);
        return authData;
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
            if (!dataAccess.isAuthorized(authToken)) {
                throw new UnauthorizedException("incorrect authToken");
            }
            dataAccess.deleteAuthData(authToken);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserService that = (UserService) o;
        return Objects.equals(dataAccess, that.dataAccess);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dataAccess);
    }
}
