package dataaccess;

import org.junit.jupiter.api.Test;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDataAccessTest {

    @Test
    void clear() {
        var user = new UserData("joe", "j@j", "j");
        MemoryDataAccess da = new MemoryDataAccess();
        da.createUser(user);
        assertNotNull(da.getUser(user.username()));
        da.clearUserData();
        assertNull(da.getUser(user.username()));
    }

    @Test
    void CreateUser() {
        var user = new UserData("joe", "j@j", "j");
        MemoryDataAccess da = new MemoryDataAccess();
        da.createUser(user);
        assertEquals(user.username(), da.getUser(user.username()).username());
    }

    @Test
    void getUser() {

    }
}