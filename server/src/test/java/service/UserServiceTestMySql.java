package service;

import dataaccess.*;
import model.RegisterResult;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTestMySql {
    MySqlDataAccess mySqlDataAccess;

    @BeforeEach
    void createDatabase(){
        try {
            this.mySqlDataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {

        }
    }

    @AfterEach
    void deleteDatabase(){
        AdminService adminService = new AdminService(mySqlDataAccess);
        try {
            adminService.clear();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    void registerSuccess() {
        try {
            mySqlDataAccess.clearUserData();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            assertDoesNotThrow(() -> userService.register(user));
        } catch (DataAccessException e) {


        }
    }

    @Test
    void registerWithSameUsername() throws DataAccessException {
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            UserData user2 = new UserData("matt", "joe", "email");
            userService.register(user);
            assertThrows(AlreadyTakenException.class, () -> userService.register(user2));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void loginSuccess() {
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            assertDoesNotThrow(() -> userService.register(user));
            LoginData login = new LoginData(user.username(), user.password());
            assertDoesNotThrow(() -> userService.login(login));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void loginFailureWrongUsername(){
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            assertDoesNotThrow(() -> userService.register(user));
            LoginData wrongLogin = new LoginData("username", user.password());
            assertThrows(UnauthorizedException.class, () -> userService.login(wrongLogin));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void loginFailureWrongPassword(){
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            assertDoesNotThrow(() -> userService.register(user));
            LoginData login = new LoginData(user.username(), "matt");
            assertThrows(UnauthorizedException.class, () -> userService.login(login));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void logoutSuccess() {
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            RegisterResult registerResult = assertDoesNotThrow(() -> userService.register(user));
            assertDoesNotThrow(() -> userService.logout(registerResult.authToken()));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void logoutFailureWrongAuthToken() {
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            assertThrows(UnauthorizedException.class, () -> userService.logout("a"));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void logoutFailureLogoutTwice() {
        try {
            MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
            UserService userService = new UserService(mySqlDataAccess);
            UserData user = new UserData("matt", "joe", "email");
            RegisterResult registerResult = assertDoesNotThrow(() -> userService.register(user));
            assertDoesNotThrow(() -> userService.logout(registerResult.authToken()));
            assertThrows(UnauthorizedException.class, () -> userService.logout(registerResult.authToken()));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }
}