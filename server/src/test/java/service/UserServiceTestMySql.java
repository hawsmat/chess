package service;

import dataaccess.*;
import model.AuthData;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTestMySql {

    @Test
    void registerSuccess() {
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            assertDoesNotThrow(() -> userService.register(user));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    void registerWithSameUsername() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        UserData user2 = new UserData("matt", "joe", "email");
        userService.register(user);
        assertThrows(AlreadyTakenException.class, () -> userService.register(user2));
    }

    @Test
    void loginSuccess() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        assertDoesNotThrow(() -> userService.register(user));
        LoginData login = new LoginData(user.username(), user.password());
        assertDoesNotThrow(() -> userService.login(login));
    }

    @Test
    void loginFailureWrongUsername(){
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        assertDoesNotThrow(() -> userService.register(user));
        LoginData wrongLogin = new LoginData("username", user.password());
        assertThrows(UnauthorizedException.class, () -> userService.login(wrongLogin));
    }

    @Test
    void loginFailureWrongPassword(){
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        assertDoesNotThrow(() -> userService.register(user));
        LoginData login = new LoginData(user.username(), "matt");
        assertThrows(UnauthorizedException.class, () -> userService.login(login));
    }

    @Test
    void logoutSuccess() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(() -> userService.logout(authData.authToken()));
    }

    @Test
    void logoutFailureWrongAuthToken() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        assertThrows(UnauthorizedException.class, () -> userService.logout("a"));
    }

    @Test
    void logoutFailureLogoutTwice() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        UserData user = new UserData("matt", "joe", "email");
        AuthData authData = assertDoesNotThrow(() -> userService.register(user));
        assertDoesNotThrow(() -> userService.logout(authData.authToken()));
        assertThrows(UnauthorizedException.class, ()-> userService.logout(authData.authToken()));
    }
}