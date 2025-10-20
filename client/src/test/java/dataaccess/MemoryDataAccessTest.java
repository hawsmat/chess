package dataaccess;

import org.junit.jupiter.api.Test;
import datamodel.User;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDataAccessTest {

    @Test
    void clear() {
        var user = new User("joe", "j@j", "j");
        DataAccess da = new MemoryDataAccess();
        da.createUser(user);
        assertNotNull(da.getUser(user.username()));
        da.clear();
        assertNull(da.getUser(user.username()));
    }

    @Test
    void CreateUser() {

    }

    @Test
    void getUser() {

    }
}