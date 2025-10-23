package dataaccess;

import org.junit.jupiter.api.Test;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDataAccessTest {
    @Test
    void addgame() {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        int ID = memoryDataAccess.addGame("name");
        System.out.println(memoryDataAccess.getGame(ID));
    }
}