package Service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;

import java.security.Provider;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    @Test
    void clear() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        UserService userService = new UserService(memoryDataAccess);
        GameService gameService = new GameService(memoryDataAccess);
        AdminService adminService = new AdminService(memoryDataAccess);


    }
}